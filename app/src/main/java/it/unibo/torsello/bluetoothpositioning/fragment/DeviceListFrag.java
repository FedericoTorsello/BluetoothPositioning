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

package it.unibo.torsello.bluetoothpositioning.fragment;

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

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceViewAdapter;
import it.unibo.torsello.bluetoothpositioning.constants.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceListFrag extends Fragment implements ApplicationActivity.OnAddDevicesListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private DeviceViewAdapter deviceViewAdapter;
    private SharedPreferences settings;
    private List<Device> deviceList;

    public static DeviceListFrag newInstance(String message) {
        DeviceListFrag fragment = new DeviceListFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceList = new ArrayList<>();
        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.frag_device_list, container, false);

        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceViewAdapter = new DeviceViewAdapter(getActivity(), deviceList);
        recyclerView.setAdapter(deviceViewAdapter);

        ((FrameLayout) root.findViewById(R.id.frame_list_device)).addView(recyclerView);

        return root;
    }

    @Override
    public void updateInfoDevices(List<Device> devices) {

        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }

        // optional sorting
        Collections.sort(devices, new Comparator<Device>() {

            public int compare(Device b1, Device b2) {
                int sorting = settings.getInt(SettingConstants.SORTING, 0);
                switch (sorting) {
                    case R.id.radioButton_color_sorting:
                        return Double.compare(b1.getColor(), b2.getColor());
                    case R.id.radioButton_distance_sorting:
                        return Double.compare(b1.getDistNoFilter2(), b2.getDistNoFilter2());
                } // default sorting (a good basic ordering for the other options)
                return Double.compare(b1.getIndex(), b2.getIndex());
            }
        });

        deviceList.addAll(devices);
        deviceViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }
        deviceViewAdapter.notifyDataSetChanged();
    }

}
