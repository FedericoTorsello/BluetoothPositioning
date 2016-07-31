package it.unibo.torsello.bluetoothpositioning.models;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;

/**
 * Created by federico on 13/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyBluetoothDevice {

    private BluetoothDevice device;
    private int rssi;
    private ScanResult scanResult;

    public MyBluetoothDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public MyBluetoothDevice(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public MyBluetoothDevice(String name, String identifier, String ProximityUUID) {

    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void accuracy() {
//        double accuracy = Math.pow(12.0, 1.5 * ((rssi / measuredPower) - 1));
//        String proximity = null;
//
//        if (accuracy < 0) {
//            proximity = "unknown";
//        } else if (accuracy < 0.5) {
//            proximity = "immediate";
//        } else if (accuracy < 4.0) {
//            proximity = "near";
//        } else {
//            proximity = "far";
//        }
    }

    /**
     * Calculates the accuracy of an RSSI reading.
     * <p/>
     * The code was taken from <a href="http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing" /a>
     *
     * @param txPower the calibrated TX power of an iBeacon
     * @param rssi    the RSSI value of the iBeacon
     * @return the calculated Accuracy
     */
    public static double calculateAccuracy(final int txPower, final double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        final double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

//    public PointF getMyPosition(){
//
//        /*Increasing	RSSI	Localization	Accuracy
//        with	Distance	Reference	Anchor	in
//        Wireless	Sensor	Networks*/
//
//
//        /* d = è la distanza tra il receiving anchor node ed il nodo di invio,
//        rispettivamente il nodo ase ed il nodo di ancoraggio di riferimento, in questo caso */
//        float x = (Math.pow(r1,2) - Math.pow(r2, 2) + Math.pow(d,2))/ 2 * d;
////        float y = (Math.pow(r1,2) - Math.pow(r3,2) + (Math.pow(x - i,2) + j) ) /2 j;
//
//        float i = distanzaDiP1;
//        float j = distanzaDiP3;
//        float y = (Math.pow(r1,2) - Math.pow(r3,2) + (Math.pow(x - i,2) + ) ) /2 j;
//
//    }
}
