package it.unibo.torsello.bluetoothpositioning.filters.kalmanFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class KFilter {

    private double R;
    private double Q;
    private double A;
    private double B;
    private double C;
    private double cov;
    private double x;   // estimated signal without noise
    private double x1;
    private double x2;

    /**
     * Create 1-dimensional kalman filter
     *
     * @param R Process noise
     * @param Q Measurement noise
     * @param A State vector
     * @param B Control vector
     * @param C Measurement vector
     */
    public KFilter(double R, double Q, double A, double B, double C) {

        this.R = R;
        this.Q = Q;
        this.A = A;
        this.B = B;
        this.C = C;

        cov = Double.NaN;
        x = Double.NaN;
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
    private double filter(double z, double u) {

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

            // KFilter2 gain
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
    private double lastMeasurement() {
        return x;
    }

    /**
     * Set measurement noise INITIAL_MEASUREMENT_NOISE
     *
     * @param noise Measurement noise
     */
    public void setMeasurementNoise(double noise) {
        Q = noise;
    }

    /**
     * Set the process noise INITIAL_PROCESS_NOISE
     *
     * @param noise Process noise
     */
    public void setProcessNoise(double noise) {
        R = noise;
    }


}


