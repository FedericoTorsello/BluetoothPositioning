package it.unibo.torsello.bluetoothpositioning.kalman_filter;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import it.unibo.torsello.bluetoothpositioning.constants.KFilterConstansts;

/**
 * Created by serhatalyurt on 21.4.2016.
 */
public class KalmanFilter4 {

    private double measurementNoise;
    private double processNoise;
    private RealMatrix A, B, H, Q, P0, R;
    private RealVector x;
    private ProcessModel pm;
    private MeasurementModel mm;
    private KalmanFilter filter;
    private RealVector pNoise;
    private RealVector mNoise;
    private RealVector z;
    private double txPower;

    private static KalmanFilter4 ourInstance = new KalmanFilter4();

    public static KalmanFilter4 getInstance() {
        return ourInstance;
    }

    private KalmanFilter4() {
        measurementNoise = KFilterConstansts.NUMBER_OF_MEASUREMENTS;
        processNoise = KFilterConstansts.PROCESS_NOISE;

        // A = [ 1 ]
        A = new Array2DRowRealMatrix(new double[]{1D});
        // B = null
        B = null;
        // H = [ 1 ]
        H = new Array2DRowRealMatrix(new double[]{1D});
        // x = [ 10 ]
        x = new ArrayRealVector(new double[]{txPower});
        // Q = [ 1e-5 ]
        Q = new Array2DRowRealMatrix(new double[]{processNoise});
        // P = [ 1 ]
        P0 = new Array2DRowRealMatrix(new double[]{1D});
        // R = [ 0.1 ]
        R = new Array2DRowRealMatrix(new double[]{measurementNoise});

        pm = new DefaultProcessModel(A, B, Q, x, P0);
        mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);

        // process and measurement noise vectors
        pNoise = new ArrayRealVector(1);
        mNoise = new ArrayRealVector(1);
    }

    public void setTxPower(double txPower) {
        this.txPower = txPower;
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
        z = H.operate(x).add(mNoise);

        filter.correct(z);

    }

    public double getEstimatedRssi() {
        return filter.getStateEstimation()[0];
    }
}
