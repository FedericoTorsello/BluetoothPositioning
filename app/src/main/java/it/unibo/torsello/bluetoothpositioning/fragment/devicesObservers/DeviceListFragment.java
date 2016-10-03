/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers;

import android.content.SharedPreferences;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceCardViewAdapter;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceListFragment extends Fragment implements Observer {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private DeviceCardViewAdapter deviceViewAdapter;
    private SharedPreferences preferences;
    private List<Device> deviceList;
    private DeviceObservable myObservable;

    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "Scan Device");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myObservable = DeviceObservable.getInstance();

        deviceList = new ArrayList<>();
        preferences = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_device_list, container, false);

        initializeDeviceDetail(root);

        return root;
    }

    private void initializeDeviceDetail(View root) {
        // add RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceViewAdapter = new DeviceCardViewAdapter(getActivity(), deviceList);
        recyclerView.setAdapter(deviceViewAdapter);
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

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof List) {

            if (!deviceList.isEmpty()) {
                deviceList.clear();
            }

            List<Device> devices = (List<Device>) arg;

            // optional sorting
            Collections.sort(devices, new Comparator<Device>() {
                public int compare(Device b1, Device b2) {
                    int sorting = preferences.getInt(SettingConstants.DISTANCE_SORTING, 0);
                    switch (sorting) {
                        case 0:
                        case R.id.radioButton_default_sorting:
                            return Double.compare(b1.getIndex(), b2.getIndex());
                        case R.id.radioButton_color_sorting:
                            return Double.compare(b1.getColor(), b2.getColor());
                        case R.id.radioButton_distance_sorting:
                            return Double.compare(b1.getKalmanFilterDistance(), b2.getKalmanFilterDistance());
                    } // default sorting (a good basic ordering for the other options)
                    return Double.compare(b1.getIndex(), b2.getIndex());
                }
            });

            deviceList.addAll(devices);
            deviceViewAdapter.notifyDataSetChanged();
        }
    }

}
