package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.StatePagerAdapter;
import it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers.DeviceChartFragment;

/**
 * Created by federico on 03/10/16.
 */

public class DeviceDetailInner2Fragmet extends Fragment {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String DEVICE_NAME = "DEVICE_NAME";

    private String idDeviceSelectedName;

    public static DeviceDetailInner2Fragmet newInstance(String message, String deviceName) {
        DeviceDetailInner2Fragmet fragment = new DeviceDetailInner2Fragmet();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        args.putString(DEVICE_NAME, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idDeviceSelectedName = getArguments().getString(DEVICE_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_detail_inner_2, container, false);

        addChildFragment(root);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void addChildFragment(View root) {

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getChildFragmentManager(), getFragments());
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        // inner fragment 0
        ArrayList<String> params1 = new ArrayList<>();
        params1.add(getString(R.string.chart_arduino));
        params1.add(getString(R.string.chart_raw_distance));
        params1.add(getString(R.string.chart_altbeacon));
        params1.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart1", idDeviceSelectedName, params1));

        // inner fragment 1
        ArrayList<String> params2 = new ArrayList<>();
        params2.add(getString(R.string.chart_arduino));
        params2.add(getString(R.string.chart_raw_distance));
        params2.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart2", idDeviceSelectedName, params2));

        // inner fragment 2
        ArrayList<String> params3 = new ArrayList<>();
        params3.add(getString(R.string.chart_arduino));
        params3.add(getString(R.string.chart_altbeacon));
        params3.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart3", idDeviceSelectedName, params3));

        return fragments;
    }

}
