package it.unibo.torsello.bluetoothpositioning.main;

import android.annotation.TargetApi;
import android.app.Application;
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
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.AndroidModel;
import org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator;
import org.altbeacon.beacon.distance.PathLossDistanceCalculator;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RangedBeacon;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.BeaconConstants;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.logic.BeaconStatistics;
import it.unibo.torsello.bluetoothpositioning.models.IBeacon;
import it.unibo.torsello.bluetoothpositioning.models.MyBeacon;

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

    public interface OnAddDevicesListener {
        void addDevices(Collection<Beacon> iBeacons);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);

        ModelSpecificDistanceCalculator.setDistanceCalculatorClass(
                PathLossDistanceCalculator.class);
//        beaconManager.setDebug(true);
//        RangedBeacon.setSampleExpirationMilliseconds(5000);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        // Simply constructing this class and holding a reference to it
        // in your custom Application class enables auto battery saving of about 60%
        new BackgroundPowerSaver(this.getApplication());


// Add AltBeacons Parser for iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.getBeaconParsers().add(new BeaconParser()
//                .setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
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
//                scanLeDevice(isRunScan);
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
        beaconManager.setRangeNotifier(new RangeNotifier() {
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

//                if (beacons.size() > 0) {
//                    Log.i(TAG_CLASS, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
//                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("com.example.myapp.boostrapRegion", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
