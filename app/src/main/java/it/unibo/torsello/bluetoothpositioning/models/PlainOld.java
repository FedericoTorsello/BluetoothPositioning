package it.unibo.torsello.bluetoothpositioning.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 11/07/16.
 */
public class PlainOld implements Plains {

    List<MyBluetoothDevice> beacons;
    int numberPlan;

    public PlainOld(int numberPlan) {
        this.numberPlan = numberPlan;
        beacons = new ArrayList<>();
    }

    public PlainOld(int numberPlan, List<MyBluetoothDevice> beacons) {
        this.numberPlan = numberPlan;
        this.beacons = beacons;
    }

    @Override
    public void setDevice(MyBluetoothDevice device) {
        beacons.add(device);
    }

    @Override
    public void setAllDevice(List<MyBluetoothDevice> allDevice) {
        beacons = allDevice;
    }

    @Override
    public List<MyBluetoothDevice> getAllDevice() {
        return beacons;
    }
}
