package it.unibo.torsello.bluetoothpositioning.examplesCamera;

/**
 * @author Jose Davis Nidhin
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;

public class CamTestActivity extends Fragment {
    private static final String TAG = "CamTestActivity";
    Preview preview;
    Button buttonClick;
    Camera camera;
    Context ctx;

    public static CamTestActivity newInstance() {
        return new CamTestActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.example, container, false);
        preview = new Preview(getActivity(), (SurfaceView) root.findViewById(R.id.surfaceView));
        ((FrameLayout) root.findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });


        buttonClick = (Button) root.findViewById(R.id.btnCapture);

        buttonClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        buttonClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        //camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    }
                });
                return true;
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            preview.setCamera();
            camera = preview.getCamera();
        } catch (RuntimeException ex) {
            Toast.makeText(ctx, "camera_not_found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        preview.onPause();
        super.onPause();
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, final Camera camera) {
            new SaveImageTask().execute(data);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    camera.startPreview();
                }
            }).start();

            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/camtest");
//                    dir.mkdirs();

                String fileName = String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                FileOutputStream outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}


