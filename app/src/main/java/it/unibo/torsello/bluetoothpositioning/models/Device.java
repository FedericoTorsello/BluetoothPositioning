package it.unibo.torsello.bluetoothpositioning.models;

import org.altbeacon.beacon.Beacon;

import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;

/**
 * Created by federico on 05/08/16.
 */
public class Device {

    private BeaconStatistics stats;
    private String address;
    private String simpleName;
    private Beacon beacon;
    private Integer imageBeacon;

    public Device(String address, String simpleName, Integer imageBeacon) {
        this.address = address;
        this.simpleName = simpleName;
        this.stats = new BeaconStatistics();
        this.imageBeacon = imageBeacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public void updateDistance(double processNoise, int movementState) {
        stats.updateDistance(beacon, processNoise, movementState);
    }

    public String getAddress() {
        return this.address;
    }

    public String getFriendlyName() {
        return simpleName;
    }

    public Integer getImageBeacon() {
        return imageBeacon;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public double getDist() {
        return stats.getDist();
    }

    public double getRawDistance() {
        return stats.getRawDistance();
    }

    public double getDistanceInternet() {
        return stats.getDistInternet();
    }

    public double getDistanceKalmanFilter1() {
        return stats.getDistKalmanFilter1();
    }

    public double getDistanceKalmanFilter2() {
        return stats.getDistKalmanFilter2();
    }

    public double getDistanceKalmanFilter3() {
        return stats.getDistKalmanFilter3();
    }

    public double getDistanceKalmanFilter4() {
        return stats.getDistKalmanFilter4();
    }

    public String getProximity() {
        double proximity = getDist();
//        double accuracy = Math.pow(12.0, 1.5 * ((rssi / measuredPower) - 1));
        String accuracy = null;

        if (proximity < 0) {
            accuracy = "unknown";
        } else if (proximity < 0.5) {
            accuracy = "immediate";
        } else if (proximity < 4.0) {
            accuracy = "near";
        } else {
            accuracy = "far";
        }
        return accuracy;
    }
}
