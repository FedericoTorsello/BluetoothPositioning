package it.unibo.torsello.bluetoothpositioning.logic;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;

import it.unibo.torsello.bluetoothpositioning.filter.D1Kalman;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter2;
import it.unibo.torsello.bluetoothpositioning.utils.IBeaconConstants;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeacon {
    private final String TAG = getClass().getSimpleName();

    public final BluetoothDevice device;
    public final String uuid;
    public final int major;
    public final int minor;
    public final String address;
    public final int txPower;

    private int lastRssi;
    private long currentTime;
    private double distanceInMetresKalmanFilter;
    private double distanceInMetresFiltered;
    private double distanceInMetresNoFiltered;

    private IBeacon(BluetoothDevice device, int rssi, String uuid, int major, int minor, int txPower, long currentTime) {
        this.device = device;
        this.lastRssi = rssi;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txPower = txPower;
        this.currentTime = currentTime;
        this.address = device.getAddress();
    }

    public static boolean isBeacon(byte[] scanRecord) {
        Integer[] headerBytes = new Integer[9];

        for (int i = 0; i < headerBytes.length; i++) {
            headerBytes[i] = scanRecord[i] & 0xff;
        }

        return Collections.indexOfSubList(Arrays.asList(headerBytes), IBeaconConstants.IBEACON_HEADER) == IBeaconConstants.IBEACON_HEADER_INDEX;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static IBeacon generateNewIBeacon(ScanResult scanResult, long currentTime) {
        byte[] scanRecord = scanResult.getScanRecord().getBytes();
        String uuid = parseUUIDFrom(scanRecord);
        int major = (scanRecord[IBeaconConstants.MAJOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MAJOR_INDEX + 1] & 0xff);
        int minor = (scanRecord[IBeaconConstants.MINOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MINOR_INDEX + 1] & 0xff);
        int txPower = (int) scanRecord[IBeaconConstants.TXPOWER_INDEX];

        return new IBeacon(scanResult.getDevice(), scanResult.getRssi(), uuid, major, minor, txPower, currentTime);
    }

    public static IBeacon generateNewIBeacon(BluetoothDevice device, int rssi, byte[] scanRecord, long currentTime) {

        String uuid = parseUUIDFrom(scanRecord);
        int major = (scanRecord[IBeaconConstants.MAJOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MAJOR_INDEX + 1] & 0xff);
        int minor = (scanRecord[IBeaconConstants.MINOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MINOR_INDEX + 1] & 0xff);
        int txPower = (int) scanRecord[IBeaconConstants.TXPOWER_INDEX];

        return new IBeacon(device, rssi, uuid, major, minor, txPower, currentTime);
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

    public double calcXXX() {
        D1Kalman d1Kalman = new D1Kalman();
        d1Kalman.addRssi(lastRssi);
        return xxx = d1Kalman.getEstimatedRssi();
    }

    public double xxx;

    public void calculateDistanceNoFilter(IBeacon existingBeacon) {

        double accuracy = KalmanFilter.filter(lastRssi, address);
        double newDistanceInMeters = distanceFrom(accuracy);

        if (existingBeacon != null) {
            double previousAccuracy = existingBeacon.getDistanceInMetresKalmanFilter();
            newDistanceInMeters = previousAccuracy * (1 - IBeaconConstants.FILTER_FACTOR)
                    + newDistanceInMeters * IBeaconConstants.FILTER_FACTOR;
        }

        distanceInMetresKalmanFilter = newDistanceInMeters;
    }

    public void calculateDistanceKalmanFilter(IBeacon existingBeacon) {
        double newDistanceInMeters = distanceFrom(lastRssi);

        if (existingBeacon != null) {
            newDistanceInMeters = distanceFrom(existingBeacon.lastRssi);
            newDistanceInMeters = new KalmanFilter2(newDistanceInMeters, 0.05).filter();
        }

        distanceInMetresKalmanFilter = newDistanceInMeters;

    }

    /**
     * Calculates the accuracy of an RSSI reading.
     * <p>
     * The code was taken from <a href="http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing" /a>
     *
     * @param rssi the RSSI value of the iBeacon
     * @return the calculated Accuracy
     */

    private double distanceFrom(double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        }

        return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;

    }

    public double getLastRssi() {
        return lastRssi;
    }

    public double getDistanceInMetresKalmanFilter() {
        return distanceInMetresKalmanFilter;
    }

    public double getDistanceInMetresNoFiltered() {
        return distanceInMetresNoFiltered = distanceFrom(lastRssi);
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
