package it.unibo.torsello.bluetoothpositioning.fragment.oldFragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class CompassMagnoFragment extends Fragment implements SensorEventListener {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float accelValues[];
    private float magnetValues[];

    public static CompassMagnoFragment newInstance(String message) {
        CompassMagnoFragment fragment = new CompassMagnoFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAccelerometer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST,
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            } else {
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        if (mMagnetometer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST,
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            } else {
                mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compass_text, container, false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mAccelerometer) {
            accelValues = event.values;
        } else if (event.sensor == mMagnetometer) {
            magnetValues = event.values;
        }


        if (accelValues != null && magnetValues != null) {
            float[] rotation = new float[16];
            float[] orientation = new float[16];

            SensorManager.getRotationMatrix(rotation, null, accelValues, magnetValues);
            SensorManager.getOrientation(rotation, orientation);


            float azimuthDegree = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            float orientationDegree = Math.round(azimuthDegree);

            String compassOrientation;
            if (orientationDegree >= 0 && orientationDegree < 90) {
                compassOrientation = "N";
            } else if (orientationDegree >= 90 && orientationDegree < 180) {
                compassOrientation = "E";
            } else if (orientationDegree >= 180 && orientationDegree < 270) {
                compassOrientation = "S";
            } else {
                compassOrientation = "W";
            }

//            try {
//                TextView messageTextView = (TextView) getActivity().findViewById(R.id.compass);
//                messageTextView.setText(compassOrientation);
//            }catch (NullPointerException e){
//                e.getStackTrace();
//            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
