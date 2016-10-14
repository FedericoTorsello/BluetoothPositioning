package it.unibo.torsello.bluetoothpositioning.filters.kalmanFilter2;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by federico on 13/10/16.
 */

public class KFilter2 {

    //A - state transition matrix
    private RealMatrix A;
    //B - control input matrix
    private RealMatrix B;
    //H - measurement matrix
    private RealMatrix H;
    //Q - process noise covariance matrix (error in the process)
    private RealMatrix Q;
    //R - measurement noise covariance matrix (error in the measurement)
    private RealMatrix R;
    //x state
    private RealVector x;

    private RealMatrix P0;

    // discrete time interval (100ms) between to steps
    private final double dt = 0.1d;
    // position measurement noise (1 meter)
    private final double measurementNoise = 0.15d;

    private RealVector pNoise;

    //    private double processNoise = 0.0000001;
    private double processNoise = 0.00001d;
//    = Math.pow(dt, 4d)/4d;

    private KalmanFilter filter;

    public KFilter2() {

        A = new Array2DRowRealMatrix(new double[]{1d});

        B = null;

        H = new Array2DRowRealMatrix(new double[]{1d});

        Q = new Array2DRowRealMatrix(new double[]{processNoise});

        P0 = new Array2DRowRealMatrix(new double[][]{{65d}});

        R = new Array2DRowRealMatrix(new double[]{
                Math.pow(measurementNoise, 2d)
        });

        ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);

        pNoise = new ArrayRealVector(1);
    }

    public double esimatePosition(double pos, double myProcess) {

//        pNoise.setEntry(0, myProcess);

        // x = [ 0 0 0 0] state consists of position and velocity[pX, pY, vX, vY]
        x = new ArrayRealVector(new double[]{pos});

        // predict the state estimate one time-step ahead
        filter.predict();

        // x = A * x + B * u (state prediction)
//        x = A.operate(x).add(pNoise);
        x = A.operate(x);

        // z = H * x  (measurement prediction)
        RealVector z = H.operate(x);

        // correct the state estimate with the latest measurement
        filter.correct(z);

        //get the corrected state - the position

        return filter.getStateEstimation()[0];
    }

    /**
     * Use Kalmanfilter to decrease measurement errors
     * @param position
     * @return
     */
//    public Position<Euclidean2D> esimatePosition(Position<Euclidean2D> position){
//
//        double[] pos = position.toArray();
//        // x = [ 0 0 0 0] state consists of position and velocity[pX, pY, vX, vY]
//        x = new ArrayRealVector(new double[] { pos[0], pos[1], 0, 0 });
//
//        // predict the state estimate one time-step ahead
//        filter.predict(u);
//
//        // x = A * x + B * u (state prediction)
//        x = A.operate(x).add(B.operate(u));
//
//        // z = H * x  (measurement prediction)
//        RealVector z = H.operate(x);
//
//        // correct the state estimate with the latest measurement
//        filter.correct(z);
//
//        //get the corrected state - the position
//        double pX = filter.getStateEstimation()[0];
//        double pY = filter.getStateEstimation()[1];
//
//        return new Position2D(pX, pY);
//    }
}