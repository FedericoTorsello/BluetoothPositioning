package it.unibo.torsello.bluetoothpositioning.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by federico on 05/10/16.
 */
public class Report implements Serializable {

    private int id;
    private String name;
    private String date;
    private double arduinoValue;
    private double rawDistance;
    private double altBeaconDistance;
    private double kalmanFilterDistance;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setArduinoValue(double arduinoValue) {
        this.arduinoValue = arduinoValue;
    }

    public void setRawDistance(double rawDistance) {
        this.rawDistance = rawDistance;
    }

    public void setAltBeaconDistance(double altBeaconDistance) {
        this.altBeaconDistance = altBeaconDistance;
    }

    public void setKalmanFilterDistance(double kalmanFilterDistance) {
        this.kalmanFilterDistance = kalmanFilterDistance;
    }

}