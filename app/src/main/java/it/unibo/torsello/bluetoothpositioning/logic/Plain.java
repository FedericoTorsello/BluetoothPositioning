package it.unibo.torsello.bluetoothpositioning.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 11/07/16.
 */
public class Plain implements Plains {

    List<MyDevice> beacons;
    int numberPlan;

    public Plain(int numberPlan) {
        this.numberPlan = numberPlan;
        beacons = new ArrayList<>();
    }

    public Plain(int numberPlan, List<MyDevice> beacons) {
        this.numberPlan = numberPlan;
        this.beacons = beacons;
    }

    @Override
    public void setDevice(MyDevice device) {
        beacons.add(device);
    }

    @Override
    public void setAllDevice(List<MyDevice> allDevice) {
        beacons = allDevice;
    }

    @Override
    public List<MyDevice> getAllDevice() {
        return beacons;
    }
}
