package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class ViewPagerFragment extends Fragment {

    private static List<Fragment> fragments;

    public static ViewPagerFragment newInstance(List<Fragment> fragmentList) {
        ViewPagerFragment.fragments = fragmentList;
        return new ViewPagerFragment();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        getActivity().findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        super.onPause();
    }
}
