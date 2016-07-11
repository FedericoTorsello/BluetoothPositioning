package it.unibo.torsello.bluetoothpositioning.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;
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
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceListAdapter2;


public class DeviceFrag extends Fragment {

    private String mTitle;

    private DeviceListAdapter deviceListAdapter;
    private DeviceListAdapter2 deviceListAdapter2;

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
            deviceListAdapter = new DeviceListAdapter(getActivity(), R.id.listView_scan_disp, new ArrayList<BluetoothDevice>());
            mDeviceListView.setAdapter(deviceListAdapter);
        } else {
            ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
            deviceListAdapter2 = new DeviceListAdapter2(getActivity(), R.id.listView_scan_disp, new ArrayList<ScanResult>());
            mDeviceListView.setAdapter(deviceListAdapter2);
        }

        return rootView;
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        deviceListAdapter.add(bluetoothDevice);

    }

    public void addDevice(ScanResult bluetoothDevice) {
        deviceListAdapter2.add(bluetoothDevice);
    }

}
