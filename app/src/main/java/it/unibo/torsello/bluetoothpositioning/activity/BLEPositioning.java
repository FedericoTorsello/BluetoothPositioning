package it.unibo.torsello.bluetoothpositioning.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.fragment.MyFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.SettingsFrag;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;

/**
 * Created by federico on 21/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEPositioning extends MainActivity implements DeviceFrag.OnAddDevicesListener {

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayMap<String, IBeacon> bluetoothDeviceMap;
    private boolean isRunScan = false;

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
                            onAddDevices(bluetoothDeviceMap);
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


                                    bluetoothDeviceMap.put(scannedBeacon.device.getAddress(), scannedBeacon);
                                    onAddDevices(bluetoothDeviceMap);
                                }
                            } catch (NullPointerException e) {
                                e.getStackTrace();
                            }
                        }
                    });
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public void onAddDevices(ArrayMap<String, IBeacon> bluetoothDevice) {

        List<Fragment> fragments = getFragments();
        String deviceFragTag = "";
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) instanceof DeviceFrag) {
                deviceFragTag = "android:switcher:" + R.id.viewpager + ":" + i;
            }
        }
        DeviceFrag deviceFrag = (DeviceFrag) getSupportFragmentManager()
                .findFragmentByTag(deviceFragTag);
        deviceFrag.addDevices(bluetoothDeviceMap);

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
