package it.unibo.torsello.bluetoothpositioning.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.fragment.SettingsFrag;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;
import it.unibo.torsello.bluetoothpositioning.utils.WalkDetection;

/**
 * Created by federico on 21/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEPositioning extends MainActivity
        implements SettingsFrag.OnWalkDetectionListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayMap<String, IBeacon> bluetoothDeviceMap;
    private boolean isRunScan = false;
    private boolean sortByDistance = false;
    private WalkDetection walkDetection;
    private SharedPreferences settings;
    private OnAddDevicesListener listener;

    public interface OnAddDevicesListener {
        void addDevices(Collection<IBeacon> iBeacons);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DeviceFrag) {
            listener = (OnAddDevicesListener) fragment;
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult scanResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (IBeacon.isBeacon(scanResult.getScanRecord().getBytes())) {
                            IBeacon scannedBeacon =
                                    IBeacon.generateNewIBeacon(scanResult, System.currentTimeMillis());

                            IBeacon existingBeacon = bluetoothDeviceMap.get(scannedBeacon.address);
                            scannedBeacon.calculateDistanceKalmanFilter(existingBeacon);

                            scannedBeacon.calcXXX();
                            scannedBeacon.calculateDistance(scannedBeacon.txPower, scannedBeacon.getLastRssi());

                            bluetoothDeviceMap.put(scannedBeacon.address, scannedBeacon);
                            listener.addDevices(bluetoothDeviceMap.values());

                        }
                    } catch (NullPointerException e) {
                        e.getStackTrace();
                    }
                }
            });
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (IBeacon.isBeacon(scanRecord)) {
                                    IBeacon scannedBeacon =
                                            IBeacon.generateNewIBeacon(device, rssi, scanRecord, System.currentTimeMillis());

                                    bluetoothDeviceMap.put(scannedBeacon.address, scannedBeacon);
                                    listener.addDevices(bluetoothDeviceMap.values());
                                }
                            } catch (NullPointerException e) {
                                e.getStackTrace();
                            }
                        }
                    });
                }
            };

    @Override
    public void updateWalkDetectionListener(boolean enabled) {
        if (enabled) walkDetection.startDetection();
        else walkDetection.killDetection();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        walkDetection = new WalkDetection(getApplication());
//        updateWalkDetectionListener(settings.getBoolean(SettingConstants.WALK_DETECTION, false));

        bluetoothDeviceMap = new ArrayMap<>();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothTurnOn();

                isRunScan = !isRunScan;

                scanLeDevice(isRunScan);
                String statusScan = isRunScan ? getString(R.string.scanning_enabled) : getString(R.string.scanning_disabled);
                Snackbar.make(view, statusScan, Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothTurnOn();
    }

    private void checkBluetoothTurnOn() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
            assert mViewPager != null;
            Snackbar.make(mViewPager, "Push the button to restart scanning", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothAdapter.STATE_ON);
        }
    }

    private void scanLeDevice(boolean isScanEnabled) {
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        if (isScanEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            }
        }
    }
}
