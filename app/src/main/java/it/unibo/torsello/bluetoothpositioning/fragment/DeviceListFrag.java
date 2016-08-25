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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.RecyclerViewAdapter;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.main.BLEPositioning;
import it.unibo.torsello.bluetoothpositioning.models.Device;

//import com.bumptech.glide.Glide;

public class DeviceListFrag extends Fragment implements BLEPositioning.OnAddDevicesListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private RecyclerViewAdapter bluetoothDeviceAdapter;
    private SharedPreferences settings;
    private List<Device> devices;

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
        devices = new ArrayList<>();
        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.frag_list_device, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bluetoothDeviceAdapter = new RecyclerViewAdapter(getActivity(), devices);
        recyclerView.setAdapter(bluetoothDeviceAdapter);

        return root;
    }

    @Override
    public void addDevices(Collection<Device> iBeacons) {
        List<Device> list = new ArrayList<>();
        list.addAll(iBeacons);

        Comparator<Device> comparator = new Comparator<Device>() {
            public int compare(Device b1, Device b2) {
                if (settings.getBoolean(SettingConstants.SORT_BY_DISTANCE, false)) {
                    return Double.compare(b1.getDist(), b2.getDist());
                } else {
                    return 0;
                }
            }
        };

        Collections.sort(list, comparator);

        devices.clear();
        devices.addAll(list);
        bluetoothDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        devices.clear();
        bluetoothDeviceAdapter.notifyDataSetChanged();
    }

}
