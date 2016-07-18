package it.unibo.torsello.bluetoothpositioning.filter;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by serhatalyurt on 21.4.2016.
 */
public class D1Kalman {
    double measurementNoise = 1;
    double processNoise = 0.0001;

    // A = [ 1 ]
    RealMatrix A = new Array2DRowRealMatrix(new double[]{1d});
    // B = null
    RealMatrix B = null;
    // H = [ 1 ]
    RealMatrix H = new Array2DRowRealMatrix(new double[]{1d});
    // x = [ 10 ]
    RealVector x = new ArrayRealVector(new double[]{-65});
    // Q = [ 1e-5 ]
    RealMatrix Q = new Array2DRowRealMatrix(new double[]{processNoise});
    // P = [ 1 ]
    RealMatrix P0 = new Array2DRowRealMatrix(new double[]{1d});
    // R = [ 0.1 ]
    RealMatrix R = new Array2DRowRealMatrix(new double[]{measurementNoise});

    ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
    MeasurementModel mm = new DefaultMeasurementModel(H, R);
    KalmanFilter filter = new KalmanFilter(pm, mm);

    // process and measurement noise vectors
    RealVector pNoise = new ArrayRealVector(1);
    RealVector mNoise = new ArrayRealVector(1);

    public D1Kalman() {
    }

    public void addRssi(double rssi) {
        filter.predict();

        // simulate the process
        pNoise.setEntry(0, rssi * processNoise);

        // x = A * x + p_noise
        x = A.operate(x).add(pNoise);

        // simulate the measurement
        mNoise.setEntry(0, rssi * measurementNoise);

        // z = H * x + m_noise
        RealVector z = H.operate(x).add(mNoise);

        filter.correct(z);

    }

    public double getEstimatedRssi() {
        return filter.getStateEstimation()[0];
    }
}
