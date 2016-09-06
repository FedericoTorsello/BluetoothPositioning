package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.CameraUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFrag extends Fragment {

    private TextureView mTextureView;

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private FrameLayout preview;

    private OnCameraListener onCameraListener;

    public interface OnCameraListener {

        void safeCameraOpenInView(SurfaceTexture surfaceTexture);

        void onSurfaceTextureDestroyed();

        void takePicture();

    }

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

        setTabLayoutVisible(false);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbarLayout != null) {
            String title = getArguments().getString(EXTRA_MESSAGE);
            collapsingToolbarLayout.setTitle(title);
//            collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }

        if (isCameraHardwarePresent()) {

            preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
            preview.addView(mTextureView);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the camera preview.
                    onCameraListener.safeCameraOpenInView(mTextureView.getSurfaceTexture());
                }
            });

            FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab_camera);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCameraListener.takePicture();
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

        mTextureView = new TextureView(getActivity());
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {

                if (surface == null) {
                    // preview surface does not exist
                    return;
                }

                // Restart the camera preview.
                onCameraListener.safeCameraOpenInView(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                onCameraListener.onSurfaceTextureDestroyed();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Invoked every time there's a new CameraUtil preview frame
            }
        });

        if (onCameraListener == null) {
            onCameraListener = new CameraUtil(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mTextureView = null;
        preview.removeAllViews();
        onCameraListener = null;

        setTabLayoutVisible(true);
    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    private void setTabLayoutVisible(boolean isTabLayoutVisible) {
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        assert tabLayout != null;

        if (isTabLayoutVisible) {
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }
    }

}
