package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceViewAdapter;
import it.unibo.torsello.bluetoothpositioning.models.Device;
import it.unibo.torsello.bluetoothpositioning.utils.CameraUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFrag extends Fragment implements ApplicationActivity.OnAddDevicesListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private CameraUtil cameraUtil;
    private FrameLayout preview;
    private TextureView mTextureView;
    private DeviceViewAdapter deviceViewAdapter;
    private List<Device> deviceList;

    String idDeviceSelected;

    public static DeviceDetailFrag newInstance(String message) {
        DeviceDetailFrag fragment = new DeviceDetailFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_details, container, false);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setVisibility(View.GONE);

        idDeviceSelected = getArguments().getString(EXTRA_MESSAGE);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbarLayout != null) {

            collapsingToolbarLayout.setTitle(idDeviceSelected);
//            collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }

        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        deviceViewAdapter = new DeviceViewAdapter(getActivity(), deviceList);
        recyclerView.setAdapter(deviceViewAdapter);
        FrameLayout frameLayout = (FrameLayout) root.findViewById(R.id.prova);
        frameLayout.addView(recyclerView);

        if (isCameraHardwarePresent()) {

            preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
            mTextureView = cameraUtil.getmTextureView();
            preview.addView(mTextureView);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the camera preview.
                    cameraUtil.safeCameraOpenInView(mTextureView.getSurfaceTexture());
                }
            });

            FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab_camera);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraUtil.takePicture();
                }
            });

        } else {
            Toast.makeText(getActivity(), "No camera onCameraListener this device", Toast.LENGTH_LONG)
                    .show();
        }

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceList = new ArrayList<>();

        if (cameraUtil == null) {
            cameraUtil = new CameraUtil(getActivity());
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        mTextureView = null;
        preview.removeAllViews();

    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    @Override
    public void updateInfoDevices(List<Device> devices) {

        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }

        for (Device deviceSelected : devices) {
            if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                    deviceSelected.getAddress().equals(idDeviceSelected)) {
                deviceList.add(deviceSelected);
            }
        }

        deviceViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
    }
}
