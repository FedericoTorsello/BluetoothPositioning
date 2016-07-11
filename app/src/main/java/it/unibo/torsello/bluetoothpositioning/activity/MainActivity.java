package it.unibo.torsello.bluetoothpositioning.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.fragment.MyFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.adapter.MyPageAdapter;
import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.logic.MyDevice;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final String TAG_CLASS = getClass().getSimpleName();
    private String deviceFragTag;
    private boolean isBackPressed = false;
    private static long back_pressed;
    private static final long DOUBLE_PRESS_INTERVAL = 2000;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    final DeviceFrag deviceFrag = (DeviceFrag) getSupportFragmentManager().findFragmentByTag(deviceFragTag);
                    if (deviceFrag != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyDevice a = new MyDevice(device, rssi, scanRecord);
                                List<MyDevice> myDevices = new ArrayList<MyDevice>();
                                myDevices.add(a);
                                deviceFrag.addDevice(a.getDevice());
                            }
                        });
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetootTurnOn();
                bluetoothAdapter.startLeScan(mLeScanCallback);

                Snackbar.make(view, "Scanning enabled", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        List<Fragment> fragments = getFragments();

        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) instanceof DeviceFrag) {
                deviceFragTag = "android:switcher:" + R.id.viewpager + ":" + i;
            }
        }

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        assert mViewPager != null;
        mViewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager(), fragments));

        Snackbar.make(mViewPager, "Push the button to start scanning", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetootTurnOn();
    }

    public void checkBluetootTurnOn() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
            assert mViewPager != null;
            Snackbar.make(mViewPager, "Push the button to restart scanning", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothAdapter.STATE_ON);
        }
    }

    @Override
    public void onBackPressed() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        assert mViewPager != null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mViewPager.getCurrentItem() == 0 || mViewPager.getCurrentItem() == 1) {
            if (!isBackPressed || back_pressed + DOUBLE_PRESS_INTERVAL <= System.currentTimeMillis()) {
                isBackPressed = true;
                Snackbar.make(mViewPager, R.string.exit, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(MyFragment.newInstance("Fragment 1"));
        fList.add(DeviceFrag.newInstance("ciao"));
        return fList;
    }

//    private void setDevice(){
//        Map<String,MyDevice> deviceArrayMap = new ArrayMap<>();
//        List<MyDevice> beacons = new ArrayList<>();
//
//        beacons.add(new MyDevice("mint1","fa6b721eeb46","B9407F30-F5F8-466E-AFF9-25556B57FE6D:60230:29214"));
//        beacons.add(new MyDevice("mint2","d98000b71678","B9407F30-F5F8-466E-AFF9-25556B57FE6D:5752:183"));
//        beacons.add(new MyDevice("blueberry1","dbf6f50c23bf","B9407F30-F5F8-466E-AFF9-25556B57FE6D:9151:62732"));
//        beacons.add(new MyDevice("blueberry2","fa6b721eeb46","B9407F30-F5F8-466E-AFF9-25556B57FE6D:31039:3830"));
//        beacons.add(new MyDevice("ice1","c19bb0b9019e","B9407F30-F5F8-466E-AFF9-25556B57FE6D:414:45241"));
//        beacons.add(new MyDevice("ice2","d1bee2e967a6","B9407F30-F5F8-466E-AFF9-25556B57FE6D:26534:58089"));
//
//    }
}
