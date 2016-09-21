package it.unibo.torsello.bluetoothpositioning.examplesCamera;

/**
 * @author Jose Davis Nidhin
 */

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.unibo.torsello.bluetoothpositioning.R;

public class CamTestActivity extends Fragment {
    Preview preview;
    Camera camera;

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
                preview.takePicture();
            }
        });
        preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        preview.takePicture();
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
        preview.setCamera(getActivity());
        camera = preview.getCamera();
    }

    @Override
    public void onPause() {
        preview.onPause();
        super.onPause();
    }
}


