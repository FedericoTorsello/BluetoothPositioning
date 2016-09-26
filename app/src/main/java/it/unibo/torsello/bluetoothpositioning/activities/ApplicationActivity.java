package it.unibo.torsello.bluetoothpositioning.activities;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.configuration.MyArmaRssiFilter;
import it.unibo.torsello.bluetoothpositioning.constant.DeviceConstants;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.SettingsFragment;
import it.unibo.torsello.bluetoothpositioning.model.Device;

//import com.estimote.sdk.EstimoteSDK;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ApplicationActivity extends MainActivity implements BeaconConsumer {

    private final String TAG_CLASS = getClass().getSimpleName();
    //    private WalkDetectionUtil walkDetection;
    private BeaconManager beaconManager;
    private boolean isRunScan = false;
    //    private boolean selfCorrection;
//    private double processNoise;
    private SharedPreferences preferences;
    private OnAddDevicesListener onAddDevicesListener;
    private BackgroundPowerSaver backgroundPowerSaver;

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
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        floatingActionButtonAction();
    }

    private void initialize() {
//        deviceList = new ArrayList<>();

        preferences = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

//        walkDetection = new WalkDetectionUtil(getApplication());
//        if (preferences.getBoolean(SettingConstants.WALK_DETECTION, false)) {
//            walkDetection.startDetection();
//        }
    }

    private void initializeBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

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

        beaconManager.setMaxTrackingAge(1000);
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
                    Region region = new Region("RegionId", null, null, null);

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

    @Override
    public void onBeaconServiceConnect() {

        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        final List<Device> deviceList = new ArrayList<>();

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                setRssiFilter();

                for (Beacon b : beacons) {

                    // take from the list the device
                    Device device = DeviceConstants.DEVICE_MAP.get(b.getBluetoothAddress());

                    if (device != null) { // useful only if DEVICE_MAP is empty
                        double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);
                        int movementState = 1;
                        device.updateDistance(b, movementState, processNoise);

                        if (!deviceList.contains(device)) {
                            deviceList.add(device);
                        }
                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (onAddDevicesListener != null) {
                            onAddDevicesListener.updateInfoDevices(deviceList);
                        }
                    }
                });
            }
        });
    }


    private void setRssiFilter() {

        int sorting = preferences.getInt(SettingConstants.FILTER_RSSI, 0);
        switch (sorting) {
            case 0:
            case R.id.radioButton_no_rssi_filtering:
                MyArmaRssiFilter.enableArmaFilter(false);
                BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);
                break;
            case R.id.radioButton_arma_rssi_filter:
                MyArmaRssiFilter.enableArmaFilter(true);
                BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);
                break;
            case R.id.radioButton_average_rssi_filter:
                BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
                break;
        }
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
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
            backgroundPowerSaver.onActivityPaused(this);
        }
//        walkDetection.stopDetection();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
            backgroundPowerSaver.onActivityResumed(this);
        }

        isBluetoothAvailable();

        isUsbAvailable();

    }

    @Override
    protected void onDestroy() {
        if (beaconManager.isBound(this)) {
            beaconManager.unbind(this);
            backgroundPowerSaver.onActivityDestroyed(this);
        }
        onAddDevicesListener = null;

        super.onDestroy();
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
//                onAddDevicesListener.clearList();

//
//                FragmentTransaction ft = getFragmentManager().beginTransaction();

//                FragmentManager fm = getSupportFragmentManager();
//                SettingsFragment alertDialog = SettingsFragment.newInstance("Some title");
//                alertDialog.show(fm, "fragment_edit_name");


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void updateKalmanNoise(double value) {
//        processNoise = value;
//    }

//    @Override
//    public void isSelfCorrection(boolean isChecked) {
//        selfCorrection = isChecked;
//    }
//
//    @Override
//    public void isWalkDetection(boolean isChecked) {
//
//        if (isChecked) {
//            walkDetection.startDetection();
//        } else {
//            walkDetection.stopDetection();
//        }
//    }

//    @Override
//    public void isArmaFilter(boolean isChecked) {
////        MyArmaRssiFilter.enableArmaFilter(isChecked);
////        BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);
//
//        BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);
//
//        BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
//        Log.i("AltBeacon filter used:", BeaconManager.getRssiFilterImplClass().getSimpleName());
//    }


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
