package it.unibo.torsello.bluetoothpositioning.constants;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class KFilterConstansts {

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

    public static double DEFAULT_RSSI_VALUE = -70D;

    public static int NUMBER_OF_MEASUREMENTS = 1;
    //        public static double ESTIMATION_VARIANCE = 0.5D;
//    public static double ESTIMATION_VARIANCE = 0.1D; // Estimation variance
    public static double ESTIMATION_VARIANCE = 0.01D; // Estimation variance


    //        public static double PROCESS_NOISE = 0.0001D;
//    public static double PROCESS_NOISE = 1e-5D;
//    public static double PROCESS_NOISE = 0.000001D; // Process variance
    public static double PROCESS_NOISE = 10;
}
