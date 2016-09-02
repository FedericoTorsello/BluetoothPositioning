package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFrag extends Fragment implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private Thread preview_thread;
    private TextureView mTextureView;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private final String TAG_CLASS = getClass().getSimpleName();

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

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbarLayout != null) {
            String title = getArguments().getString(EXTRA_MESSAGE);
            collapsingToolbarLayout.setTitle(title);
//            collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }

        if (checkCameraHardware()) {

            if (mCamera == null) {
                mCamera = getCameraInstance();
            }

            mTextureView = new TextureView(getActivity());
            mTextureView.requestLayout();
            mTextureView.setSurfaceTextureListener(this);
            FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
            preview.addView(mTextureView);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    safeCameraOpenInView(mTextureView.getSurfaceTexture());
                }
            });

            FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCamera != null) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        }

        return root;
    }

    // Restart the camera preview.
    private void safeCameraOpenInView(SurfaceTexture surface) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException ioe) {
                ioe.getStackTrace();
            }

            preview_thread = new Thread() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            };
            preview_thread.start();

        }

    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware() {
        return (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {

        Camera c = null;

        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (RuntimeException e) {
            // Camera is not available (in use or does not exist)
            e.getStackTrace();
        }

        return c; // returns null if camera is unavailable
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {

        if (surface == null) {
            // preview surface does not exist
            return;
        }

        safeCameraOpenInView(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            if (!preview_thread.isInterrupted()) {
                preview_thread.interrupt();
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                assert fab != null;
                Snackbar.make(fab, "Image retrieval failed.", Snackbar.LENGTH_SHORT);
            } else {
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Used to return the camera File output.
     *
     * @return
     */
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        new AlertDialog.Builder(getActivity())
                .setTitle("Success!")
                .setMessage("Your picture has been saved!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

        return mediaFile;
    }

}
