package it.unibo.torsello.bluetoothpositioning.logic;

import android.bluetooth.BluetoothDevice;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by federico on 11/07/16.
 */
public class MyDevice {

    BluetoothDevice device;
    int rssi;
    byte[] scanRecord;

    public MyDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public MyDevice(String name, String identifier, String ProximityUUID) {

    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }
}
