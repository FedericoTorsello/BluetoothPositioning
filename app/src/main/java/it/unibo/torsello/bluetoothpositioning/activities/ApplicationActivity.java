package it.unibo.torsello.bluetoothpositioning.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

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
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;
import it.unibo.torsello.bluetoothpositioning.util.UsbUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ApplicationActivity extends MainActivity implements BeaconConsumer {

    private DeviceObservable myDeviceObservable;

    private final String TAG_CLASS = getClass().getSimpleName();
    private BeaconManager beaconManager;
    private boolean isRunScan = false;
    private SharedPreferences preferences;
    private BackgroundPowerSaver backgroundPowerSaver;

    private UsbUtil usbUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDeviceObservable = DeviceObservable.getInstance();

        preferences = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        usbUtil = new UsbUtil(this);

        initializeBeaconManager();

        inizializeFloatingActionButton();
    }

    private void initializeBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        // Save battery whenever the application is not visible.
        // This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

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

    private void inizializeFloatingActionButton() {
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
                        device.setBeacon(b);
                        device.updateDistance(processNoise);

                        if (!deviceList.contains(device)) {
                            deviceList.add(device);
                        }
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                myDeviceObservable.notifyObservers(deviceList);
                            }
                        });
                    }
                }).start();
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

    @Override
    protected void onPause() {
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
            backgroundPowerSaver.onActivityPaused(this);
        }

        usbUtil.onPause();

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

        usbUtil.onResume();

    }

    @Override
    protected void onDestroy() {
        if (beaconManager.isBound(this)) {
            beaconManager.unbind(this);
            backgroundPowerSaver.onActivityDestroyed(this);
        }

        super.onDestroy();
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

}
