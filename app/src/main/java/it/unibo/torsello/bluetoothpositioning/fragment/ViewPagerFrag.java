package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

    public static ViewPagerFrag newInstance() {
        return new ViewPagerFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.prova_pag, container, false);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        assert mViewPager != null;
        mViewPager.setAdapter(new MyPageAdapter(getFragmentManager(), getFragments()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        View pr = getActivity().findViewById(R.id.prova1);

        Snackbar.make(pr, R.string.info_start_scanning, Snackbar.LENGTH_INDEFINITE).show();

        return view;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(DeviceFrag.newInstance("Scan Device"));
        fList.add(CompassMagnometerFrag.newInstance("CompassMagnometerFrag"));
        fList.add(CompassFrag.newInstance("CompassFrag"));
        fList.add(CountPassFrag.newInstance("CountPassFrag"));
        fList.add(SettingsFrag.newInstance("SettingsFrag"));

        return fList;
    }

}
