package it.unibo.torsello.bluetoothpositioning.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
//public class StatePagerAdapter extends FragmentStatePagerAdapter {
public class StatePagerAdapter extends FragmentPagerAdapter {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private ArrayList<Fragment> fragments;

    public StatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getArguments().getString(EXTRA_MESSAGE);
    }

}
