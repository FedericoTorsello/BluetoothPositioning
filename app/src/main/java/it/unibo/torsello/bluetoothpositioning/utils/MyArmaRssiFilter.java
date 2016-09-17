package it.unibo.torsello.bluetoothpositioning.utils;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */

import org.altbeacon.beacon.service.RssiFilter;

public class MyArmaRssiFilter implements RssiFilter {
    private static final String TAG = "MyArmaRssiFilter";
    private static double armaSpeed = 0.08D;
    private static boolean isEnabled = true;
    private int armaMeasurement;
    private boolean isInitialized = false;

    public static void setArmaSpeed(double arma_speed) {
        armaSpeed = arma_speed;
    }

    public static void enableArmaFilter(boolean set) {
        isEnabled = set;
    }

    @Override
    public void addMeasurement(Integer rssi) {
//        Log.d("MyArmaRssiFilter", "adding rssi: " + rssi);

        if (isEnabled) {
            if (!isInitialized) {
                armaMeasurement = rssi;
                isInitialized = true;
            }

            armaMeasurement = (int) (armaMeasurement - armaSpeed * (armaMeasurement - rssi));
//            Log.d("MyArmaRssiFilter", "armaMeasurement: " + armaMeasurement);
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
