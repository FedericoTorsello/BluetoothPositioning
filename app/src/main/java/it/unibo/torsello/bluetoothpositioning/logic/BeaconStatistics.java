package it.unibo.torsello.bluetoothpositioning.logic;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.filter.KFBuilder;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter2;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter3;

/**
 * A helper class
 *
 * @author Jonathan Vidmar
 * @version 1.0
 */
public class BeaconStatistics {

    private DescriptiveStatistics recentRSSI;
    private DescriptiveStatistics recentTxPower;
    private double lastCalculatedDistance;
    private KalmanFilter3 kf;
    private double lastRawDistance;
    private double lastWOSC;

    private static final int WINDOW = 20;
    private double dist;
    private double distInMetresKalmanFilter2;
    private double distInMetresKalmanFilter1;

    private double distInternet;
    public static final double FILTER_FACTOR = 0.1;

    public BeaconStatistics() {

        // limit on the number of values that can be stored in the dataset
        recentRSSI = new DescriptiveStatistics();
        recentRSSI.setWindowSize(WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(WINDOW);

        //Sulla base della distanza costante dal beacon, cioè, la bassa interferenza dal sistema (R), la misura di alta interferenza (Q)
        // I valori dovrebbero essere basati su misurazioni statistiche attuali, quindi,
        // il filtro (measuredValue) restituisce il valore calcolato
        kf = new KFBuilder()
                // filter for RSSI
                .R(10) // Initial process noise
                .Q(60.0) // Initial measurement noise
                .build();

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

        dist = calculateDistanceNoFilter2(b);
        distInternet = calculateDistanceNoFilter3(b);

        distInMetresKalmanFilter1 = calculateDistanceKalmanFilter1(b);
        distInMetresKalmanFilter2 = calculateDistanceKalmanFilter2(b);
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

    private double calculateDistanceNoFilter2(Beacon b) {
        if (b.getRssi() == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        final double ratio = b.getRssi() * 1.0 / b.getTxPower();
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    private double calculateDistanceNoFilter3(Beacon b) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10d, ((double) b.getTxPower() - b.getRssi()) / (10 * 2));
    }

    private double calculateDistanceKalmanFilter1(Beacon b) {

        double accuracy = KalmanFilter.filter(b.getRssi(), b.getBluetoothAddress());
        double newDistanceInMeters = calculateDistanceFrom(accuracy, b.getTxPower());

        double previousAccuracy = distInMetresKalmanFilter2;
        newDistanceInMeters = previousAccuracy * (1 - FILTER_FACTOR)
                + newDistanceInMeters * FILTER_FACTOR;

        return newDistanceInMeters;
    }

    private double calculateDistanceKalmanFilter2(Beacon b) {

        double newDistanceInMeters = calculateDistanceFrom(b.getRssi(), b.getTxPower());
        newDistanceInMeters = new KalmanFilter2(newDistanceInMeters, 0.05).filter();

        return newDistanceInMeters;
    }

    private double calculateDistanceFrom(double rssi, int txPower) {
        if (rssi == 0.0D) {
            return -1.0D; // if we cannot determine accuracy, return -1.
        }

        double ratio = (rssi * 1.0D) / ((double) txPower);
        if (ratio < 1.0D) {
            return Math.pow(ratio, 10.0D);
        }

//        return (0.89976d) * Math.pow(ratio, 7.7095d) + 0.125d;
        return (0.89976d * Math.pow(ratio, 7.7095D)) + 0.111D;
//            distance =  Math.pow(10.0,(rssi - txPower)/-25.0);
//            distance = Math.pow(10.0, ((-rssi + txPower) / 10 * 0.25));
    }

    //radiousNetwork
    private double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0.0d) {
            return -1.0d;
        }

        double ratio = (rssi * 1.0d) / ((double) txPower);
        if (ratio < 1.0d) {
            return Math.pow(ratio, 10.0d);
        }

        return (0.89976d * Math.pow(ratio, 7.7095d)) + 0.111d;
    }


}
