package it.unibo.torsello.bluetoothpositioning.kalmanFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unibo.torsello.bluetoothpositioning.constant.KFilterConstansts;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class KalmanFilter3 {

    private static KalmanFilter3 ourInstance = new KalmanFilter3();
    private double R;
    private double Q;
    private double A;
    private double B;
    private double C;
    private double cov;
    private double x;   // estimated signal without noise
    private double x1;
    private double x2;
    private DescriptiveStatistics recentRssi;
    private DescriptiveStatistics recentTxPower;

    /**
     * Create 1-dimensional kalman filter
     */

    private KalmanFilter3() {
        //1-dimensional Kalman filter with predefined
        R = KFilterConstansts.R; // Initial process noise
        Q = KFilterConstansts.Q; // Initial measurement noise
        A = KFilterConstansts.A; // State vector
        B = KFilterConstansts.B; // Control vector
        C = KFilterConstansts.C; // Measurement vector

        cov = Double.NaN;
        x = Double.NaN;

        // limit on the number of values that can be stored in the dataset
        recentRssi = new DescriptiveStatistics();
        recentRssi.setWindowSize(KFilterConstansts.WINDOW);
        recentTxPower = new DescriptiveStatistics();
        recentTxPower.setWindowSize(KFilterConstansts.WINDOW);
    }

    public static KalmanFilter3 getInstance() {
        return ourInstance;
    }

    public double filter(double z) {
        return filter(z, 0);
    }

    /**
     * Filter a new value
     *
     * @param z Measurement
     * @param u Control
     * @return x
     */
    public double filter(double z, double u) {

        if (Double.isNaN(x)) {
            x = (1 / C) * z;
            x1 = x;
            x2 = x1;
            cov = (1 / C) * Q * (1 / C);
        } else {

            // Calculate previous update step
            B = (x - x1) / 2;

            // Compute prediction
            double predX = (A * x) + (B * u);
            double predCov = ((A * cov) * A) + R;

            // Kalman gain
            double K = predCov * C * (1 / ((C * predCov * C) + Q));

            // Correction
            x1 = x;
            x = predX + K * (z - (C * predX));
            cov = predCov - (K * C * predCov);
        }

        return x;
    }

    /**
     * Return the last filtered measurement
     *
     * @return x Estimated signal without noise
     */
    public double lastMeasurement() {
        return x;
    }

    /**
     * Set measurement noise Q
     *
     * @param noise Measurement noise
     */
    public void setMeasurementNoise(double noise) {
        Q = noise;
    }

    /**
     * Set the process noise R
     *
     * @param noise Process noise
     */
    public void setProcessNoise(double noise) {
        R = noise;
    }

    public double getCalculatedNoise(int progressPosition) {
        double percent = (double) progressPosition / 100.0;
        return KFilterConstansts.KALMAN_NOISE_MIN +
                (KFilterConstansts.KALMAN_NOISE_MAX - KFilterConstansts.KALMAN_NOISE_MIN) * percent;
    }
}


