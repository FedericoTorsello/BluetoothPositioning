package it.unibo.torsello.bluetoothpositioning.kalman_filter;

import android.support.v4.util.ArrayMap;

/**
 * Created by thenathanjones on 16/02/2014.
 */
public class KalmanFilter {
    public static double KALMAN_VALUE = 2D;
    private static final ArrayMap<String, Double> mPredictedSignals = new ArrayMap<>();
    private static final ArrayMap<String, Double> mPredictedVelocities = new ArrayMap<>();

    public static double filter(double newSignalValue, String identifier) {
        Double predictedSignal = mPredictedSignals.get(identifier);
        if (predictedSignal == null) {
            predictedSignal = -70D;
        }

        Double predictedVelocity = mPredictedVelocities.get(identifier);
        if (predictedVelocity == null) {
            predictedVelocity = 1D;
        }

        double k = predictedVelocity / (predictedVelocity + KALMAN_VALUE);

        double measuredSignal = predictedSignal + k * (newSignalValue - predictedSignal);
        mPredictedSignals.put(identifier, measuredSignal);

        double measuredVelocity = (1 - k) * predictedVelocity;
        mPredictedVelocities.put(identifier, measuredVelocity);

        return measuredSignal;
    }
}
