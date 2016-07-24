package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activity.BLEPositioning;
import it.unibo.torsello.bluetoothpositioning.adapter.LeDeviceListAdapter;
import it.unibo.torsello.bluetoothpositioning.adapter.MyArrayAdapter;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;

public class DeviceFrag extends Fragment implements BLEPositioning.OnAddDevicesListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String mTitle;
    private LeDeviceListAdapter leDeviceListAdapter;

    public static DeviceFrag newInstance(String message) {
        DeviceFrag f = new DeviceFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(EXTRA_MESSAGE);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beacon_item_fragment, container, false);

        ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
        leDeviceListAdapter = new LeDeviceListAdapter(getContext(), R.layout.device_item, new ArrayList<IBeacon>());
        mDeviceListView.setAdapter(leDeviceListAdapter);

        return rootView;
    }

    @Override
    public void addDevices(Collection<IBeacon> iBeacons) {
        List<IBeacon> list = new ArrayList<>();
        list.addAll(iBeacons);

        Comparator<IBeacon> comparator = new Comparator<IBeacon>() {
            public int compare(IBeacon c1, IBeacon c2) {
//                if (sortByDistance) {
//                    return Double.compare(c1.getDist(), c2.getDist());
//                }
                return 0;
            }
        };
        Collections.sort(list, comparator);

        leDeviceListAdapter.clear();
        leDeviceListAdapter.addAll(list);
        leDeviceListAdapter.notifyDataSetChanged();

    }
}


