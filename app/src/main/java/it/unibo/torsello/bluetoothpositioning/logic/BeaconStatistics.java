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

    private DescriptiveStatistics mostRecentRSSI;
    private DescriptiveStatistics mostRecentTxPower;
    private KalmanFilter3 kf;
    private double lastCalculatedDistance;
    private double lastRawDistance;
    private double lastWOSC;
    private static final int WINDOW = 20;

    private double dist;
    private double distInMetresKalmanFilter;
    private double distInMetresNoFilter;

    public static final double FILTER_FACTOR = 0.1;

    public BeaconStatistics() {

        // limit on the number of values that can be stored in the dataset
        mostRecentRSSI = new DescriptiveStatistics();
        mostRecentRSSI.setWindowSize(WINDOW);
        mostRecentTxPower = new DescriptiveStatistics();
        mostRecentTxPower.setWindowSize(WINDOW);

        lastCalculatedDistance = 0;
        lastRawDistance = 0;
        lastWOSC = 0;

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
//        public void updateDistance(IBeacon b, double movementState, double processNoise) {
        double lastFilteredReading = -1;

        mostRecentRSSI.addValue(b.getRssi());
        mostRecentTxPower.addValue(b.getTxPower());

        // Update measurement noise continually
        double mNoise = Math.sqrt((100 * 9 / Math.log(10)) * Math.log(1 + Math.pow(mostRecentRSSI.getMean() / mostRecentRSSI.getStandardDeviation(), 2)));
        if (!Double.isInfinite(mNoise) && !Double.isNaN(mNoise)) {
            kf.setMeasurementNoise(mNoise);
        }
        kf.setProcessNoise(processNoise);
        lastFilteredReading = kf.filter(mostRecentRSSI.getPercentile(50), movementState);

        lastCalculatedDistance = calculateDistance(mostRecentTxPower.getPercentile(50), lastFilteredReading);
        lastRawDistance = calculateDistance(b.getTxPower(), b.getRssi());
        lastWOSC = calculateDistance(b.getTxPower(), lastFilteredReading);

        dist = calculateDistanceMIo(b.getTxPower(), b.getRssi());

        distInMetresNoFilter = calculateDistanceNoFilter(b);

        distInMetresKalmanFilter = calculateDistanceKalmanFilter(b);
    }

    private double calculateDistance(double txPower, double rssi) {
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

    public double calculateDistanceMIo(double txPower, double rssi) {
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


    public double calculateDistanceNoFilter(Beacon b) {

        double accuracy = KalmanFilter.filter(b.getRssi(), b.getBluetoothAddress());
        double newDistanceInMeters = distanceFrom(accuracy, b.getTxPower());

        if (b != null) {
//            double previousAccuracy = b.getDistanceInMetresKalmanFilter();
            double previousAccuracy = distInMetresKalmanFilter;
            newDistanceInMeters = previousAccuracy * (1 - FILTER_FACTOR)
                    + newDistanceInMeters * FILTER_FACTOR;
        }

        return newDistanceInMeters;
    }

    public double calculateDistanceKalmanFilter(Beacon b) {
        double newDistanceInMeters = distanceFrom(b.getRssi(), b.getTxPower());

        if (b != null) {
            newDistanceInMeters = distanceFrom(b.getRssi(), b.getTxPower());
            newDistanceInMeters = new KalmanFilter2(newDistanceInMeters, 0.05).filter();
        }

        return newDistanceInMeters;

    }


//    double getDistanceInternet(int rssi, int txPower) {
//    /*
//     * RSSI = TxPower - 10 * n * lg(d)
//     * n = 2 (in free space)
//     *
//     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
//     */
//
//        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
//    }


    /**
     * Calculates the accuracy of an RSSI reading.
     * <p/>
     * The code was taken from <a href="http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing" /a>
     *
     * @param rssi the RSSI value of the iBeacon
     * @return the calculated Accuracy
     */

    private double distanceFrom(double rssi, int txPower) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi / txPower;

        double distance;
        if (ratio < 1.0) {
            distance = Math.pow(ratio, 10);
        } else {
//        return (0.89976) * Math.pow(ratio, 7.7095) + 0.125;
//            distance =  Math.pow(10.0,(rssi - txPower)/-25.0);
            distance = Math.pow(10.0, ((-rssi + txPower) / 10 * 0.25));
        }
        return distance;
    }

    public double getDist() {
        return dist;
    }

    public double getDistance() {
        return lastCalculatedDistance;
    }

    public double getRawDistance() {
        return lastRawDistance;
    }

    public double getDistanceWOSC() {
        return lastWOSC;
    }

    public double getDistInMetresKalmanFilter() {
        return distInMetresKalmanFilter;
    }

    public double getDistInMetresNoFilter() {
        return distInMetresNoFilter;
    }
}
