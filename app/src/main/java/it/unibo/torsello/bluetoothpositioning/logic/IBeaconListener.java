package it.unibo.torsello.bluetoothpositioning.logic;

import java.util.Collection;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public interface IBeaconListener {
    void beaconsFound(Collection<IBeacon> beacons);

    void onRssItemSelected(String link);
}
