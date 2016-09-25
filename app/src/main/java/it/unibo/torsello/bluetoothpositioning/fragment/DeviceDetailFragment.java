package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceCardViewAdapter;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.util.CameraUtil;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;
import it.unibo.torsello.bluetoothpositioning.util.UsbDataUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFragment extends Fragment implements ApplicationActivity.OnAddDevicesListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private DeviceCardViewAdapter deviceViewAdapter;
    private List<Device> deviceList;

    private CameraUtil cameraUtil;

    private ChartUtil chartUtil1;
    private ChartUtil chartUtil2;

    private TextView twDistance;
    private TextView twState;

    private UsbDataUtil usbUtil;

    private String idDeviceSelectedName;
    private float arduinoDistance;
    private boolean usbEnabled;

    private DecimalFormat df;

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

        ((CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar)).setTitle(idDeviceSelectedName);

        initializeDeviceDetail(root);

        initializeArduinoDistance(root);

        initializeCamera(root);

        initializeChart(root);

        return root;
    }

    private void initializeDeviceDetail(View root) {
        // add RecyclerView
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(deviceViewAdapter);
        ((FrameLayout) root.findViewById(R.id.frame_selected_device)).addView(recyclerView);
    }

    private void initializeArduinoDistance(View root) {
        twDistance = (TextView) root.findViewById(R.id.tw_distance_value);
        twState = (TextView) root.findViewById(R.id.tw_state_value);
    }

    private void initializeCamera(View root) {

        if (cameraUtil != null) {

            final TextureView mTextureView = cameraUtil.getmTextureView();

            // programmatically add camera preview
            FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview);
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
        // add the charts
        LineChart lineChart = (LineChart) root.findViewById(R.id.chart1);
        LineChart lineChart2 = (LineChart) root.findViewById(R.id.chart2);

        chartUtil1 = new ChartUtil(getActivity(), lineChart);
        chartUtil2 = new ChartUtil(getActivity(), lineChart2);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceCardViewAdapter(getActivity(), deviceList);

        cameraUtil = new CameraUtil(getActivity());

        usbUtil = new UsbDataUtil(getActivity());
        usbUtil.setOnReceiveNewData(new UsbDataUtil.OnReceiveNewData() {

            @Override
            public void getData(byte[] data) {
                try {
                    arduinoDistance = Float.valueOf(new String(data).trim()) / 100;
                    twDistance.setText(String.format("%s m", df.format(arduinoDistance)));
                } catch (NumberFormatException nfe) {
                }
            }

            @Override
            public void getStatus(String state) {
                twState.setText(state);
            }

            @Override
            public void isEnabled(boolean isEnabled) {
                usbEnabled = isEnabled;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        usbUtil.onPause();
        cameraUtil.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        usbUtil.onResume();
        cameraUtil.onResume();
    }

    @Override
    public void updateInfoDevices(final List<Device> devices) {

        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }

        for (Device deviceSelected : devices) {
            if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                    deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                if (!usbEnabled) {
                    // reset of distance estimated of arduino
                    twDistance.setText(String.format("%s m", df.format(0)));
                    arduinoDistance = 0.00f;

                    twState.setTextColor(Color.RED);
                } else {
                    twState.setTextColor(Color.GREEN);
                }

                if (chartUtil1 != null) {
                    chartUtil1.createDataSet("AltBeacon", "Kalman Filter");
                    chartUtil1.updateDataSet(arduinoDistance,
                            deviceSelected.getAltBeaconDistance(),
                            deviceSelected.getKalmanFilterDistance());
                }

                if (chartUtil2 != null) {
                    chartUtil2.createDataSet("AltBeacon", "Raw distance");
                    chartUtil2.updateDataSet(arduinoDistance,
                            deviceSelected.getAltBeaconDistance(),
                            deviceSelected.getRawDistance());
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
