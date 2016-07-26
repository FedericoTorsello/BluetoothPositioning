package it.unibo.torsello.bluetoothpositioning.utils;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by federico on 25/07/16.
 */
public class Magnetometer implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] accelValues;
    private float[] magnetValues;
    private OnCompassOrientationListener listener;

    public Magnetometer(Application app) {
        //Initialize sensors used for compass.
        mSensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public interface OnCompassOrientationListener {
        void setCompassOrientationString(String compassOrientationString);
    }

    public void setListener(OnCompassOrientationListener orientationListener) {
        listener = orientationListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float rotation[] = new float[9];
        float orientation[] = new float[3];
        float orientationDegree = 0f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelValues = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetValues = event.values;

        if (accelValues != null && magnetValues != null) {
            if (SensorManager.getRotationMatrix(rotation, null, accelValues, magnetValues)) {
                SensorManager.getOrientation(rotation, orientation);
                float azimuthDegree = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
                orientationDegree = Math.round(azimuthDegree);
            }
        }

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

        listener.setCompassOrientationString(compassOrientation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void startDetection() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void killDetection() {
        mSensorManager.unregisterListener(this);
    }


}
