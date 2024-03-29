package it.unibo.torsello.bluetoothpositioning.filters;

import android.util.Log;

import org.altbeacon.beacon.service.RssiFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MyArmaRssiFilter implements RssiFilter {
    private static double armaSpeed = 0.1D; // default value
    private static boolean isEnabled = true;
    private double armaMeasurement;
    private boolean isInitialized = false;

    public static void setArmaSpeed(double arma_speed) {
        armaSpeed = arma_speed;
    }

    public static double getArmaSpeed() {
        return armaSpeed;
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
