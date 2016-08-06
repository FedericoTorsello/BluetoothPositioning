package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.Device;
import it.unibo.torsello.bluetoothpositioning.main.BLEPositioning;
import it.unibo.torsello.bluetoothpositioning.adapter.LeDeviceListAdapter;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;

public class DeviceFrag extends Fragment implements BLEPositioning.OnAddDevicesListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private LeDeviceListAdapter leDeviceListAdapter;
    private SharedPreferences settings;

    public static DeviceFrag newInstance(String message) {
        DeviceFrag f = new DeviceFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beacon_item_fragment, container, false);

        ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
        leDeviceListAdapter = new LeDeviceListAdapter(getContext(), R.layout.device_item, new ArrayList<Device>());
        mDeviceListView.setAdapter(leDeviceListAdapter);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, String.valueOf(position), Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

//    @Override
//    public void addDevices(Collection<MyBeacon> iBeacons) {
//        List<MyBeacon> list = new ArrayList<>();
//        list.addAll(iBeacons);
//
//        Comparator<MyBeacon> comparator = new Comparator<MyBeacon>() {
//            public int compare(MyBeacon b1, MyBeacon b2) {
////                if (settings.getBoolean(SettingConstants.SORT_BY_DISTANCE, false)) {
////                    return Double.compare(b1.getDist(), b2.getDist());
////                } else {
//                return 0;
////                }
//            }
//        };
//        Collections.sort(list, comparator);
//
//
//        leDeviceListAdapter.clear();
//        leDeviceListAdapter.addAll(list);
//        leDeviceListAdapter.notifyDataSetChanged();
//
//    }

    @Override
    public void addDevices(Collection<Device> iBeacons) {
        List<Device> list = new ArrayList<>();
        list.addAll(iBeacons);

        Comparator<Device> comparator = new Comparator<Device>() {
            public int compare(Device b1, Device b2) {
//                if (settings.getBoolean(SettingConstants.SORT_BY_DISTANCE, false)) {
//                    return Double.compare(b1.getDist(), b2.getDist());
//                } else {
                return 0;
//                }
            }
        };
        Collections.sort(list, comparator);


        leDeviceListAdapter.clear();
        leDeviceListAdapter.addAll(list);
        leDeviceListAdapter.notifyDataSetChanged();

    }

    @Override
    public void clearList() {
        leDeviceListAdapter.clear();
    }
}


