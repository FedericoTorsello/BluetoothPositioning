package it.unibo.torsello.bluetoothpositioning.constant;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class KFilterConstants {

    public static final double KALMAN_NOISE_MIN = 0D;
    public static final double KALMAN_NOISE_MAX = 5D;

    public static final double INITIAL_PROCESS_NOISE = 5D; // Initial process noise
    public static final double INITIAL_MEASUREMENT_NOISE = 5D; // Initial measurement noise

    public static final int WINDOW = 5;

}
