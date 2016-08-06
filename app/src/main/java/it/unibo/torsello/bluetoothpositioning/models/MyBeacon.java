package it.unibo.torsello.bluetoothpositioning.models;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.client.BeaconDataFactory;
import org.altbeacon.beacon.client.NullBeaconDataFactory;
import org.altbeacon.beacon.distance.DistanceCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;

/**
 * Created by federico on 31/07/16.
 */
public class MyBeacon {

    private static final String TAG = "Beacon";
    protected List<Identifier> mIdentifiers;
    protected List<Long> mDataFields;
    protected List<Long> mExtraDataFields;
    protected Double mDistance;
    protected int mRssi;
    protected int mTxPower;
    protected String mBluetoothAddress;
    protected int mBeaconTypeCode;
    protected int mManufacturer;
    protected int mServiceUuid = -1;
    protected String mBluetoothName;
    protected String mParserIdentifier;
    private static final List<Long> UNMODIFIABLE_LIST_OF_LONG =
            Collections.unmodifiableList(new ArrayList<Long>());
    private static final List<Identifier> UNMODIFIABLE_LIST_OF_IDENTIFIER =
            Collections.unmodifiableList(new ArrayList<Identifier>());

    BeaconStatistics beaconStatistics;

    public MyBeacon(Beacon otherBeacon) {
        this.mIdentifiers = otherBeacon.getIdentifiers();
        this.mDataFields = otherBeacon.getDataFields();
        this.mExtraDataFields = otherBeacon.getExtraDataFields();
        this.mDistance = otherBeacon.getDistance();
        this.mRssi = otherBeacon.getRssi();
        this.mTxPower = otherBeacon.getTxPower();
        this.mBluetoothAddress = otherBeacon.getBluetoothAddress();
        this.mBeaconTypeCode = otherBeacon.getBeaconTypeCode();
        this.mServiceUuid = otherBeacon.getServiceUuid();
        this.mBluetoothName = otherBeacon.getBluetoothName();
        this.mParserIdentifier = otherBeacon.getParserIdentifier();
        beaconStatistics = new BeaconStatistics();
    }

    public int getRssi() {
        return mRssi;
    }

    public int getTxPower() {
        return mTxPower;
    }

    public String getBluetoothAddress() {
        return mBluetoothAddress;
    }

    public int getBeaconTypeCode() {
        return mBeaconTypeCode;
    }

    public int getManufacturer() {
        return mManufacturer;
    }

    public int getServiceUuid() {
        return mServiceUuid;
    }

    public String getBluetoothName() {
        return mBluetoothName;
    }

    public String getParserIdentifier() {
        return mParserIdentifier;
    }

    public Identifier getIdentifier(int i) {
        return this.mIdentifiers.get(i);
    }

    public Identifier getId1() {
        return this.mIdentifiers.get(0);
    }

    public Identifier getId2() {
        return this.mIdentifiers.get(1);
    }

    public Identifier getId3() {
        return this.mIdentifiers.get(2);
    }

    public List<Long> getDataFields() {
        return this.mDataFields.getClass().isInstance(UNMODIFIABLE_LIST_OF_LONG) ?
                this.mDataFields : Collections.unmodifiableList(this.mDataFields);
    }

    public List<Long> getExtraDataFields() {
        return this.mExtraDataFields.getClass().isInstance(UNMODIFIABLE_LIST_OF_LONG) ?
                this.mExtraDataFields : Collections.unmodifiableList(this.mExtraDataFields);
    }

    public List<Identifier> getIdentifiers() {
        return this.mIdentifiers.getClass().isInstance(UNMODIFIABLE_LIST_OF_IDENTIFIER) ?
                this.mIdentifiers : Collections.unmodifiableList(this.mIdentifiers);
    }

    public Double getDistanceAltBeacon() {
        return mDistance;
    }

    //    public double getDist() {
//        return beaconStatistics.getDist();
//    }
//
//    public double getDist2() {
//        return beaconStatistics.getDist2();
//    }
//
//    public double getDistanceInMetresKalmanFilter() {
//        return beaconStatistics.getDistInMetresKalmanFilter();
//    }
//
//    public double getDistanceInMetresNoFiltered() {
//        return beaconStatistics.getDistInMetresNoFilter();
//    }
//
//    public double getDistanceAltBeacon() {
//        return beaconStatistics.getDistanceAltBeacon();
//    }
//
//    public double getRawDistance() {
//        return beaconStatistics.getRawDistance();
//    }
//
//    public double getDistanceWOSC() {
//        return beaconStatistics.getDistanceWOSC();
//    }

}
