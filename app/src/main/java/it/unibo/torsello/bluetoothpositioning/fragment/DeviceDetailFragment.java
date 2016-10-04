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
import it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers.DeviceDetailInner1Fragment;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private String idDeviceSelectedName;

    public static DeviceDetailFragment newInstance(String message) {
        DeviceDetailFragment fragment = new DeviceDetailFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);
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

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getChildFragmentManager(), getFragments());
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        // fragment 0
        fragments.add(DeviceDetailInner1Fragment.newInstance(idDeviceSelectedName));

        // fragment 1
        fragments.add(DeviceDetailInner2Fragment.newInstance("Details", idDeviceSelectedName));

        return fragments;
    }

}
