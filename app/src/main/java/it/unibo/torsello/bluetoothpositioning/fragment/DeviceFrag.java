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
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceListAdapter;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.main.BLEPositioning;
import it.unibo.torsello.bluetoothpositioning.models.Device;

public class DeviceFrag extends Fragment implements BLEPositioning.OnAddDevicesListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences settings;
    private DeviceListAdapter deviceListAdapter;

    public static DeviceFrag newInstance(String message) {
        DeviceFrag fragment = new DeviceFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_beacon, container, false);

        deviceListAdapter = new DeviceListAdapter(getActivity(),
                R.layout.list_item, new ArrayList<Device>());
        ListView mDeviceListView = (ListView) root.findViewById(R.id.listView_scan_disp);
        mDeviceListView.setAdapter(deviceListAdapter);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, String.valueOf(position), Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void addDevices(Collection<Device> iBeacons) {
        List<Device> list = new ArrayList<>();
        list.addAll(iBeacons);

        Comparator<Device> comparator = new Comparator<Device>() {
            public int compare(Device b1, Device b2) {
                if (settings.getBoolean(SettingConstants.SORT_BY_DISTANCE, false)) {
                    return Double.compare(b1.getDist(), b2.getDist());
                } else {
                    return 0;
                }
            }
        };

        Collections.sort(list, comparator);

        deviceListAdapter.clear();
        deviceListAdapter.addAll(list);
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        deviceListAdapter.clear();
    }
}


