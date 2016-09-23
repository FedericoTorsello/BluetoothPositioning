package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.StatePagerAdapter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MainViewFragment extends Fragment {

    private static List<Fragment> fragments;

    public static MainViewFragment newInstance(List<Fragment> fragmentList) {
        MainViewFragment.fragments = fragmentList;
        return new MainViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_viewpager, container, false);

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getFragmentManager(), fragments);
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return root;
    }

}
