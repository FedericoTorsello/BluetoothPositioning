package it.unibo.torsello.bluetoothpositioning.utils;

import android.bluetooth.BluetoothDevice;
import android.support.v4.util.ArrayMap;

/**
 * Created by federico on 11/07/16.
 */
public class Device extends ArrayMap<String, BluetoothDevice> {

    @Override
    public BluetoothDevice put(String key, BluetoothDevice value) {
        return super.put(key, value);
    }

    @Override
    public BluetoothDevice get(Object key) {
        return super.get(key);
    }
}
