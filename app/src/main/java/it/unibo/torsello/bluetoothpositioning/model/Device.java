package it.unibo.torsello.bluetoothpositioning.model;

import org.altbeacon.beacon.Beacon;

import it.unibo.torsello.bluetoothpositioning.distanceEstimation.BeaconStatistics;
import it.unibo.torsello.bluetoothpositioning.distanceEstimation.Estimation;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class Device {

    private Estimation stats;
    private BeaconStatistics beaconStatistics;
    private String address;
    private String friendlyName;
    private Beacon beacon;
    private Integer imageBeacon;
    private Integer color;
    private int index;

    public Device(int index, String address, String friendlyName, Integer color, Integer imageBeacon) {
        this.index = index;
        this.address = address;
        this.friendlyName = friendlyName;
        this.stats = new Estimation();
        this.beaconStatistics = new BeaconStatistics();
        this.imageBeacon = imageBeacon;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public Integer getColor() {
        return color;
    }

    public String getAddress() {
        return this.address;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Integer getImageBeacon() {
        return imageBeacon;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public double getAltBeaconDistance() {
        return beacon.getDistance();
    }

    public String getProximity() {
        double proximity = getAltBeaconDistance();
//        double accuracy = Math.pow(12.0, 1.5 * ((rssi / measuredPower) - 1));
        String accuracy;

        if (proximity <= 0) {
            accuracy = "unknown";
        } else if (proximity <= 0.5) {
            accuracy = "immediate";
        } else if (proximity <= 4.0) {
            accuracy = "near";
        } else {
            accuracy = "far";
        }
        return accuracy;
    }

    /* Kalman filter*/
    public double getKalmanFilterDistance() {
        return beaconStatistics.getKalmanFilterDistance();
    }

    public double getRawDistance() {
        return beaconStatistics.getRawDistance();
    }


    public double getDistanceWOSC() {
        return beaconStatistics.getDistanceWOSC();
    }


    public void updateDistance(Beacon b, double processNoise) {
        beacon = b;
        beaconStatistics.updateDistance(b, processNoise);
    }

}
