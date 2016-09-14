package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceViewAdapter;
import it.unibo.torsello.bluetoothpositioning.models.Device;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoUtil;
import it.unibo.torsello.bluetoothpositioning.utils.CameraUtil;
import it.unibo.torsello.bluetoothpositioning.utils.ChartUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFragment extends Fragment implements ApplicationActivity.OnAddDevicesListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private DeviceViewAdapter deviceViewAdapter;
    private List<Device> deviceList;

    private CameraUtil cameraUtil;
    private TextureView mTextureView;

    private ChartUtil chartUtil;

    private ArduinoUtil arduinoUtil;
    private TextView estimateDistanceRAW;

    private TextView textView;

    private String idDeviceSelected;

    public static DeviceDetailFragment newInstance(String message) {
        DeviceDetailFragment fragment = new DeviceDetailFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_details, container, false);

        getActivity().findViewById(R.id.sliding_tabs).setVisibility(View.GONE);

        ((CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar)).setTitle(idDeviceSelected);

        // add RecyclerView
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(deviceViewAdapter);
        ((FrameLayout) root.findViewById(R.id.frame_selected_device)).addView(recyclerView);

        initializeCamera(root);

        initializeChart(root);

        estimateDistanceRAW = (TextView) root.findViewById(R.id.prova);

        textView = (TextView) getActivity().findViewById(R.id.arduinoMis);

        return root;
    }

    private void initializeCamera(View root) {

        if (isCameraHardwarePresent()) {
            cameraUtil = new CameraUtil(getActivity());

            mTextureView = cameraUtil.getmTextureView();
            FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
            preview.addView(mTextureView);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the camera preview.
                    cameraUtil.safeCameraOpenInView(mTextureView.getSurfaceTexture());
                }
            });

            root.findViewById(R.id.fab_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraUtil.takePicture();
                }
            });

        } else {
            Toast.makeText(getActivity(), "No camera onCameraListener this device", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void initializeChart(View root) {
        // programatically add the chart
        LineChart lineChart = new LineChart(getActivity());
        ((FrameLayout) root.findViewById(R.id.chart)).addView(lineChart);

        chartUtil = new ChartUtil(getActivity(), lineChart);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idDeviceSelected = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceViewAdapter(getActivity(), deviceList);
        arduinoUtil = new ArduinoUtil(getActivity()
//                ,textView
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        arduinoUtil.createService();
    }

    @Override
    public void onPause() {
        super.onPause();

        arduinoUtil.unregisterReceiver();
    }


    @Override
    public void updateInfoDevices(final List<Device> devices) {

        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }

        for (Device deviceSelected : devices) {
            if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                    deviceSelected.getAddress().equals(idDeviceSelected)) {

                estimateDistanceRAW.setText(String.format(Locale.getDefault(), "%.2f", deviceSelected.getDistNoFilter1()));

                if (chartUtil != null) {
                    chartUtil.updateDataSet(deviceSelected);
                }

                deviceList.add(deviceSelected);
            }
        }

        deviceViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void clearList() {
    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


}
