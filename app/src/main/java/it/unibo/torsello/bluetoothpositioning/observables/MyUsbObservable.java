package it.unibo.torsello.bluetoothpositioning.observables;

import java.util.Observable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MyUsbObservable extends Observable {

    private static MyUsbObservable instance = new MyUsbObservable();

    public static MyUsbObservable getInstance() {
        return instance;
    }

    private MyUsbObservable() {
        super();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
