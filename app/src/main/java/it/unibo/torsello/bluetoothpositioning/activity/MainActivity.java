package it.unibo.torsello.bluetoothpositioning.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.fragment.MyFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.adapter.MyPageAdapter;
import it.unibo.torsello.bluetoothpositioning.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < getFragments().size(); i++) {
                                if (getFragments().get(i) instanceof DeviceFrag) {
                                    String tag = "android:switcher:" + R.id.viewpager + ":" + i;
                                    DeviceFrag deviceFrag = (DeviceFrag) getSupportFragmentManager().findFragmentByTag(tag);
                                    deviceFrag.addDevice(device);
                                }
                            }
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        List<Fragment> fragments = getFragments();

        MyPageAdapter pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        Log.i("current", pageAdapter.toString());

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        assert pager != null;
        pager.setAdapter(pageAdapter);
//        Log.i("current",pager.getAdapter() + "");

        BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();
        mBluetooth.startLeScan(mLeScanCallback);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        fList.add(DeviceFrag.newInstance("ciao"));
        fList.add(MyFragment.newInstance("Fragment 1"));


//        fList.add(MyFragment.newInstance("Fragment 2"));

        return fList;
    }
}
