package it.unibo.torsello.bluetoothpositioning.utils;

import android.app.Application;
import android.app.LoaderManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.constants.KFilterConstansts;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter1;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter2;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter3;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter4;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 * <p>
 * A helper class
 */
public class BeaconStatisticsUtil {

    private DescriptiveStatistics recentRssi;
    private DescriptiveStatistics recentTxPower;
    private KalmanFilter1 kalmanFilter1;
    private KalmanFilter2 kalmanFilter2;
    private KalmanFilter3 kalmanFilter3;
    private KalmanFilter4 kalmanFilter4;
    private double dist1;
    private double dist2;
    private double dist3;
    private double filteredDist1;
    private double filteredDist2;
    private double filteredDist3_1;
    private double filteredDist3_2;
    private double filteredDist4;
    private double velocity;
    private Application application;

    public BeaconStatisticsUtil() {

        // limit on the number of values that can be stored in the dataset
        recentRssi = new DescriptiveStatistics();
        recentRssi.setWindowSize(KFilterConstansts.WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(KFilterConstansts.WINDOW);

        //Sulla base della distanza costante dal beacon, cioè, la bassa interferenza dal sistema (R), la misura di alta interferenza (Q)
        // I valori dovrebbero essere basati su misurazioni statistiche attuali, quindi,
        // il filtro (measuredValue) restituisce il valore calcolato

        kalmanFilter1 = KalmanFilter1.getInstance();
        kalmanFilter2 = KalmanFilter2.getInstance();
        kalmanFilter3 = KalmanFilter3.getInstance();
        kalmanFilter4 = KalmanFilter4.getInstance();

    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void updateDistance(Beacon b, double processNoise, int movementState) {

        int rssi = b.getRssi();
        int txPower = b.getTxPower();

        recentRssi.addValue(rssi);
        recentTxPower.addValue(txPower);

        // Update measurement noise continually
        double mNoise = Math.sqrt((100 * 9 / Math.log(10)) *
                Math.log(1 + Math.pow(recentRssi.getMean() / recentRssi.getStandardDeviation(), 2)));

        if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
            kalmanFilter3.setMeasurementNoise(mNoise);
        }
        kalmanFilter3.setProcessNoise(processNoise);

        dist1 = calculateDistanceNoFilter1(rssi, txPower);
        dist2 = calculateDistanceNoFilter2(rssi, txPower);
        dist3 = calculateDistanceNoFilter3(rssi, txPower);

        filteredDist1 = calculateDistanceKalmanFilter1(rssi, txPower, b.getBluetoothAddress());
        filteredDist2 = calculateDistanceKalmanFilter2(rssi, txPower);

        double rssiFiltered = kalmanFilter3.filter(recentRssi.getPercentile(50), movementState);
        filteredDist3_1 = calculateDistanceNoFilter1(rssiFiltered, recentTxPower.getPercentile(50));

        double txPowerFiltered = kalmanFilter3.filter(recentTxPower.getPercentile(50), movementState);
        filteredDist3_2 = calculateDistanceNoFilter1(rssiFiltered, txPowerFiltered);

        filteredDist4 = calculateDistanceKalmanFilter4(rssi, txPower);


    }

    public double getDistNoFilter1() {
        return dist1;
    }

    public double getDistNoFilter2() {
        return dist2;
    }

    public double getDistNoFilter3() {
        return dist3;
    }

    public double getDistKalmanFilter1() {
        return filteredDist1;
    }

    public double getDistKalmanFilter2() {
        return filteredDist2;
    }

    public double getDistKalmanFilter3_1() {
        return filteredDist3_1;
    }

    public double getDistKalmanFilter3_2() {
        return filteredDist3_2;
    }

    public double getDistKalmanFilter4() {
        return filteredDist4;
    }

    public double getVelocity() {
        return velocity;
    }

    private double calculateDistanceNoFilter1(double rssi, double txPower) {
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

    private double calculateDistanceNoFilter2(int rssi, int txPower) {
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

    private double calculateDistanceNoFilter3(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10D, ((double) txPower - rssi) / (10 * 2));
    }

    private double calculateDistanceKalmanFilter1(double rssi, int txPower, String bluetoothAddress) {

        kalmanFilter1.filter(rssi, bluetoothAddress);
        double filteredRssi = kalmanFilter1.getMeasuredRssi();
        velocity = kalmanFilter1.getMeasuredVelocity();

        double newDistanceInMeters = calculateDistanceStandard(filteredRssi, txPower);

        return filteredDist2 * (1 - KFilterConstansts.FILTER_FACTOR)
                + newDistanceInMeters * KFilterConstansts.FILTER_FACTOR;
    }

    private double calculateDistanceKalmanFilter2(int rssi, int txPower) {

        double newDistanceInMeters = calculateDistanceStandard(rssi, txPower);

        return kalmanFilter2.filter(newDistanceInMeters);
    }

//    private double calculateDistanceKalmanFilter3(int rssi, int txPower) {
//
//        double newDistanceInMeters = calculateDistanceStandard(rssi, txPower);
//
//        return calculateDistanceNoFilter1(rssiFiltered, txPowerFiltered);
//    }

    private double calculateDistanceKalmanFilter4(int rssi, int txPower) {

        kalmanFilter4.setTxPower(txPower);
        kalmanFilter4.addRssi(rssi);

        return calculateDistanceStandard(kalmanFilter4.getEstimatedRssi(), txPower);
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
