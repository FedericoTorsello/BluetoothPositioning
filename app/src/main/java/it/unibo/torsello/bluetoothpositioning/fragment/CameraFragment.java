package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.util.CameraUtil;

/**
 * Created by federico on 28/09/16.
 */

public class CameraFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private CameraUtil cameraUtil;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, "Camera");
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        initializeCamera(root);

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
        } else {
            Toast.makeText(getActivity(), "No camera onCameraListener this device", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraUtil = new CameraUtil(getActivity());
        cameraUtil.initialize();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.fab_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtil.takePicture();
            }
        });

    }

    @Override
    public void onPause() {
        cameraUtil.onPause();
        super.onPause();
    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
