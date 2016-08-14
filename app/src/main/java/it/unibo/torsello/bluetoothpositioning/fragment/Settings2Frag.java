package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
//import android.preference.PreferenceCategory;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//import android.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by federico on 13/08/16.
 */
public class Settings2Frag extends PreferenceFragmentCompat {

    public static Settings2Frag newInstance() {
        return new Settings2Frag();
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
        addPreferencesFromResource(R.xml.settings);
    }
}