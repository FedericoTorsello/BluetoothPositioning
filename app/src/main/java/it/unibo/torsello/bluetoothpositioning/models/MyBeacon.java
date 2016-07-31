package it.unibo.torsello.bluetoothpositioning.models;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;

/**
 * Created by federico on 31/07/16.
 */
public class MyBeacon extends Beacon {

    public static final Creator<MyBeacon> CREATOR = new Creator<MyBeacon>() {
        @Override
        public MyBeacon createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public MyBeacon[] newArray(int size) {
            return new MyBeacon[0];
        }
    };

}
