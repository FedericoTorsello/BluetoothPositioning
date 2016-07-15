package it.unibo.torsello.bluetoothpositioning.logic;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.utils.IBeaconConstants;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeaconService extends Activity implements BluetoothAdapter.LeScanCallback {

    private final String TAG = getClass().getSimpleName();

    private final Context mContext;
    private final Collection<IBeaconListener> mListeners = new ArrayList<IBeaconListener>();
    private ArrayMap<String, IBeacon> mKnownBeacons = new ArrayMap<String, IBeacon>();
    private Timer mTimer;

    public ArrayMap<String, IBeacon> getmKnownBeacons() {
        return mKnownBeacons;
    }

    public IBeaconService(Context context) {
        mContext = context;
    }

    public void registerListener(IBeaconListener listener) {
        if (mListeners.isEmpty()) {
            startScanning();
        }

        mListeners.add(listener);
    }

    public void unregisterListener(IBeaconListener listener) {
        mListeners.remove(listener);

        if (mListeners.isEmpty()) {
            stopScanning();
        }
    }

    public void startScanning() {
        if (bluetoothAdapter().isEnabled()) {
            bluetoothAdapter().startLeScan(this);

            startListenerUpdates();
        } else {
            Log.w(TAG, "Bluetooth is disabled, unable to scan.");
        }
    }

    private void stopScanning() {
        bluetoothAdapter().stopLeScan(this);

        stopListenerUpdates();
    }

    private void startListenerUpdates() {
        mTimer = new Timer();
        TimerTask updateListeners = new TimerTask() {
            @Override
            public void run() {
                updateListenersWith(mKnownBeacons.values());
            }
        };

        mTimer.scheduleAtFixedRate(updateListeners, IBeaconConstants.UPDATE_PERIOD, IBeaconConstants.UPDATE_PERIOD);
    }

    private void updateListenersWith(Collection<IBeacon> beacons) {
        cullStaleBeacons(beacons);

        for (IBeaconListener listener : mListeners) {
            listener.beaconsFound(mKnownBeacons.values());
        }
    }

    private void cullStaleBeacons(Collection<IBeacon> beacons) {
        long now = System.currentTimeMillis();
        Collection<IBeacon> toRemove = new HashSet<IBeacon>();
        for (IBeacon beacon : beacons) {
            if ((now - beacon.getLastReport()) > IBeaconConstants.CULL_DELAY) {
                toRemove.add(beacon);
            }
        }

        for (IBeacon beacon : toRemove) {
            mKnownBeacons.remove(beacon.getHash());
        }
    }

    private void stopListenerUpdates() {
        mTimer.cancel();
    }

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothAdapter bluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        return mBluetoothAdapter;
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (IBeacon.isBeacon(scanRecord)) {
            IBeacon scannedBeacon = from(device, rssi, scanRecord);

            mKnownBeacons.put(scannedBeacon.getHash(), scannedBeacon);
            IBeacon existingBeacon = mKnownBeacons.get(scannedBeacon.getHash());
            scannedBeacon.calculateDistanceFrom(rssi, existingBeacon);
            Log.d(TAG + "->:distanceVars", scannedBeacon.getMinor() + "," + scannedBeacon.getTxPower() + ",");

            Log.d(TAG, "Beacon " + scannedBeacon.getHash() + " located approx. " + scannedBeacon.getDistanceInMetres() + "m");
        } else {
            Log.d(TAG, "Record is not an iBeacon");
        }
//            }
//        });


    }

    private static IBeacon from(BluetoothDevice device, int rssi, byte[] scanRecord) {

        String uuid = parseUUIDFrom(scanRecord);

        int major = (scanRecord[IBeaconConstants.MAJOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MAJOR_INDEX + 1] & 0xff);
        int minor = (scanRecord[IBeaconConstants.MINOR_INDEX] & 0xff) * 0x100 + (scanRecord[IBeaconConstants.MINOR_INDEX + 1] & 0xff);
        int txPower = (int) scanRecord[IBeaconConstants.TXPOWER_INDEX];

//        Log.d("BEACON", "\n\tName  " + device.getAddress() + "  UUID: " + uuid
//                + "\n  Major: " + major + "  Minor: " + minor + "  TxPower: " + txPower);

        return new IBeacon(device, rssi, uuid, major, minor, txPower);
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
}
