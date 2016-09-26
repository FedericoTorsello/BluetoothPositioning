package it.unibo.torsello.bluetoothpositioning.configuration;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */

import org.altbeacon.beacon.service.RssiFilter;

public class MyArmaRssiFilter implements RssiFilter {
    private static double armaSpeed = 0.08D;
    private static boolean isEnabled = true;
    private double armaMeasurement;
    private boolean isInitialized = false;

    public static void setArmaSpeed(double arma_speed) {
        armaSpeed = arma_speed;
    }

    public static void enableArmaFilter(boolean set) {
        isEnabled = set;
    }

    @Override
    public void addMeasurement(Integer rssi) {

        if (isEnabled) {
            if (!isInitialized) {
                armaMeasurement = rssi;
                isInitialized = true;
            }

            armaMeasurement = (armaMeasurement - armaSpeed * (armaMeasurement - rssi));
        } else {
            armaMeasurement = rssi;
        }

    }

    @Override
    public boolean noMeasurementsAvailable() {
        return false;
    }

    @Override
    public double calculateRssi() {
        return armaMeasurement;
    }
}
