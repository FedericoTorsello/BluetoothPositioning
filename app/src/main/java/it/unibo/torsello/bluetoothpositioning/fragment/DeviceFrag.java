package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.LeDeviceListAdapter;
import it.unibo.torsello.bluetoothpositioning.adapter.LeDeviceListAdapter2;
import it.unibo.torsello.bluetoothpositioning.adapter.MyArrayAdapter;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;

public class DeviceFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String mTitle;
    private MyArrayAdapter myArrayAdapter;
    private OnAddDevicesListener listener;
    private LeDeviceListAdapter2 leDeviceListAdapter;
//    private LeDeviceListAdapter leDeviceListAdapter;

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
//        myArrayAdapter = new MyArrayAdapter(getContext(), R.layout.beacon_item_fragment, new ArrayList<IBeacon>());
//        mDeviceListView.setAdapter(myArrayAdapter);

//        leDeviceListAdapter = new LeDeviceListAdapter(getActivity());
//        mDeviceListView.setAdapter(leDeviceListAdapter);

        leDeviceListAdapter = new LeDeviceListAdapter2(getContext(), R.layout.device_item, new ArrayList<IBeacon>());
        mDeviceListView.setAdapter(leDeviceListAdapter);

        return rootView;
    }


    //     Store the listener (activity) that will have events fired once the fragment is attached
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

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Define the events that the fragment will use to communicate
    public interface OnAddDevicesListener {
        // This can be any number of events to be sent to the activity
        void onAddDevices(Collection<IBeacon> bluetoothDevice);
    }

//    public MyArrayAdapter getMyAdapter() {
//        return myArrayAdapter;
//    }

//    public LeDeviceListAdapter getLeDeviceListAdapter() {
//        return leDeviceListAdapter;
//    }

    public LeDeviceListAdapter2 getLeDeviceListAdapter() {
        return leDeviceListAdapter;
    }
}


