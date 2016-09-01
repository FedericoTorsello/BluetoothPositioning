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

    private RealMatrix A; // A - state transition matrix
    private RealMatrix B; // B - control input matrix
    private RealMatrix H; // H - measurement matrix
    private RealMatrix Q; // Q - process noise  matrix
    private RealMatrix P0; // P - initial error covariance matrix
    private RealMatrix R; // R - measurement noise covariance matrix
    private RealVector x; // initial state estimate vector
    private ProcessModel pm;
    private MeasurementModel mm;
    private KalmanFilter kalmanFilter;
    private RealVector pNoise;
    private RealVector mNoise;
    private RealVector z;
    private double txPower;

    double measurementNoise;
    double processNoise;

    private static KalmanFilter4 ourInstance = new KalmanFilter4();

    public static KalmanFilter4 getInstance() {
        return ourInstance;
    }

    private KalmanFilter4() {

        measurementNoise = KFilterConstansts.ESTIMATION_VARIANCE;
        processNoise = KFilterConstansts.PROCESS_NOISE;

        // A = [ 1 ]
        A = new Array2DRowRealMatrix(new double[]{1D});

        // B = null no control input
        B = null;
//        B = new Array2DRowRealMatrix(new double[]{1D});

        // H = [ 1 ]
        H = new Array2DRowRealMatrix(new double[]{1D});

        // x = [ 10 ]
//        x = new ArrayRealVector(new double[]{KFilterConstansts.DEFAULT_RSSI_VALUE}); // constant voltage
//        x = new ArrayRealVector(new double[]{txPower}); // constant voltage
        x = new ArrayRealVector(new double[]{1D}); // constant voltage

        // Q = [ 1e-5 ]
        Q = new Array2DRowRealMatrix(new double[]{processNoise}); //process noise
//        Q = new Array2DRowRealMatrix(new double[]{0}); //process noise

        // P = [ 1 ]
//        P0 = new Array2DRowRealMatrix(new double[]{1D});
        P0 = new Array2DRowRealMatrix(new double[]{100000D});

        // R = [ 0.1 ]
//        R = new Array2DRowRealMatrix(new double[]{measurementNoise}); //measurement noise
//        R = new Array2DRowRealMatrix(new double[]{1D}); //measurement noise
        R = new Array2DRowRealMatrix(new double[]{0}); //measurement noise


        pm = new DefaultProcessModel(A, B, Q, x, P0);
        mm = new DefaultMeasurementModel(H, R);
        kalmanFilter = new KalmanFilter(pm, mm);

        // process and measurement noise vectors
        pNoise = new ArrayRealVector(1);
        mNoise = new ArrayRealVector(1);
    }

    public void setTxPower(double txPower) {
        this.txPower = txPower;
    }

    public void addRssi(int rssi) {
//        kalmanFilter.predict();
//
//        // process
//        pNoise.setEntry(0, rssi * processNoise);
//
//        // x = A * x + p_noise
//        x = A.operate(x).add(pNoise);
//
//        // measurement
//        mNoise.setEntry(0, rssi * measurementNoise);
//
//        // z = H * x + m_noise
//        z = H.operate(x).add(mNoise);
//
//        kalmanFilter.correct(z);

        /////////////

        kalmanFilter.predict();
        kalmanFilter.correct(new ArrayRealVector(new double[]{rssi}));


    }

    public double getEstimatedRssi() {
        return kalmanFilter.getStateEstimation()[0];
    }
}
