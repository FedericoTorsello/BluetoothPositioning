package it.unibo.torsello.bluetoothpositioning.utils;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.constants.KalmanFilterConstansts;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter1;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter2;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter3;

/**
 * A helper class
 *
 * @author federico
 */
public class BeaconStatisticsUtil {

    private DescriptiveStatistics recentRSSI;
    private DescriptiveStatistics recentTxPower;
    private KalmanFilter3 kf;
    private double lastCalculatedDistance;
    private double lastRawDistance;
    private double lastWOSC;
    private double dist;
    private double distInMetresKalmanFilter1;
    private double distInMetresKalmanFilter2;
    private double distInternet;

    public BeaconStatisticsUtil() {

        // limit on the number of values that can be stored in the dataset
        recentRSSI = new DescriptiveStatistics();
        recentRSSI.setWindowSize(KalmanFilterConstansts.WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(KalmanFilterConstansts.WINDOW);

        //Sulla base della distanza costante dal beacon, cioè, la bassa interferenza dal sistema (R), la misura di alta interferenza (Q)
        // I valori dovrebbero essere basati su misurazioni statistiche attuali, quindi,
        // il filtro (measuredValue) restituisce il valore calcolato

        //1-dimensional Kalman filter with predefined
        double R = KalmanFilterConstansts.R; // Initial process noise
        double Q = KalmanFilterConstansts.Q; // Initial measurement noise
        double A = KalmanFilterConstansts.A;
        double B = KalmanFilterConstansts.B;
        double C = KalmanFilterConstansts.C;

        kf = KalmanFilter3.getInstance();
        kf.build(R, Q, A, B, C);

    }

    public void updateDistance(Beacon b, double processNoise, int movementState) {

        recentRSSI.addValue(b.getRssi());
        recentTxPower.addValue(b.getTxPower());

        // Update measurement noise continually
        double mNoise = Math.sqrt((100 * 9 / Math.log(10)) *
                Math.log(1 + Math.pow(recentRSSI.getMean() / recentRSSI.getStandardDeviation(), 2)));

        if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
            kf.setMeasurementNoise(mNoise);
        }
        kf.setProcessNoise(processNoise);

        lastRawDistance = calculateDistanceNoFilter1(b.getTxPower(), b.getRssi());
        double lastFilteredReading = kf.filter(recentRSSI.getPercentile(50), movementState);
        lastCalculatedDistance = calculateDistanceNoFilter1(recentTxPower.getPercentile(50), lastFilteredReading);

        double lastFilteredReading1 = kf.filter(recentTxPower.getPercentile(50), movementState);
        lastWOSC = calculateDistanceNoFilter1(lastFilteredReading1, lastFilteredReading);

        dist = calculateDistanceNoFilter2(b.getRssi(), b.getTxPower());
        distInternet = calculateDistanceNoFilter3(b.getRssi(), b.getTxPower());

        distInMetresKalmanFilter1 = calculateDistanceKalmanFilter1(b.getRssi(), b.getTxPower(), b.getBluetoothAddress());
        distInMetresKalmanFilter2 = calculateDistanceKalmanFilter2(b.getRssi(), b.getTxPower());
    }

    public double getDist() {
        return dist;
    }

    public double getRawDistance() {
        return lastRawDistance;
    }

    public double getDistInternet() {
        return distInternet;
    }

    public double getDistKalmanFilter1() {
        return distInMetresKalmanFilter1;
    }

    public double getDistKalmanFilter2() {
        return distInMetresKalmanFilter2;
    }

    public double getDistKalmanFilter3() {
        return lastCalculatedDistance;
    }

    public double getDistKalmanFilter4() {
        return lastWOSC;
    }

    private double calculateDistanceNoFilter1(double txPower, double rssi) {
        double n = 2.0;   // Signal propogation exponent
        double d0 = 1;  // Reference distance in meters
        double C = 0;   // Gaussian variable for mitigating flat fading

        // model specific adjustments for Samsung S3 as per Android Beacon Library
        double mReceiverRssiSlope = 0;
        double mReceiverRssiOffset = -2;

        // calculation of adjustment
        double adjustment = mReceiverRssiSlope * rssi + mReceiverRssiOffset;
        double adjustedRssi = rssi - adjustment;

        // Log-distance path loss model
        return d0 * Math.pow(10.0, (adjustedRssi - txPower - C) / (-10 * n));
    }

    private double calculateDistanceNoFilter2(double rssi, int txPower) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        final double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    private double calculateDistanceNoFilter3(double rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10D, ((double) txPower - rssi) / (10 * 2));
    }

    private double calculateDistanceKalmanFilter1(double rssi, int txPower, String bluetoothAddress) {

        KalmanFilter1 kalmanFilter = KalmanFilter1.getInstance();
        double accuracy = kalmanFilter.filter(rssi, bluetoothAddress);
        double newDistanceInMeters = calculateDistanceStandard(accuracy, txPower);

        double previousAccuracy = distInMetresKalmanFilter2;
        newDistanceInMeters = previousAccuracy * (1 - KalmanFilterConstansts.FILTER_FACTOR)
                + newDistanceInMeters * KalmanFilterConstansts.FILTER_FACTOR;

        return newDistanceInMeters;
    }

    private double calculateDistanceKalmanFilter2(double rssi, int txPower) {

        double newDistanceInMeters = calculateDistanceStandard(rssi, txPower);
        KalmanFilter2 kf = KalmanFilter2.getInstance();
        newDistanceInMeters = kf.filter(newDistanceInMeters, KalmanFilterConstansts.estimationVariance);

        return newDistanceInMeters;
    }

    //radiousNetwork
    private double calculateDistanceStandard(double rssi, int txPower) {
        if (rssi == 0.0D) {
            return -1.0D; // if we cannot determine accuracy, return -1.
        }

        double ratio = (rssi * 1.0D) / ((double) txPower);
        if (ratio < 1.0D) {
            return Math.pow(ratio, 10.0D);
        }

        return (0.89976D) * Math.pow(ratio, 7.7095D) + 0.125D;
//        return (0.89976d * Math.pow(ratio, 7.7095D)) + 0.111D;
    }

}
