package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers.DeviceDetailInnerFragment;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DetailDeviceDetailFragment extends Fragment {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private String idDeviceSelectedName;

    public static DetailDeviceDetailFragment newInstance(String message) {
        DetailDeviceDetailFragment fragment = new DetailDeviceDetailFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_detail, container, false);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        ((CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar)).setTitle(idDeviceSelectedName);

        addChildFragment(root);

        return root;
    }

    @Override
    public void onPause() {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        super.onPause();
    }

    private void addChildFragment(View root) {

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.pag);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getChildFragmentManager(), getFragments());
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs2);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private ArrayList<Fragment> getFragments() {
        // fragment 0
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(DeviceDetailInnerFragment.newInstance(idDeviceSelectedName));

        // fragment 1
        ArrayList<String> params1 = new ArrayList<>();
        params1.add(getString(R.string.chart_arduino));
        params1.add(getString(R.string.chart_raw_distance));
        params1.add(getString(R.string.chart_altbeacon));
        params1.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart1", idDeviceSelectedName, params1));

        // fragment 2
        ArrayList<String> params2 = new ArrayList<>();
        params2.add(getString(R.string.chart_arduino));
        params2.add(getString(R.string.chart_raw_distance));
        params2.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart2", idDeviceSelectedName, params2));

        // fragment 3
        ArrayList<String> params3 = new ArrayList<>();
        params3.add(getString(R.string.chart_arduino));
        params3.add(getString(R.string.chart_altbeacon));
        params3.add(getString(R.string.chart_kalman_filter));

        fragments.add(DeviceChartFragment.newInstance("chart3", idDeviceSelectedName, params3));

        return fragments;
    }

}
