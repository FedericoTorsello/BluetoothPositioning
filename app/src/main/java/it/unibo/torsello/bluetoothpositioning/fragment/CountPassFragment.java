package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class CountPassFragment extends Fragment implements SensorEventListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    private Sensor mStepDetector;
    private Sensor mAccellSensor;
    private long lastTime = 0L;
    private int steps = 0;
    private int tempCount;//For counting real steps without external influence.

    public static CountPassFragment newInstance(String message) {
        CountPassFragment f = new CountPassFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        if (mStepCounter == null && mStepDetector == null) {
            mAccellSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mStepCounter != null) {
                mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST,
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
//                mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
            }

            if (mStepDetector != null) {
                mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST,
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
//                mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        if (mStepCounter == null && mStepDetector == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSensorManager.registerListener(this, mAccellSensor, SensorManager.SENSOR_DELAY_FASTEST,
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            } else {
                mSensorManager.registerListener(this, mAccellSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (mStepCounter != null && mStepDetector != null) {
            mSensorManager.unregisterListener(this, mStepCounter);
            mSensorManager.unregisterListener(this, mStepDetector);
        } else {
            mSensorManager.unregisterListener(this, mAccellSensor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_count_pass, container, false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor == mStepCounter) {
//            stepCounter = event.values;
//            Log.d("stepCount" , String.valueOf(stepCounter[0]));
//        } else if (event.sensor == mStepDetector) {
//            stepDetector = event.values;
//            Log.d("stepDetector" , String.valueOf(stepDetector[0]));
//        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                Log.d("stepCount", String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_STEP_COUNTER:
                Log.d("stepDetector", String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_ACCELEROMETER: {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float accelationSquareRoot = Math.abs((x * x + y * y + z * z)
                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)) - 1.0f;
                long actualTime = System.currentTimeMillis();

                if (actualTime - lastTime > 300) {
                    if (accelationSquareRoot < -0.45f) {
                        steps++;
                        tempCount++;
                    }
                    lastTime = actualTime;
                }

                TextView textView = (TextView) getActivity().findViewById(R.id.countPassText);
                textView.setText(String.valueOf(steps));
            }
            break;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
