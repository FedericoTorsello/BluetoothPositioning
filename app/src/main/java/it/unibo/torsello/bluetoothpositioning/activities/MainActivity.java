package it.unibo.torsello.bluetoothpositioning.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers.DeviceListFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.usbObservers.UsbMeasurementFragment;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    private boolean isBackPressed = false;
    private long back_pressed;

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public static final String DEVICE_FRAGMENT = "device";
    public static final String USB_MEASUREMENT_FRAGMENT = "usb measurement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);

        ((NavigationView) findViewById(R.id.nav_view2)).setNavigationItemSelectedListener(this);

        replaceFragment(DEVICE_FRAGMENT);

        checkAndroidMPermission();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {

            replaceFragment(DEVICE_FRAGMENT);

            final long DOUBLE_PRESS_INTERVAL = 1500L;
            if (!isBackPressed || back_pressed + DOUBLE_PRESS_INTERVAL <= System.currentTimeMillis()) {
                isBackPressed = true;
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                assert fab != null;
                Snackbar.make(fab, R.string.snackBar_exit, Snackbar.LENGTH_SHORT).show();
            } else {
                super.finish();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(DEVICE_FRAGMENT);
                break;
            case R.id.nav_settings:
                drawer.openDrawer(GravityCompat.END);
                break;
            case R.id.nav_measurement:
                replaceFragment(USB_MEASUREMENT_FRAGMENT);
                break;
//            case R.id.nav_share:
//                fragment = CamTestFragment.newInstance();
//                break;
//            case R.id.nav_send:
//                fragment = ViewPagerFragment.newInstance(getFragments());
//                break;
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void replaceFragment(String fragTag) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragTag);
        switch (fragTag) {
            case DEVICE_FRAGMENT:
                currentFragment = DeviceListFragment.newInstance();
                break;
            case USB_MEASUREMENT_FRAGMENT:
                currentFragment = UsbMeasurementFragment.newInstance();
                break;
        }

        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentMainLayout, currentFragment, fragTag)
                    .commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d(TAG_CLASS, "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        Log.d(TAG_CLASS, "Permission Denied: " + permissions[i]);
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.dialog_permissions_location_access_title)
                                .setMessage(R.string.dialog_permissions_location_access_text)
                                .setPositiveButton(android.R.string.ok, null)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                    }

                                }).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkAndroidMPermission() {

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
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_location_access_title)
                        .setMessage(R.string.dialog_bluetooth_text)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(23)
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
