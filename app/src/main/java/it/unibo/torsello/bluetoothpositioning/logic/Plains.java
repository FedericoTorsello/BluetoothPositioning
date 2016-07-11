package it.unibo.torsello.bluetoothpositioning.logic;

import java.util.List;

/**
 * Created by federico on 11/07/16.
 */
interface Plains {
    void setDevice(MyDevice device);

    void setAllDevice(List<MyDevice> allDevice);

    List<MyDevice> getAllDevice();
}
