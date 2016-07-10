package it.unibo.torsello.bluetoothpositioning.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.unibo.torsello.bluetoothpositioning.adapter.DeviceListAdapter;
import it.unibo.torsello.bluetoothpositioning.R;


public class DeviceFrag extends Fragment {

    private String mTitle;

    private DeviceListAdapter deviceListAdapter;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

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
        View rootView = inflater.inflate(R.layout.scan_beacon_layout, container, false);
        ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
        deviceListAdapter = new DeviceListAdapter(getActivity(), R.id.listView_scan_disp, new ArrayList<BluetoothDevice>());
        mDeviceListView.setAdapter(deviceListAdapter);
        return rootView;
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        deviceListAdapter.add(bluetoothDevice);
    }

}
