package it.unibo.torsello.bluetoothpositioning.distanceEstimation;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KFBuilder;
import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KalmanFilter;

/**
 * A helper class
 *
 * @author Jonathan Vidmar
 * @version 1.0
 */
public class Estimation {

    private DescriptiveStatistics mostRecentRSSI;
    private DescriptiveStatistics mostRecentTxPower;
    private KalmanFilter kf;
    private double lastCalculatedDistance;
    private double lastRawDistance;
    private double lastWOSC;
    private static final int WINDOW = 20;

    public Estimation() {

        // limit on the number of values that can be stored in the dataset
        mostRecentRSSI = new DescriptiveStatistics();
        mostRecentRSSI.setWindowSize(WINDOW);
        mostRecentTxPower = new DescriptiveStatistics();
        mostRecentTxPower.setWindowSize(WINDOW);

        lastCalculatedDistance = 0;
        lastRawDistance = 0;
        lastWOSC = 0;

        kf = new KFBuilder()
                // filter for RSSI
                .R(10) // Initial process noise
                .Q(60.0) // Initial measurement noise
                .build();
    }

    public void updateDistance(Beacon b, double processNoise) {
        double lastFilteredReading = -1;

        mostRecentRSSI.addValue(b.getRssi());
        mostRecentTxPower.addValue(b.getTxPower());

        // Update measurement noise continually
        double mNoise = Math.sqrt((100 * 9 / Math.log(10)) *
                Math.log(1 + Math.pow(mostRecentRSSI.getMean() / mostRecentRSSI.getStandardDeviation(), 2)));

        if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
            kf.setMeasurementNoise(mNoise);
        }

        kf.setProcessNoise(processNoise);
        lastFilteredReading = kf.filter(mostRecentRSSI.getPercentile(50));
        lastCalculatedDistance = calculateDistance(mostRecentTxPower.getPercentile(50), lastFilteredReading);
        lastRawDistance = calculateDistance(b.getTxPower(), b.getRssi());
        lastWOSC = calculateDistance(b.getTxPower(), lastFilteredReading);
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

//        return (0.89976D) * Math.pow(ratio, 7.7095D) + 0.125D;
        return (0.89976d * Math.pow(ratio, 7.7095D)) + 0.111D;

    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */
        // return Math.pow(10D, (txPower - rssi) / (10 * 2));
    }

    public double getKalmanFilterDistance() {
        return lastCalculatedDistance;
    }

    public double getRawDistance() {
        return lastRawDistance;
    }

    public double getDistanceWOSC() {
        return lastWOSC;
    }

    public String getProximity() {
        double proximity = lastCalculatedDistance;
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
