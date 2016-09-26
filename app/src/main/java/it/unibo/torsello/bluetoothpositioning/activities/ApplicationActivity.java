package it.unibo.torsello.bluetoothpositioning.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.constant.DeviceConstants;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.PreferencesFragment;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.configuration.MyArmaRssiFilter;
import it.unibo.torsello.bluetoothpositioning.util.WalkDetectionUtil;

//import com.estimote.sdk.EstimoteSDK;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ApplicationActivity extends MainActivity implements BeaconConsumer,
        PreferencesFragment.OnSettingsListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    private WalkDetectionUtil walkDetection;
    private BeaconManager beaconManager;
    private boolean isRunScan = false;
    private boolean selfCorrection;
    private double processNoise;
    private SharedPreferences preferences;
    private OnAddDevicesListener onAddDevicesListener;
    private int movementState = 1;
    private List<Device> deviceList;

    private Runnable runnable;

    public interface OnAddDevicesListener {
        void updateInfoDevices(List<Device> iBeacons);

        void clearList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        initializeBeaconManager();

        // Save battery whenever the application is not visible.
        // This reduces bluetooth power usage by about 60%
        new BackgroundPowerSaver(getApplicationContext());

        floatingActionButtonAction();
    }

    private void initialize() {
        deviceList = new ArrayList<>();

        //  memory optimization
        runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (onAddDevicesListener != null) {
                            onAddDevicesListener.updateInfoDevices(deviceList);
                        }
                    }
                });
            }
        };

        preferences = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        walkDetection = new WalkDetectionUtil(getApplication());
        if (preferences.getBoolean(SettingConstants.WALK_DETECTION, false)) {
            walkDetection.startDetection();
        }
    }

    private void initializeBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

//        MyArmaRssiFilter.enableArmaFilter(false);
//        BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);

        Log.i("AltBeacon filter used:", BeaconManager.getRssiFilterImplClass().getSimpleName());

        // for finding different type of beacon,
        beaconManager.getBeaconParsers().clear();

        // Alt beacon
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // Standard Apple iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(DeviceConstants.APPLE_BEACON_LAYOUT));
        // Estimote Nearable
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(DeviceConstants.ESTIMOTE_NEARABLE_LAYOUT));

        beaconManager.setForegroundScanPeriod(250L);
        beaconManager.setForegroundBetweenScanPeriod(0L);
        beaconManager.setBackgroundScanPeriod(250L);
        beaconManager.setBackgroundBetweenScanPeriod(0L);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                for (Beacon b : beacons) {

                    // take from the list the device
                    Device device = DeviceConstants.DEVICE_MAP.get(b.getBluetoothAddress());

                    if (device != null) { // useful only if DEVICE_MAP is empty
                        device.updateDistance(b, movementState, processNoise);

                        if (!deviceList.contains(device)) {
                            deviceList.add(device);
                        }
                    }
                }

                new Thread(runnable).start();
            }
        });
    }


    private void floatingActionButtonAction() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        Snackbar.make(fab, R.string.snackBar_start_scanning, Snackbar.LENGTH_LONG).show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isBluetoothAvailable()) {

                    isRunScan = !isRunScan;
                    String idRegion = "myRangingUniqueId";
                    Region region = new Region(idRegion, null, null, null);

                    if (isRunScan) {
                        fab.setImageResource(R.drawable.ic_bluetooth_searching_white_24dp);
                        try {
                            beaconManager.startRangingBeaconsInRegion(region);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Snackbar.make(view, R.string.snackBar_scanning_enabled,
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        fab.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                        try {
                            beaconManager.stopRangingBeaconsInRegion(region);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Snackbar.make(view, R.string.snackBar_scanning_disabled,
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if (!getSupportFragmentManager().getFragments().isEmpty()) {
//            getSupportFragmentManager().popBackStack();
//        }
//
//        super.onBackPressed();
//    }

    @Override
    protected void onPause() {
        super.onPause();

        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
        }
        walkDetection.stopDetection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }

        isBluetoothAvailable();

        isUsbAvailable();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (beaconManager.isBound(this)) {
            beaconManager.unbind(this);
        }
        onAddDevicesListener = null;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof DeviceFragment) {
            onAddDevicesListener = (OnAddDevicesListener) fragment;
        } else if (fragment instanceof DeviceDetailFragment) {
            onAddDevicesListener = (OnAddDevicesListener) fragment;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button

        // noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_clear:
                onAddDevicesListener.clearList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateKalmanNoise(double value) {
        processNoise = value;
    }

    @Override
    public void isSelfCorrection(boolean isChecked) {
        selfCorrection = isChecked;
    }

    @Override
    public void isWalkDetection(boolean isChecked) {

        if (isChecked) {
            walkDetection.startDetection();
        } else {
            walkDetection.stopDetection();
        }
    }

    @Override
    public void isArmaFilter(boolean isChecked) {

    }

    private boolean isBluetoothAvailable() {

        try {
            if (!beaconManager.checkAvailability()) {

                final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                assert fab != null;

                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_bluetooth_title)
                        .setMessage(R.string.dialog_bluetooth_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                fab.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                                BluetoothAdapter.getDefaultAdapter().enable();
                            }
                        }).show();
                fab.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
                return false;
            }
        } catch (RuntimeException e) {
            e.getStackTrace();
        }
        return true;
    }

    private boolean isUsbAvailable() {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (!availableDrivers.isEmpty()) {
            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);

            if (!manager.hasPermission(driver.getDevice())) {
                Intent startIntent = new Intent(this, getClass());
                PendingIntent pendingIntent =
                        PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_ONE_SHOT);
                manager.requestPermission(driver.getDevice(), pendingIntent);
            }
            return true;
        } else {
            return false;
        }
    }

}
