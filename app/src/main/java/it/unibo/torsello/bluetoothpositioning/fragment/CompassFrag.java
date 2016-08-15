package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by federico on 25/07/16.
 */
public class CompassFrag extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float accelValues[];
    private float magnetValues[];

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static CompassFrag newInstance(String message) {
        CompassFrag f = new CompassFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compass_frag, container, false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            accelValues = event.values;
        } else if (event.sensor == mMagnetometer) {
            magnetValues = event.values;
        }

        if (accelValues != null && magnetValues != null) {
            float[] rotationMatrix = new float[16];
            float[] orientation = new float[16];

            SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magnetValues);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthDegree = (float) Math.toDegrees(-orientation[0]);

            RotateAnimation ra = new RotateAnimation(
                    azimuthDegree, azimuthDegree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);

            try {
                ImageView mPointer = (ImageView) getActivity().findViewById(R.id.pointer);
                mPointer.startAnimation(ra);
            } catch (NullPointerException e) {
                e.getStackTrace();
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
