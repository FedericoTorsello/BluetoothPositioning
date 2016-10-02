package it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceCardViewAdapter;
import it.unibo.torsello.bluetoothpositioning.fragment.usbObservers.UsbMeasurementFragment;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.MyDeviceObservable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailInnerFragment extends Fragment implements Observer {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private DeviceCardViewAdapter deviceViewAdapter;
    private List<Device> deviceList;

    private MyDeviceObservable myObservable;

    private String idDeviceSelectedName;

    public static DeviceDetailInnerFragment newInstance(String message) {
        DeviceDetailInnerFragment fragment = new DeviceDetailInnerFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myObservable = MyDeviceObservable.getInstance();
        myObservable.addObserver(this);

        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceCardViewAdapter(getActivity(), deviceList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_inner_detail, container, false);

        initializeDeviceDetail(root);

        return root;
    }

    @Override
    public void onPause() {
        myObservable.deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        myObservable.addObserver(this);
    }

    private void initializeDeviceDetail(View root) {
        // add RecyclerView
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(deviceViewAdapter);
        ((FrameLayout) root.findViewById(R.id.frame_selected_device)).addView(recyclerView);
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof List) {


            if (!deviceList.isEmpty()) {
                deviceList.clear();
            }

            List<Device> devices = (List<Device>) arg;

            for (Device deviceSelected : devices) {
                if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                        deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                    if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                            deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                        deviceList.add(deviceSelected);
                    }

                    deviceViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}
