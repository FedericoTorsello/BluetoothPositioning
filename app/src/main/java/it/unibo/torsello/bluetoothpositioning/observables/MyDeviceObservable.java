package it.unibo.torsello.bluetoothpositioning.observables;

import java.util.Observable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MyDeviceObservable extends Observable {

    private static MyDeviceObservable instance = new MyDeviceObservable();

    public static MyDeviceObservable getInstance() {
        return instance;
    }

    private MyDeviceObservable() {
        super();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
