package it.unibo.torsello.bluetoothpositioning.model;

import org.altbeacon.beacon.Beacon;

import it.unibo.torsello.bluetoothpositioning.distanceEstimation.Estimation;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class Device {

    private Estimation estimation;
    private String address;
    private String friendlyName;
    private Beacon beacon;
    private int imageBeacon;
    private int color;
    private int index;

    public Device(int index, String address, String friendlyName, Integer color, Integer imageBeacon) {
        this.index = index;
        this.address = address;
        this.friendlyName = friendlyName;
        this.estimation = new Estimation();
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
        return estimation.getProximity();
    }

    /* KFilter2 filter*/
    public double getKalmanFilterDistance() {
        if (!estimation.isKf1Enabled()) {
            return 0;
        }

        return estimation.getKalmanFilterDistance();
    }

    public double getKalmanFilter2() {
        return estimation.getKalmanDistance2();
    }

    public double getRawDistance() {
        return estimation.getRawDistance();
    }

    public double getDistanceWOSC() {
        return estimation.getDistanceWOSC();
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public void updateDistance(double processNoise) {
        if (beacon != null) {
            estimation.updateDistance(beacon, processNoise);
        }
    }

    public boolean isKalmanFilterEnabled() {
        return estimation.isKf1Enabled();
    }

}
