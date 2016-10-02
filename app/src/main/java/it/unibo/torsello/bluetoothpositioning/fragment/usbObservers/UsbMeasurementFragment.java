package it.unibo.torsello.bluetoothpositioning.fragment.usbObservers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.observables.MyUsbObservable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class UsbMeasurementFragment extends Fragment implements Observer {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private TextView twDistance;
    private TextView twState;

    private DecimalFormat df;

    private boolean isEnabled;

//    private double arduinoDistance;

    private MyUsbObservable myUsbObservable;

    public static UsbMeasurementFragment newInstance() {
        UsbMeasurementFragment fragment = new UsbMeasurementFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "Measurement");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_arduino_usb, container, false);

        twDistance = (TextView) root.findViewById(R.id.tw_distance_value);
        twState = (TextView) root.findViewById(R.id.tw_state_value);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myUsbObservable = MyUsbObservable.getInstance();
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

    }

    @Override
    public void onPause() {
        myUsbObservable.deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        myUsbObservable.addObserver(this);
    }

    @Override
    public void update(Observable o, final Object arg) {

        if (arg instanceof Double) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Double arduinoDistance = (Double) arg;

                    twDistance.setText(String.format("%s m", df.format(arduinoDistance)));
                }
            });
        } else if (arg instanceof String) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String) arg;
                    twState.setText(message);
                }
            });
        } else if (arg instanceof Boolean) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean state = (Boolean) arg;
                    if (state) {
                        twState.setTextColor(Color.GREEN);
                    } else {
                        twState.setTextColor(Color.RED);
                    }
                }
            });
        }
    }
}
