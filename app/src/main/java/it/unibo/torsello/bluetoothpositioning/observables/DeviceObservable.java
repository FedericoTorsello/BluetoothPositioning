package it.unibo.torsello.bluetoothpositioning.observables;

import java.util.Observable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceObservable extends Observable {

    private static DeviceObservable instance = new DeviceObservable();

    public static DeviceObservable getInstance() {
        return instance;
    }

    private DeviceObservable() {
        super();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
