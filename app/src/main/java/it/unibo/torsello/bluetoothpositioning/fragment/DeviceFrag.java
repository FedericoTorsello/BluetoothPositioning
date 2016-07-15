package it.unibo.torsello.bluetoothpositioning.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collection;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.MyMapAdapter;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;
import it.unibo.torsello.bluetoothpositioning.logic.IBeaconListener;

public class DeviceFrag extends Fragment {

    private String mTitle;

    private MyMapAdapter myMapAdapter;

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
        myMapAdapter = new MyMapAdapter(getContext(), new ArrayMap<String, BluetoothDevice>());
        mDeviceListView.setAdapter(myMapAdapter);

        return rootView;
    }


    private OnItemSelectedListener listener;

    public void addDevices(ArrayMap<String, BluetoothDevice> bluetoothDevice) {
        myMapAdapter.notifyDataSetInvalidated();
        myMapAdapter.setData(bluetoothDevice);
        myMapAdapter.notifyDataSetChanged();
    }

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onRssItemSelected(ArrayMap<String, BluetoothDevice> bluetoothDevice);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
//            listener.onRssItemSelected(new ArrayMap<String, BluetoothDevice>());
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

}


