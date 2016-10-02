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
import java.util.List;

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
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(DeviceDetailInnerFragment.newInstance(idDeviceSelectedName));
        fragments.add(DeviceChartFragment.newInstance(idDeviceSelectedName));
        fragments.add(DeviceChartFragment.newInstance(idDeviceSelectedName));
        fragments.add(DeviceChartFragment.newInstance(idDeviceSelectedName));

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.pag);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs2);
        tabLayout.setupWithViewPager(mViewPager);
    }


}
