package it.unibo.torsello.bluetoothpositioning.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.estimote.sdk.EstimoteSDK;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
//import org.altbeacon.beacon.distance.PathLossDistanceCalculator;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.R;
//import it.unibo.torsello.bluetoothpositioning.filter.BLA;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;

/**
 * Created by federico on 21/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEPositioning2 extends MainActivity implements
//        BootstrapNotifier
        BeaconConsumer {

    private final String TAG_CLASS = getClass().getSimpleName();
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Map<String, Beacon> bluetoothDeviceMap;
    private boolean isRunScan = false;
    //    private boolean selfCorrection;
//    private double processNoise;
    private SharedPreferences settings;
    private OnAddDevicesListener onAddDevicesListener;
    //    private WalkDetection walkDetection;
    private BeaconStatistics beaconStatistics;
    private BeaconManager beaconManager;
    private RegionBootstrap regionBootstrap;

    public static final String APPLE_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public static final String ESTIMOTE_NEARABLE_LAYOUT = "m:1-2=0101,i:3-10,d:11-11,d:12-12," +
            "d:13-14,d:15-15,d:16-16,d:17-17,d:18-18,d:19-19,d:20-20, p:21-21";

    public interface OnAddDevicesListener {
        void addDevices(Collection<Beacon> iBeacons);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyBluetooth();

        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), "federico-torsello-studio-u-6yo", "57c8cf3bef60d9258fd9123556dace89");
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        beaconManager = BeaconManager.getInstanceForApplication(this);

//        ModelSpecificDistanceCalculator.setDistanceCalculatorClass(PathLossDistanceCalculator.class);

        beaconManager.getBeaconParsers().clear();
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.

        // Alt beacon
        beaconManager.getBeaconParsers().add(new BeaconParser("ALTBEACON").
                setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser("EDDYSTONE_UID")
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser("EDDYSTONE").
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser("EDDYSTONE").
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // Standard Apple iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser("APPLE_BEACON")
                .setBeaconLayout(APPLE_BEACON_LAYOUT));
        // Estimote Nearable
        beaconManager.getBeaconParsers().add(new BeaconParser("ESTIMOTE_NEARABLE")
                .setBeaconLayout(ESTIMOTE_NEARABLE_LAYOUT));

        beaconManager.bind(this);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        new BackgroundPowerSaver(this.getApplicationContext());

        //beaconManager.setDebug(true);
//        BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
//        RangedBeacon.setSampleExpirationMilliseconds(5000);

//        // Simply constructing this class and holding a reference to it
//        // in your custom Application class enables auto battery saving of about 60%
//        new BackgroundPowerSaver(this.getApplication());

        beaconManager.setForegroundScanPeriod(200L);
        beaconManager.setForegroundBetweenScanPeriod(0L);
        beaconManager.setBackgroundScanPeriod(200L);
        beaconManager.setBackgroundBetweenScanPeriod(0L);

//        regionBootstrap = new RegionBootstrap(this, BeaconConstants.REGIONS);

//        settings = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
        bluetoothDeviceMap = new ArrayMap<>();
//        beaconStatistics = new BeaconStatistics();

//        walkDetection = new WalkDetection(getApplication());
//        if (settings.getBoolean(SettingConstants.WALK_DETECTION, false)) {
//            walkDetection.startDetection();
//        }

//        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
//        regionBootstrap = new RegionBootstrap(this, region);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunScan = !isRunScan;

                Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
                try {
                    if (isRunScan) {
                        beaconManager.startRangingBeaconsInRegion(region);
                    } else {
                        beaconManager.stopRangingBeaconsInRegion(region);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                String statusScan = isRunScan ? getString(R.string.scanning_enabled) : getString(R.string.scanning_disabled);
                Snackbar.make(view, statusScan, Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        walkDetection.killDetection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DeviceFrag) {
            onAddDevicesListener = (OnAddDevicesListener) fragment;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Beacon b : beacons) {
                            bluetoothDeviceMap.put(b.getBluetoothAddress(), b);
                        }

                        onAddDevicesListener.addDevices(bluetoothDeviceMap.values());
                    }
                });
            }
        });
    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();
        }
    }

}
