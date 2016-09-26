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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.examplesCamera.CamTestFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.MainViewFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.SettingsFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.UsbMeasurementFragment;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view2);
        navigationView2.setNavigationItemSelectedListener(this);

        replaceFragment(DeviceFragment.newInstance("Scan Device"));

        checkAndroidMPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {

            if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
                getSupportFragmentManager().popBackStack();
                Log.i("a", "1");
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = DeviceFragment.newInstance("Scan Device");
                break;
            case R.id.nav_settings:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
                break;
            case R.id.nav_measurement:
                fragment = UsbMeasurementFragment.newInstance();
                break;
//            case R.id.nav_share:
//                fragment = CamTestFragment.newInstance();
//                break;
//            case R.id.nav_send:
//                break;
        }

        replaceFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

//    private List<Fragment> getFragments() {
//        List<Fragment> fList = new ArrayList<>();
//        fList.add(DeviceFragment.newInstance("Scan Device"));
//        fList.add(SettingsFragment.newInstance("Settings"));
//
////        fList.add(CompassFragment.newInstance("Compass1"));
////        fList.add(CompassMagnoFragment.newInstance("Compass2"));
////        fList.add(CountPassFragment.newInstance("CountPass"));
//
//        return fList;
//    }

    private void replaceFragment(Fragment fragment) {

        if (fragment != null) {
            Fragment currentFrag = getSupportFragmentManager()
                    .findFragmentById(R.id.contentMainLayout);


            if (currentFrag == null || !(currentFrag.getClass().equals(fragment.getClass()))) {

                if (fragment instanceof SettingsFragment) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.contentMainLayout, fragment)
                            .addToBackStack(null)
                            .commit();

                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentMainLayout, fragment)
                            .commit();
                }

                TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                if (fragment instanceof MainViewFragment) {
                    tabLayout.setVisibility(View.VISIBLE);
                } else {
                    tabLayout.setVisibility(View.GONE);
                }
            }
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
