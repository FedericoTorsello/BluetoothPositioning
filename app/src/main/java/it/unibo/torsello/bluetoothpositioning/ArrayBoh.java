package it.unibo.torsello.bluetoothpositioning;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by federico on 09/07/16.
 */
public class ArrayBoh extends ArrayList<BluetoothDevice> {

    private static ArrayBoh ourInstance = new ArrayBoh();

    private ArrayBoh() {
    }

    public static ArrayBoh getInstance() {
        return ourInstance;
    }

    @Override
    public boolean add(BluetoothDevice object) {
       return !contains(object) && super.add(object);
    }

}

