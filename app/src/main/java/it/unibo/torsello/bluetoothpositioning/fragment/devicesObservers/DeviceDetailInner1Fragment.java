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
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailInner1Fragment extends Fragment implements Observer {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private DeviceCardViewAdapter deviceViewAdapter;
    private List<Device> deviceList;
    private DeviceObservable myObservable;
    private String idDeviceSelectedName;

    public static DeviceDetailInner1Fragment newInstance(String message) {
        DeviceDetailInner1Fragment fragment = new DeviceDetailInner1Fragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myObservable = DeviceObservable.getInstance();

        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceCardViewAdapter(getActivity(), deviceList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_detail_inner_1, container, false);

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
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceViewAdapter);
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
                    deviceList.add(deviceSelected);
                }
            }

            deviceViewAdapter.notifyDataSetChanged();
        }
    }

}
