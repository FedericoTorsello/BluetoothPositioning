package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.MyPageAdapter;

/**
 * Created by federico on 14/08/16.
 */
public class ViewPagerFrag extends Fragment {

    private static List<Fragment> fragments;

    public static ViewPagerFrag newInstance(List<Fragment> fragments1) {
        fragments = fragments1;
        return new ViewPagerFrag();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.frag_viewpager, container, false);

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
        assert mViewPager != null;
        MyPageAdapter myPageAdapter = new MyPageAdapter(getFragmentManager(), fragments);
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        return root;
    }


}
