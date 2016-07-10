package it.unibo.torsello.bluetoothpositioning;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MyFragmentVerMia extends Fragment {

    private String mTitle;

    private DeviceListAdapter deviceListAdapter;

	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static MyFragmentVerMia newInstance(String message)
	{
        MyFragmentVerMia f = new MyFragmentVerMia();
		Bundle bdl = new Bundle();
		bdl.putSerializable(EXTRA_MESSAGE, message);
	    f.setArguments(bdl);
	    return f;
	}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mTitle = getArguments().getString(EXTRA_MESSAGE);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
//		String message = getArguments().getString(EXTRA_MESSAGE);
//		View v = inflater.inflate(R.layout.scan_beacon_layout, container, false);
//		TextView messageTextView = (TextView)v.findViewById(R.id.textView);
//		messageTextView.setText(message);

        View rootView = inflater.inflate(R.layout.scan_beacon_layout, container, false);
        ListView mDeviceListView = (ListView) rootView.findViewById(R.id.listView_scan_disp);
        deviceListAdapter = new DeviceListAdapter(getActivity(), R.id.listView_scan_disp, new ArrayList<BluetoothDevice>());
        mDeviceListView.setAdapter(deviceListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deviceListAdapter.addAll(ArrayBoh.getInstance());

    }

}
