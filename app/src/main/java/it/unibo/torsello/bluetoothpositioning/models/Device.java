package it.unibo.torsello.bluetoothpositioning.models;

import android.app.Application;

import org.altbeacon.beacon.Beacon;

import it.unibo.torsello.bluetoothpositioning.utils.BeaconStatisticsUtil;

/**
 * Created by federico on 05/08/16.
 */
public class Device {

    private BeaconStatisticsUtil stats;
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
        this.stats = new BeaconStatisticsUtil();
        this.imageBeacon = imageBeacon;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public void setApplication(Application application) {
        stats.setApplication(application);
    }

    public void updateDistance(double processNoise, int movementState) {
        stats.updateDistance(beacon, processNoise, movementState);
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

    public double getDistNoFilter1() {
        return stats.getDistNoFilter1();
    }

    public double getDistNoFilter2() {
        return stats.getDistNoFilter2();
    }

    public double getDistNoFilter3() {
        return stats.getDistNoFilter3();
    }

    public double getDistKalmanFilter1() {
        return stats.getDistKalmanFilter1();
    }

    public double getDistKalmanFilter2() {
        return stats.getDistKalmanFilter2();
    }

    public double getDistKalmanFilter3_1() {
        return stats.getDistKalmanFilter3_1();
    }

    public double getDistKalmanFilter3_2() {
        return stats.getDistKalmanFilter3_2();
    }

    public double getDistKalmanFilter4() {
        return stats.getDistKalmanFilter4();
    }

    public double getVelocity() {
        return stats.getVelocity();
    }

    public String getProximity() {
        double proximity = getDistNoFilter2();
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
