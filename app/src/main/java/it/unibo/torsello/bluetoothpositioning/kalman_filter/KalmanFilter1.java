package it.unibo.torsello.bluetoothpositioning.kalman_filter;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import it.unibo.torsello.bluetoothpositioning.constants.KalmanFilterConstansts;

/**
 * Created by thenathanjones on 16/02/2014.
 */
public class KalmanFilter1 {

    private ArrayMap<String, Double> mPredictedSignals;
    private ArrayMap<String, Double> mPredictedVelocities;

    private static KalmanFilter1 ourInstance = new KalmanFilter1();

    public static KalmanFilter1 getInstance() {
        return ourInstance;
    }

    private KalmanFilter1() {
        mPredictedSignals = new ArrayMap<>();
        mPredictedVelocities = new ArrayMap<>();
    }

    public double filter(double newSignalValue, String identifier) {

        Double predictedSignal = mPredictedSignals.get(identifier);
        if (predictedSignal == null) {
            predictedSignal = -70D;
        }

        Double predictedVelocity = mPredictedVelocities.get(identifier);
        if (predictedVelocity == null) {
            predictedVelocity = 1D;
        }

        double k = predictedVelocity / (predictedVelocity + KalmanFilterConstansts.KALMAN_VALUE);

        double measuredSignal = predictedSignal + k * (newSignalValue - predictedSignal);
        mPredictedSignals.put(identifier, measuredSignal);


        double measuredVelocity = (1 - k) * predictedVelocity;
        mPredictedVelocities.put(identifier, measuredVelocity);

        return measuredSignal;
    }
}
