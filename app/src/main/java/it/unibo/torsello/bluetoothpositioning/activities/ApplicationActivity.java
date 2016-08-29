package it.unibo.torsello.bluetoothpositioning.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

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
import it.unibo.torsello.bluetoothpositioning.constants.BeaconConstants;
import it.unibo.torsello.bluetoothpositioning.constants.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceListFrag;
import it.unibo.torsello.bluetoothpositioning.fragment.PreferencesFrag;
import it.unibo.torsello.bluetoothpositioning.utils.MyArmaRssiFilter;
import it.unibo.torsello.bluetoothpositioning.models.Device;
import it.unibo.torsello.bluetoothpositioning.utils.WalkDetection;

//import com.estimote.sdk.EstimoteSDK;

/**
 * Created by federico on 21/07/16.
 */

public class ApplicationActivity extends MainActivity implements BeaconConsumer,
        PreferencesFrag.OnSettingsListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    private WalkDetection walkDetection;
    private BeaconManager beaconManager;
    private boolean isRunScan = false;
    private boolean selfCorrection;
    private double processNoise;
    private SharedPreferences settings;
    private OnAddDevicesListener onAddDevicesListener;
    private int movementState = 1;

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private static final String APPLE_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String ESTIMOTE_NEARABLE_LAYOUT = "m:1-2=0101,i:3-10,d:11-11,d:12-12," +
            "d:13-14,d:15-15,d:16-16,d:17-17,d:18-18,d:19-19,d:20-20, p:21-21";

    private List<Device> deviceList;

    public interface OnAddDevicesListener {
        void updateInfoDevices(List<Device> iBeacons);

        void clearList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceList = new ArrayList<>();

        checkAndroidPermission();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);

//        String appId = "federico-torsello-studio-u-6yo";
//        String appToken = "57c8cf3bef60d9258fd9123556dace89";

        //  App ID & App Token can be taken from App section of Estimote Cloud.
//        EstimoteSDK.initialize(getApplicationContext(), appId, appToken);
        // Optional, debug logging.
//        EstimoteSDK.enableDebugLogging(true);


        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.
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
                .setBeaconLayout(APPLE_BEACON_LAYOUT));
        // Estimote Nearable
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(ESTIMOTE_NEARABLE_LAYOUT));

        beaconManager.setForegroundScanPeriod(200L);
        beaconManager.setForegroundBetweenScanPeriod(0L);
        beaconManager.setBackgroundScanPeriod(200L);
        beaconManager.setBackgroundBetweenScanPeriod(0L);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        new BackgroundPowerSaver(this.getApplicationContext());

        settings = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        walkDetection = new WalkDetection(getApplication());
        if (settings.getBoolean(SettingConstants.WALK_DETECTION, false)) {
            walkDetection.startDetection();
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        Snackbar.make(fab, R.string.snackbar_start_scanning, Snackbar.LENGTH_LONG).show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (verifyBluetooth()) {

                    isRunScan = !isRunScan;
                    String idRegion = "myRangingUniqueId";
                    Region region = new Region(idRegion, null, null, null);

                    if (isRunScan) {
                        fab.setImageResource(R.drawable.ic_bluetooth_searching_white_24dp);
                        try {
                            beaconManager.startRangingBeaconsInRegion(region);
                            Snackbar.make(view, R.string.snackbar_scanning_enabled, Snackbar.LENGTH_SHORT).show();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        fab.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                        try {
                            beaconManager.stopRangingBeaconsInRegion(region);
                            Snackbar.make(view, R.string.snackbar_scanning_disabled, Snackbar.LENGTH_INDEFINITE).show();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().getFragments().isEmpty()) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        super.onBackPressed();
    }

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
        verifyBluetooth();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onAddDevicesListener = null;
        if (beaconManager.isBound(this)) {
            beaconManager.unbind(this);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DeviceListFrag) {
            onAddDevicesListener = (OnAddDevicesListener) fragment;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                if (onAddDevicesListener != null) {

                    new Thread() {
                        @Override
                        public void run() {
                            for (Beacon b : beacons) {
                                Device device = BeaconConstants.DEVICE_MAP.get(b.getBluetoothAddress());
                                if (device != null) { //serve solo se la DEVICE_MAP è vuota
                                    device.setBeacon(b);
                                    device.updateDistance(processNoise, movementState);
                                    if (!deviceList.contains(device)) {
                                        deviceList.add(device);
                                    }
                                }
                            }
                        }
                    }.start();

                    new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onAddDevicesListener.updateInfoDevices(deviceList);
                                }
                            });
                        }
                    }.start();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d(TAG_CLASS, "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        Log.d(TAG_CLASS, "Permission Denied: " + permissions[i]);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.dialog_permissions_location_access_title)
                                .setMessage(R.string.dialog_permissions_location_access_text)
                                .setPositiveButton(android.R.string.ok, null)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                    }

                                }).show();
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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

    private boolean verifyBluetooth() {
        try {
            if (!beaconManager.checkAvailability()) {

                final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                assert fab != null;

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_bluetooth_title)
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

    @TargetApi(23)
    private void checkAndroidPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> permissions = new ArrayList<>();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (!permissions.isEmpty()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_location_access_title)
                        .setMessage(R.string.dialog_bluetooth_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(permissions.toArray(new String[permissions.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }

                        }).show();
            }
        }
    }
}
