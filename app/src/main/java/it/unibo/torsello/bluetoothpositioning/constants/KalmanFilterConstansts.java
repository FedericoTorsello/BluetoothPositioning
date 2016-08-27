package it.unibo.torsello.bluetoothpositioning.constants;

/**
 * Created by federico on 27/08/16.
 */
public class KalmanFilterConstansts {

    public static final String KALMAN_SEEK_VALUE = "kalman_filter_mode";
    public static final double KALMAN_NOISE_MIN = 0.01;
    public static final double KALMAN_NOISE_MAX = 25.0;
    public static final double FILTER_FACTOR = 0.1;
    public static final int WINDOW = 20;

    public static final double R = 10; // Initial process noise
    public static final double Q = 60.0; // Initial measurement noise
    public static final double A = 1;
    public static final double B = 0;
    public static final double C = 1;

    public static double KALMAN_VALUE = 2D;

    public static int numOfMeasurements = 1;
    public static double estimationVariance = 0.5;
}
