package it.unibo.torsello.bluetoothpositioning.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.util.CameraPreviewUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class CameraFragment extends Fragment {

    private CameraPreviewUtil preview;
    private Camera camera;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        preview = new CameraPreviewUtil(getContext(), (SurfaceView) root.findViewById(R.id.surfaceView));
        ((FrameLayout) root.findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                preview.takePicture();
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera arg1) {
//                        if (success) {
//                            preview.takePicture();
//                        }
                    }
                });
            }
        });
        preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {

                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera arg1) {
//                        if (success) {
//                            preview.takePicture();
//                        }
                    }
                });
                return true;
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.fab_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        preview.setCamera(getActivity());
        camera = preview.getCamera();
    }

    @Override
    public void onPause() {
        preview.onPause();
        super.onPause();
    }

}


