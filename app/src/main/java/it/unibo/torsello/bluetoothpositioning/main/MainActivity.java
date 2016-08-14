package it.unibo.torsello.bluetoothpositioning.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.Settings2Frag;
import it.unibo.torsello.bluetoothpositioning.fragment.ViewPagerFrag;


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

//        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
//        assert mViewPager != null;
//        mViewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager(), getFragments()));
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
//        assert tabLayout != null;
//        tabLayout.setupWithViewPager(mViewPager);
//
//        Snackbar.make(mViewPager, R.string.info_start_scanning, Snackbar.LENGTH_INDEFINITE).show();

    }


    @Override
    public void onBackPressed() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        assert mViewPager != null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final long DOUBLE_PRESS_INTERVAL = 1000;
            if (!isBackPressed || back_pressed + DOUBLE_PRESS_INTERVAL <= System.currentTimeMillis()) {
                isBackPressed = true;
                Snackbar.make(mViewPager, R.string.exit, Snackbar.LENGTH_SHORT).show();
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


//        noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.prova, Settings2Frag.newInstance())
                    .commit();


//            getFragmentManager().beginTransaction()
//                    .replace(R.id.prova, new Settings2Frag())
//                    .commit();


        } else if (id == R.id.nav_send) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.prova, ViewPagerFrag.newInstance())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
