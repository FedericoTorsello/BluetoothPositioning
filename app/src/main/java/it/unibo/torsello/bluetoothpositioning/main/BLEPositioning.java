package it.unibo.torsello.bluetoothpositioning.main;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;
import it.unibo.torsello.bluetoothpositioning.models.IBeacon;

/**
 * Created by federico on 21/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEPositioning extends MainActivity {

    private final String TAG_CLASS = getClass().getSimpleName();
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Map<String, IBeacon> bluetoothDeviceMap;
    private boolean isRunScan = false;
    //    private boolean selfCorrection;
//    private double processNoise;
    private SharedPreferences settings;
    private OnAddDevicesListener onAddDevicesListener;
//    private WalkDetection walkDetection;

    private BeaconStatistics beaconStatistics;

    public interface OnAddDevicesListener {
        void addDevices(Collection<IBeacon> iBeacons);
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult scanResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] scanRecord = scanResult.getScanRecord().getBytes();
                        String name = scanResult.getDevice().getName();
                        String address = scanResult.getDevice().getAddress();
                        int rssi = scanResult.getRssi();
                        long time = System.currentTimeMillis();

                        if (IBeacon.isBeacon(scanRecord)) {
                            IBeacon scannedBeacon =
                                    IBeacon.generateNewIBeacon(name, address, rssi, scanRecord, time);

//                            IBeacon existingBeacon = bluetoothDeviceMap.get(scannedBeacon.getAddress());
//                            scannedBeacon.calculateDistanceKalmanFilter(existingBeacon);

//                            scannedBeacon.calcXXX();
//                            scannedBeacon.calculateDistanceMIo(scannedBeacon.getTxPower(), scannedBeacon.getRssi());

//                            beaconStatistics.updateDistance(scannedBeacon,1,20);
//                            Log.d(TAG_CLASS , beaconStatistics.getDistance() + "<-- " + scannedBeacon.address);

                            if (address.equals("D1:BE:E2:E9:67:A6")) {
                                bluetoothDeviceMap.put(address, scannedBeacon);
                                Log.d("dist", scannedBeacon.getDistanceApprox() + "<--");

                                beaconStatistics.updateDistance(scannedBeacon);
                                onAddDevicesListener.addDevices(bluetoothDeviceMap.values());
                            }

                        }
                    } catch (NullPointerException e) {
                        e.getStackTrace();
                    }
                }
            });
        }

        //Scan error codes.
        @Override
        public void onScanFailed(int errorCode) {
            String messageError;
            switch (errorCode) {
                case SCAN_FAILED_ALREADY_STARTED:
                    messageError = "SCAN_FAILED_ALREADY_STARTED";
                    break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    messageError = "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED";
                    break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    messageError = "SCAN_FAILED_FEATURE_UNSUPPORTED";
                    break;
                case SCAN_FAILED_INTERNAL_ERROR:
                    messageError = "SCAN_FAILED_INTERNAL_ERROR";
                    break;
                default:
                    messageError = "Scan failed, unknown error code";
                    break;
            }
            Toast.makeText(getApplication(), messageError, Toast.LENGTH_SHORT).show();
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
                                String name = device.getName();
                                String address = device.getAddress();
                                long time = System.currentTimeMillis();

                                if (IBeacon.isBeacon(scanRecord)) {
                                    IBeacon scannedBeacon =
                                            IBeacon.generateNewIBeacon(name, address, rssi, scanRecord, time);
                                    bluetoothDeviceMap.put(address, scannedBeacon);
                                    onAddDevicesListener.addDevices(bluetoothDeviceMap.values());
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

        settings = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        beaconStatistics = new BeaconStatistics();

//        walkDetection = new WalkDetection(getApplication());
//        if (settings.getBoolean(SettingConstants.WALK_DETECTION, false)) {
//            walkDetection.startDetection();
//        }

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
    protected void onPause() {
        super.onPause();
//        walkDetection.killDetection();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DeviceFrag) {
            onAddDevicesListener = (OnAddDevicesListener) fragment;
        }
    }

//    public double getProcessNoise() {
//        return processNoise;
//    }

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
