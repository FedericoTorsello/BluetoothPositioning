package it.unibo.torsello.bluetoothpositioning.distanceEstimation;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.constant.KFilterConstants;
import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KFilterBuilder;
import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class Estimation {

    private DescriptiveStatistics recentRSSI;
    private DescriptiveStatistics recentTxPower;
    private KFilter kf;
    private double distanceEstimated;
    private double rawDistanceEstimated;
    private double WOSC;

    public Estimation() {

        // limit on the number of values that can be stored in the dataset
        recentRSSI = new DescriptiveStatistics();
        recentRSSI.setWindowSize(KFilterConstants.WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(KFilterConstants.WINDOW);

        distanceEstimated = 0;
        rawDistanceEstimated = 0;
        WOSC = 0;

        kf = new KFilterBuilder()
                // filter for RSSI
                .R(KFilterConstants.INITIAL_PROCESS_NOISE) // Initial process noise
                .Q(KFilterConstants.INITIAL_MEASUREMENT_NOISE) // Initial measurement noise
                .build();
    }

    public void updateDistance(Beacon b, double processNoise) {

        recentRSSI.addValue(b.getRssi());
        recentTxPower.addValue(b.getTxPower());

        // Update measurement noise continually
        double mNoise = Math.sqrt((100 * 9 / Math.log(10)) *
                Math.log(1 + Math.pow(recentRSSI.getMean() / recentRSSI.getStandardDeviation(), 2)));

        if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
            kf.setMeasurementNoise(mNoise);
        }

        kf.setProcessNoise(processNoise);
        double lastFilteredReading = kf.filter(recentRSSI.getPercentile(50));
        distanceEstimated = calculateDistance(recentTxPower.getPercentile(50), lastFilteredReading);
        rawDistanceEstimated = calculateDistance(b.getTxPower(), b.getRssi());
        WOSC = calculateDistance(b.getTxPower(), lastFilteredReading);
    }

    // radiousNetwork formula
    private double calculateDistance(double txPower, double rssi) {

        if (rssi == 0.0D) {
            return -1.0D; // if we cannot determine accuracy, return -1.
        }

        double ratio = (rssi * 1.0D) / txPower;
        if (ratio < 1.0D) {
            return Math.pow(ratio, 10.0D);
        }

        return (0.89976D) * Math.pow(ratio, 7.7095D) + 0.125D;
//        return (0.89976d * Math.pow(ratio, 7.7095D)) + 0.111D;

    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */
        // return Math.pow(10D, (txPower - rssi) / (10 * 2));
    }

    public double getKalmanFilterDistance() {
        return distanceEstimated;
    }

    public double getRawDistance() {
        return rawDistanceEstimated;
    }

    public double getDistanceWOSC() {
        return WOSC;
    }

    public String getProximity() {
        double proximity = distanceEstimated;
        String accuracy;

        if (proximity <= 0) {
            accuracy = "unknown";
        } else if (proximity <= 0.5) {
            accuracy = "immediate";
        } else if (proximity <= 4.0) {
            accuracy = "near";
        } else {
            accuracy = "far";
        }
        return accuracy;
    }

}
