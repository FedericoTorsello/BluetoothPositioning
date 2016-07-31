package it.unibo.torsello.bluetoothpositioning.models;

import org.altbeacon.beacon.Beacon;

import java.util.Arrays;
import java.util.Collections;

import it.unibo.torsello.bluetoothpositioning.config.IBeaconConstants;
import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeacon {
    private final String TAG = getClass().getSimpleName();

    private String uuid;
    private int major;
    private int minor;
    private String address;
    private String name;
    private int txPower;
    private int rssi;
    private long currentTime;
    private double distanceApprox;

//    public IBeacon(String address, String simpleName){
//        this.address = address;
//        this.name = name;
//    }

    public IBeacon(String name, String address, int rssi, String uuid, int major, int minor, int txPower, long currentTime) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txPower = txPower;
        this.currentTime = currentTime;
        calculateDistance(rssi, txPower);
    }

    public static IBeacon generateNewIBeacon(String name, String address, int rssi, byte[] scanRecord, long currentTime) {

        String uuid = parseUUIDFrom(scanRecord);
        int major = (scanRecord[IBeaconConstants.MAJOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MAJOR_INDEX + 1] & 0xff);
        int minor = (scanRecord[IBeaconConstants.MINOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MINOR_INDEX + 1] & 0xff);
        int txPower = (int) scanRecord[IBeaconConstants.TXPOWER_INDEX];

        return new IBeacon(name, address, rssi, uuid, major, minor, txPower, currentTime);
    }

    public static boolean isBeacon(byte[] scanRecord) {
        Integer[] headerBytes = new Integer[9];

        for (int i = 0; i < headerBytes.length; i++) {
            headerBytes[i] = scanRecord[i] & 0xff;
        }

        return Collections.indexOfSubList(Arrays.asList(headerBytes), IBeaconConstants.IBEACON_HEADER) == IBeaconConstants.IBEACON_HEADER_INDEX;
    }

    private static String parseUUIDFrom(byte[] scanRecord) {
        int[] proximityUuidBytes = new int[16];
        char[] proximityUuidChars = new char[proximityUuidBytes.length * 2];

        for (int i = 0; i < proximityUuidBytes.length; i++) {
            proximityUuidBytes[i] = scanRecord[i + IBeaconConstants.PROXIMITY_UUID_INDEX] & 0xFF;
            proximityUuidChars[i * 2] = IBeaconConstants.HEX_ARRAY[proximityUuidBytes[i] >>> 4];
            proximityUuidChars[i * 2 + 1] = IBeaconConstants.HEX_ARRAY[proximityUuidBytes[i] & 0x0F];
        }

        String proximityUuidHexString = new String(proximityUuidChars);
        StringBuilder builder = new StringBuilder();
        builder.append(proximityUuidHexString.substring(0, 8));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(8, 12));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(12, 16));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(16, 20));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(20, 32));

        return builder.toString();
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getTxPower() {
        return txPower;
    }

    public double getRssi() {
        return rssi;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void calculateDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        distanceApprox = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    public double getDistanceApprox() {
        return distanceApprox;
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
//    public double getDistance() {
//        return beaconStatistics.getDistance();
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
