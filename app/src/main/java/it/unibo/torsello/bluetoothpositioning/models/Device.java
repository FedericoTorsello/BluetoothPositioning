package it.unibo.torsello.bluetoothpositioning.models;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;

/**
 * Created by federico on 05/08/16.
 */
public class Device {

    private BeaconStatistics stats;
    private String address;
    private String name;
    private Beacon beacon;

    public Device(String address, String name) {
        this.address = address;
        this.name = name;
        this.stats = new BeaconStatistics();
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return name;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void updateDistance(double processNoise, int movementState) {
        stats.updateDistance(beacon, processNoise, movementState);
    }

    public double getDist() {
        return stats.getDist();
    }

    public double getDistanceInMetresKalmanFilter() {
        return stats.getDistInMetresKalmanFilter();
    }

    public double getDistanceInMetresNoFiltered() {
        return stats.getDistInMetresNoFilter();
    }

    public double getDistance() {
        return stats.getDistance();
    }

    public double getRawDistance() {
        return stats.getRawDistance();
    }

    public double getDistanceWOSC() {
        return stats.getDistanceWOSC();
    }

    public void accuracy() {
//        double accuracy = Math.pow(12.0, 1.5 * ((rssi / measuredPower) - 1));
//        String proximity = null;
//
//        if (accuracy < 0) {
//            proximity = "unknown";
//        } else if (accuracy < 0.5) {
//            proximity = "immediate";
//        } else if (accuracy < 4.0) {
//            proximity = "near";
//        } else {
//            proximity = "far";
//        }
    }
}
