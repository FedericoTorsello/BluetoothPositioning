package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.MyMapAdapter;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;

public class DeviceFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String mTitle;
    private MyMapAdapter myMapAdapter;
    private OnAddDevicesListener listener;

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
        myMapAdapter = new MyMapAdapter(getContext(), new ArrayMap<String, IBeacon>());
        mDeviceListView.setAdapter(myMapAdapter);

        return rootView;
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddDevicesListener) {
            listener = (OnAddDevicesListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement the OnAddDevicesListener");
        }
    }

    // Define the events that the fragment will use to communicate
    public interface OnAddDevicesListener {
        // This can be any number of events to be sent to the activity
        void onAddDevices(ArrayMap<String, IBeacon> bluetoothDevice);
    }

    public void addDevices(ArrayMap<String, IBeacon> bluetoothDevice) {
        myMapAdapter.notifyDataSetInvalidated();
        myMapAdapter.setData(bluetoothDevice);
        myMapAdapter.notifyDataSetChanged();
    }

}


