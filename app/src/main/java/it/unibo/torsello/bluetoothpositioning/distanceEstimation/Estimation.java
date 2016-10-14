package it.unibo.torsello.bluetoothpositioning.distanceEstimation;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

import it.unibo.torsello.bluetoothpositioning.constant.KFilterConstants;
import it.unibo.torsello.bluetoothpositioning.filters.kalmanFilter.KFilterBuilder;
import it.unibo.torsello.bluetoothpositioning.filters.kalmanFilter.KFilter;
import it.unibo.torsello.bluetoothpositioning.filters.kalmanFilter2.KFilter2;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class Estimation {

    private DescriptiveStatistics recentRSSI;
    private DescriptiveStatistics recentTxPower;
    private KFilter kf1;
    private double distanceEstimated;
    private double rawDistanceEstimated;
    private double WOSC;

    private boolean kf1Enabled;

    private KFilter2 kf2;

    private double kalmanDistance2;

    private DescriptiveStatistics recentRSSI1;
    private DescriptiveStatistics recentTxPower1;

    public Estimation() {

        rawDistanceEstimated = 0;

        // limit on the number of values that can be stored in the dataset
        recentRSSI = new DescriptiveStatistics();
        recentRSSI.setWindowSize(KFilterConstants.WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(KFilterConstants.WINDOW);

        distanceEstimated = 0;
        WOSC = 0;

        kf1 = new KFilterBuilder()
                // filter for RSSI
                .R(KFilterConstants.INITIAL_PROCESS_NOISE) // Initial process noise
                .Q(KFilterConstants.INITIAL_MEASUREMENT_NOISE) // Initial measurement noise
                .build();

        recentRSSI1 = new DescriptiveStatistics();
        recentRSSI1.setWindowSize(KFilterConstants.WINDOW);
        recentTxPower1 = new DescriptiveStatistics();
        recentTxPower1.setWindowSize(KFilterConstants.WINDOW);

        kf2 = new KFilter2();

    }

    public boolean isKf1Enabled() {
        return kf1Enabled;
    }

    public void updateDistance(Beacon b, double processNoise) {

        estimateRawDistance(b);

        estimateKalmanFilterDistance(b, processNoise);

        estimateKalmanFilter2(b, processNoise);

    }

    private void estimateKalmanFilter2(Beacon b, double myProcess) {

        double rssiFiltered = kf2.esimatePosition(b.getRssi(), myProcess);
//        rssiFiltered = Math.round(rssiFiltered * 10.0) / 10.0;
        kalmanDistance2 = calculateDistance(b.getTxPower(), rssiFiltered);
    }

    public double getKalmanDistance2() {
        return kalmanDistance2;
    }

    private void estimateRawDistance(Beacon b) {
        // raw distance
        rawDistanceEstimated = calculateDistance(b.getTxPower(), b.getRssi());
    }

    private void estimateKalmanFilterDistance(Beacon b, double processNoise) {

        if (!(processNoise > 0)) {
            kf1Enabled = false;
            distanceEstimated = 0;
        } else {
            kf1Enabled = true;

            recentRSSI.addValue(b.getRssi());
            recentTxPower.addValue(b.getTxPower());

            // Update measurement noise continually
            double mNoise = Math.sqrt((100 * 9 / Math.log(10)) *
                    Math.log(1 + Math.pow(recentRSSI.getMean() / recentRSSI.getStandardDeviation(), 2)));

            if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
                Log.i("cuai1", "asd " + mNoise);
                kf1.setMeasurementNoise(mNoise);
            }

            // update measurement, z parameter
            double lastFilteredReading = kf1.filter(recentRSSI.getPercentile(50));

            WOSC = calculateDistance(b.getTxPower(), lastFilteredReading);

            distanceEstimated = calculateDistance(recentTxPower.getPercentile(50), lastFilteredReading);
        }
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

//        return (0.89976D * Math.pow(ratio, 7.7095D)) + 0.125D;
        return (0.89976D * Math.pow(ratio, 7.7095D)) + 0.111D;
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
