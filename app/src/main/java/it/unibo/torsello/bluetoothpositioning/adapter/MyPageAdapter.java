package it.unibo.torsello.bluetoothpositioning.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceFrag;
import it.unibo.torsello.bluetoothpositioning.fragment.Settings2Frag;
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by federico on 09/07/16.
 */
public class MyPageAdapter extends FragmentStatePagerAdapter {
//public class MyPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    FragmentManager mFragmentManager;

    public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentManager = fm;
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
        return fragments.get(position).getArguments().getString("EXTRA_MESSAGE");
    }

}
