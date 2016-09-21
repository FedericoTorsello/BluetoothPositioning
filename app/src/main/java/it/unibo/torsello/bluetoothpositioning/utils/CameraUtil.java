package it.unibo.torsello.bluetoothpositioning.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.TextureView;

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
public class CameraUtil implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private Camera mCamera;
    private Thread preview_thread;
    private FragmentActivity fragmentActivity;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Snackbar.make(fragmentActivity.findViewById(R.id.fab),
                        "Image retrieval failed.", Snackbar.LENGTH_SHORT);
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

    public CameraUtil(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        initialize();
    }

    /**
     * A safe way to get an instance of the CameraUtil object.
     */
    private static Camera getCameraInstance() {

        Camera c = null;

        try {
            c = Camera.open(); // attempt to get a CameraUtil instance
        } catch (RuntimeException e) {
            // CameraUtil is not available (in use or does not exist)
            e.getStackTrace();
        }

        return c; // returns null if camera is unavailable
    }

    private void initialize() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }

        mTextureView = new TextureView(fragmentActivity);
        mTextureView.setSurfaceTextureListener(this);

    }

    public void onResume() {
        initialize();
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    public void takePicture() {

        if (mCamera != null) {
            // get an image from the camera
            mCamera.takePicture(null, null, mPicture);
        }

    }

    public void safeCameraOpenInView(SurfaceTexture surface) {

        if (mCamera != null) {

            if (preview_thread != null)
                preview_thread.interrupt();

            preview_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            });

            preview_thread.start();

            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException ioe) {
                ioe.getStackTrace();
            }
        }
    }

    /**
     * Used to return the camera File output.
     *
     * @return
     */
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), fragmentActivity.getString(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("CameraUtil Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        new AlertDialog.Builder(fragmentActivity)
                .setTitle("Success!")
                .setMessage("Your picture has been saved!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

        return mediaFile;
    }

    public TextureView getmTextureView() {
        return mTextureView;
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {

        if (surface == null) {
            // preview surface does not exist
            return;
        }

        // Restart the camera preview.
        safeCameraOpenInView(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            if (!preview_thread.isInterrupted()) {
                preview_thread.interrupt();
            }
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new CameraUtil preview frame
    }

    public void onPause() {
        if (mCamera != null) {
            if (!preview_thread.isInterrupted()) {
                preview_thread.interrupt();
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
