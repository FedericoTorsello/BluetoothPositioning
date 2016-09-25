package it.unibo.torsello.bluetoothpositioning.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.examplesCamera.CamTestFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceRecycleViewFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.MainViewFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.PreferencesFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(MainViewFragment.newInstance(getFragments()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            replaceFragment(MainViewFragment.newInstance(getFragments()));

            final long DOUBLE_PRESS_INTERVAL = 1500;
            if (!isBackPressed || back_pressed + DOUBLE_PRESS_INTERVAL <= System.currentTimeMillis()) {
                isBackPressed = true;
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                assert fab != null;
                Snackbar.make(fab, R.string.snackBar_exit, Snackbar.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
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
                fragment = MainViewFragment.newInstance(getFragments());
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                break;
            case R.id.nav_measurement:
                fragment = UsbMeasurementFragment.newInstance();
                break;
            case R.id.nav_share:
                fragment = CamTestFragment.newInstance();
                break;
            case R.id.nav_send:
                break;
        }

        if (fragment != null) {
            replaceFragment(fragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(DeviceRecycleViewFragment.newInstance("Scan Device"));
        fList.add(PreferencesFragment.newInstance("Preferences"));

//        fList.add(CompassFragment.newInstance("Compass1"));
//        fList.add(CompassMagnoFragment.newInstance("Compass2"));
//        fList.add(CountPassFragment.newInstance("CountPass"));

        return fList;
    }

    private void replaceFragment(Fragment fragment) {

        Fragment currentFrag = getSupportFragmentManager()
                .findFragmentById(R.id.contentMainLayout);

        if (currentFrag == null || !(currentFrag.equals(fragment))) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentMainLayout, fragment)
                    .commit();

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            assert tabLayout != null;
            if (fragment instanceof MainViewFragment) {
                tabLayout.setVisibility(View.VISIBLE);
            } else {
                tabLayout.setVisibility(View.GONE);
            }
        }

    }

}
