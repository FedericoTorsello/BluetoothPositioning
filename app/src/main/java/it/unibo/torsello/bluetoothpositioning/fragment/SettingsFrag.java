package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import it.unibo.torsello.bluetoothpositioning.R;

//import android.preference.PreferenceCategory;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//import android.preference.PreferenceScreen;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class SettingsFrag extends PreferenceFragmentCompat {

    public static SettingsFrag newInstance() {
        return new SettingsFrag();
    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.settings);
//
////        PreferenceManager prefMgr = getPreferenceManager();
////        PreferenceScreen as = prefMgr.createPreferenceScreen(getActivity());
////        as.setTitle("aaa");
////        as.setSummary("ssss");
////        as.setLayoutResource(R.layout.as_row_layout);
////        PreferenceCategory carDriverCategory = new PreferenceCategory(getActivity());
////        carDriverCategory.setTitle("wowowo");
////        as.addPreference(carDriverCategory);
////
//
//
////        PreferenceManager mgr = getPreferenceManager();
////
////        PreferenceScreen as = mgr.createPreferenceScreen(getActivity());
////        as.setTitle("aaa");
////        as.setSummary("ssss");
//
//// specify the layout for the preference screen row when it is
//// rendered as a row in a preference activity/fragment
////        as.setLayoutResource(R.layout.as_row_layout);
////
////        PreferenceCategory myCategory = new PreferenceCategory(getActivity());
////
////        as.addPreference(myCategory);
////
////        setPreferenceScreen(as);
//
//    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
//        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
//        assert tabLayout != null;
//        tabLayout.setVisibility(View.GONE);

        addPreferencesFromResource(R.xml.settings);
    }
}