package it.unibo.torsello.bluetoothpositioning.kalmanFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 * <p>
 * Simple builder class for 1-dimensional Kalman filter with predefined
 */
public class KFilterBuilder {

    private double R = 1;
    private double Q = 1;
    private double A = 1;
    private double B = 0;
    private double C = 1;

    public KFilterBuilder R(double R) {
        this.R = R;
        return this;
    }

    public KFilterBuilder Q(double Q) {
        this.Q = Q;
        return this;
    }

    public KFilterBuilder A(double A) {
        this.A = A;
        return this;
    }

    public KFilterBuilder B(double B) {
        this.B = B;
        return this;
    }

    public KFilterBuilder C(double C) {
        this.C = C;
        return this;
    }

    public KFilter build() {
        return new KFilter(R, Q, A, B, C);
    }
}
