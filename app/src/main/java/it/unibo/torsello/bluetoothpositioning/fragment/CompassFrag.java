package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.Magnetometer;

public class CompassFrag extends Fragment implements Magnetometer.OnCompassOrientationListener {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private Magnetometer magnetometer;

    public static CompassFrag newInstance(String message) {
        CompassFrag f = new CompassFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        magnetometer = new Magnetometer(getActivity().getApplication());
        magnetometer.startDetection();
        magnetometer.setListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        magnetometer.killDetection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        TextView messageTextView = (TextView) v.findViewById(R.id.textView);
        messageTextView.setText("");

        return v;
    }

    @Override
    public void setCompassOrientationString(String compassOrientationString) {
        TextView messageTextView = (TextView) getActivity().findViewById(R.id.textView);
        messageTextView.setText(compassOrientationString);
    }
}
