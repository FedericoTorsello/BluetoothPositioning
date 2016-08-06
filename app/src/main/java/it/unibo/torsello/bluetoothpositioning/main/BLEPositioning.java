package it.unibo.torsello.bluetoothpositioning.main;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.estimote.sdk.EstimoteSDK;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Collection;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.BeaconConstants;
import it.unibo.torsello.bluetoothpositioning.logic.MyArmaRssiFilter;
import it.unibo.torsello.bluetoothpositioning.models.Device;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.utils.WalkDetection;

/**
 * Created by federico on 21/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEPositioning extends MainActivity implements BeaconConsumer {

    private final String TAG_CLASS = getClass().getSimpleName();
    private boolean isRunScan = false;
    private boolean selfCorrection;
    private double processNoise;
    private SharedPreferences settings;
    private OnAddDevicesListener onAddDevicesListener;
    private WalkDetection walkDetection;
    private BeaconManager beaconManager;

    public static final String APPLE_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public static final String ESTIMOTE_NEARABLE_LAYOUT = "m:1-2=0101,i:3-10,d:11-11,d:12-12," +
            "d:13-14,d:15-15,d:16-16,d:17-17,d:18-18,d:19-19,d:20-20, p:21-21";

    public interface OnAddDevicesListener {
        void addDevices(Collection<Device> iBeacons);
        void clearList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            onAddDevicesListener.clearList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeaconManager.setRssiFilterImplClass(MyArmaRssiFilter.class);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        verifyBluetooth();

        String appId = "federico-torsello-studio-u-6yo";
        String appToken = "57c8cf3bef60d9258fd9123556dace89";

        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), appId, appToken);
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

//        ModelSpecificDistanceCalculator.setDistanceCalculatorClass(PathLossDistanceCalculator.class);

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

//        RangedBeacon.setSampleExpirationMilliseconds(5000);

        beaconManager.setForegroundScanPeriod(200L);
        beaconManager.setForegroundBetweenScanPeriod(0L);
        beaconManager.setBackgroundScanPeriod(200L);
        beaconManager.setBackgroundBetweenScanPeriod(0L);

        beaconManager.bind(this);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        new BackgroundPowerSaver(this.getApplicationContext());

        settings = getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        walkDetection = new WalkDetection(getApplication());
//        if (settings.getBoolean(SettingConstants.WALK_DETECTION, false)) {
//            walkDetection.startDetection();
//        }

        final Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunScan = !isRunScan;

                try {
                    if (isRunScan) {
                        beaconManager.startRangingBeaconsInRegion(region);
                    } else {
                        beaconManager.stopRangingBeaconsInRegion(region);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                String statusScan = isRunScan ?
                        getString(R.string.scanning_enabled) : getString(R.string.scanning_disabled);
                Snackbar.make(view, statusScan, Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyBluetooth();
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
                        try {
                            for (Beacon b : beacons) {
                                Device device = BeaconConstants.BEACON_LIST.get(b.getBluetoothAddress());
                                if (b.getBluetoothAddress().equals(device.getAddress())) {
                                    device.setBeacon(b);
                                    device.updateDistance(processNoise, 1);
                                }
                            }

                        } catch (NullPointerException e) {
                            e.getStackTrace();
                        }
                        onAddDevicesListener.addDevices(BeaconConstants.BEACON_LIST.values());
                    }
                });
            }
        });
    }

    private void verifyBluetooth() {
        if (!beaconManager.checkAvailability()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }

//            try {
//                if (!beaconManager.checkAvailability()) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Bluetooth not enabled");
//                    builder.setMessage("Please enable bluetooth in settings and restart this application.");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            finish();
//                            System.exit(0);
//                        }
//                    });
//                    builder.show();
//                }
//            } catch (RuntimeException e) {
//                e.getStackTrace();
//            }
    }


}
