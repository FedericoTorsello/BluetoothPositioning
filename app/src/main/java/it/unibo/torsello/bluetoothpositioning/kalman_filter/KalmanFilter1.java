package it.unibo.torsello.bluetoothpositioning.kalman_filter;

import android.support.v4.util.ArrayMap;

import it.unibo.torsello.bluetoothpositioning.constants.KFilterConstansts;

/**
 * Created by thenathanjones on 16/02/2014.
 */
public class KalmanFilter1 {

    private ArrayMap<String, Double> mPredictedSignals;
    private ArrayMap<String, Double> mPredictedVelocities;
    private double measuredVelocity;
    private double measuredRssi;

    private static KalmanFilter1 ourInstance = new KalmanFilter1();

    public static KalmanFilter1 getInstance() {
        return ourInstance;
    }

    private KalmanFilter1() {
        mPredictedSignals = new ArrayMap<>();
        mPredictedVelocities = new ArrayMap<>();

    }

    public void filter(double newSignalValue, String identifier) {

        Double predictedSignal = mPredictedSignals.get(identifier);
        if (predictedSignal == null) {
            predictedSignal = KFilterConstansts.DEFAULT_RSSI_VALUE;
        }

        Double predictedVelocity = mPredictedVelocities.get(identifier);
        if (predictedVelocity == null) {
            predictedVelocity = 1D;
        }

        double k = predictedVelocity / (predictedVelocity + KFilterConstansts.KALMAN_VALUE);

        measuredRssi = predictedSignal + k * (newSignalValue - predictedSignal);
        mPredictedSignals.put(identifier, measuredRssi);


        measuredVelocity = (1 - k) * predictedVelocity;
        mPredictedVelocities.put(identifier, measuredVelocity);
    }

    public double getMeasuredRssi() {
        return measuredRssi;
    }

    public double getMeasuredVelocity() {
        return measuredVelocity;
    }


}
