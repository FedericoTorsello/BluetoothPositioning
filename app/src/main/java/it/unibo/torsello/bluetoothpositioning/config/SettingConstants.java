package it.unibo.torsello.bluetoothpositioning.config;

/**
 * A class containing constants for the SharedPreference objects.
 *
 * @author Jacob Arvidsson & Jonathan Vidmar
 * @version 1.1
 */
public class SettingConstants {
    public static final String SETTINGS_PREFERENCES = "settings_preferences";

    public static final String KALMAN_SEEK_VALUE = "kalman_filter_mode";
    public static final String SELF_CORRECTING_BEACON = "self_correcting_beacon";
    public static final String WALK_DETECTION = "walk_detection";
    public static final String SORT_BY_DISTANCE = "sort_by_distance";

    public static final double KALMAN_NOISE_MIN = 0.01;
    public static final double KALMAN_NOISE_MAX = 25.0;
}
