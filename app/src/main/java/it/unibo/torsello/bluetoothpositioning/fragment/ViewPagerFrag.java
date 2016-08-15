package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

//        private static ViewPagerFrag ourInstance = new ViewPagerFrag();
//
//    public static ViewPagerFrag getInstance() {
//        return ourInstance;
//    }

    public static ViewPagerFrag newInstance(String message) {
        ViewPagerFrag fragment = new ViewPagerFrag();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.view_pager_frag, container, false);

        final ViewPager mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
        assert mViewPager != null;
        mViewPager.setAdapter(new MyPageAdapter(getChildFragmentManager(), getFragments())); ///<--------
//        mViewPager.setAdapter(new MyPageAdapter(getFragmentManager(), getFragments()));
//        mViewPager.setAdapter(new MyPageAdapter(getActivity().getSupportFragmentManager(), getFragments()));

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

//        View app_view = getActivity().findViewById(R.id.app_bar_main);
//        Snackbar.make(app_view, R.string.info_start_scanning, Snackbar.LENGTH_INDEFINITE).show();

        return root;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(CompassFrag.newInstance("CompassFrag"));
        fList.add(DeviceFrag.newInstance("Scan Device"));
//        fList.add(DeviceFrag.getInstance());
        fList.add(CompassMagnometerFrag.newInstance("CompassMagnometerFrag"));
        fList.add(CountPassFrag.newInstance("CountPassFrag"));
        fList.add(SettingsFrag.newInstance("SettingsFrag"));

        return fList;
    }

}
