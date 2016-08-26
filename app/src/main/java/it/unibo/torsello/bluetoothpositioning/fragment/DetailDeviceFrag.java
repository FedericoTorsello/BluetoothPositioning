package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.CameraPreview;

/**
 * Created by ocabafox on 7/8/2015.
 */
public class DetailDeviceFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Camera mCamera;
    private CameraPreview mPreview;


    public static DetailDeviceFrag newInstance(String message) {
        DetailDeviceFrag fragment = new DetailDeviceFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_details, container, false);
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbarLayout != null) {
            String title = getArguments().getString(EXTRA_MESSAGE);
            collapsingToolbarLayout.setTitle(title);
            //collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }

        checkCameraHardware(getContext());
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getContext(), mCamera);
//                FrameLayout preview = (FrameLayout) getActivity().findViewById(R.id.camera_preview);
//        FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
//        preview.addView(mPreview);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab2);
        assert fab != null;
//        Snackbar.make(fab, R.string.snackbar_start_scanning, Snackbar.LENGTH_LONG).show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create Intent to take a picture and return control to the calling application
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                // start the image capture Intent
//                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                Snackbar.make(view, "ciao", Snackbar.LENGTH_SHORT).show();


//
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameLayout, mPreview)
//                        .commit();


                /** A safe way to get an instance of the Camera object. */

            }

        });
        return root;
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}
