package it.unibo.torsello.bluetoothpositioning.logic;

import android.bluetooth.BluetoothDevice;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by federico on 11/07/16.
 */
public class MyBluetoothDevice {

    BluetoothDevice device;
    int rssi;

    public MyBluetoothDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public MyBluetoothDevice(String name, String identifier, String ProximityUUID) {

    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }

}
