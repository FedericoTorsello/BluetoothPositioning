package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import it.unibo.torsello.bluetoothpositioning.adapter.StatePagerAdapter;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.util.CameraUtil;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;
import it.unibo.torsello.bluetoothpositioning.util.UsbDataUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFragmentProva extends Fragment implements ApplicationActivity.OnAddDevicesListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private DeviceCardViewAdapter deviceViewAdapter;
    private List<Device> deviceList;

    private CameraUtil cameraUtil;

    private String idDeviceSelectedName;
    private double arduinoDistance;
    private boolean usbEnabled;

    private DecimalFormat df;

    public static DeviceDetailFragmentProva newInstance(String message) {
        DeviceDetailFragmentProva fragment = new DeviceDetailFragmentProva();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_details_prova, container, false);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        ((CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar)).setTitle(idDeviceSelectedName);

        initializeCamera(root);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(InnerFragment.newInstance("1"));
        fragments.add(InnerFragment.newInstance("2"));

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.pag);
        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getFragmentManager(), fragments);
        mViewPager.setAdapter(myPageAdapter);
//
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs2);
        tabLayout.setupWithViewPager(mViewPager);

        return root;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceCardViewAdapter(getActivity(), deviceList);

        cameraUtil = new CameraUtil(getActivity());

    }

    @Override
    public void onPause() {
        cameraUtil.onPause();

        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraUtil.onResume();
    }

    @Override
    public void updateInfoDevices(final List<Device> devices) {

        if (getActivity() != null) {

            if (!deviceList.isEmpty()) {
                deviceList.clear();
            }

            for (Device deviceSelected : devices) {
                if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                        deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                    deviceList.add(deviceSelected);
                }


                deviceViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


}
