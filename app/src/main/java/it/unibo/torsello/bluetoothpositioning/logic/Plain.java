package it.unibo.torsello.bluetoothpositioning.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 11/07/16.
 */
public class Plain implements Plains {

    List<MyBluetoothDevice> beacons;
    int numberPlan;

    public Plain(int numberPlan) {
        this.numberPlan = numberPlan;
        beacons = new ArrayList<>();
    }

    public Plain(int numberPlan, List<MyBluetoothDevice> beacons) {
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
