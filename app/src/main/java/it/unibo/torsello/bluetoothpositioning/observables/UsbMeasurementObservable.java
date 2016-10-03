package it.unibo.torsello.bluetoothpositioning.observables;

import java.util.Observable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class UsbMeasurementObservable extends Observable {

    private static UsbMeasurementObservable instance = new UsbMeasurementObservable();

    public static UsbMeasurementObservable getInstance() {
        return instance;
    }

    private UsbMeasurementObservable() {
        super();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
