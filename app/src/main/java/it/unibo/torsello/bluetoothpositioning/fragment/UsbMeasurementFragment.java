package it.unibo.torsello.bluetoothpositioning.fragment;

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

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.util.UsbDataUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class UsbMeasurementFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private TextView twDistance;
    private TextView twState;

    private UsbDataUtil usbUtil;

    private DecimalFormat df;

    private double arduinoDistance;

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
        View root = inflater.inflate(R.layout.cardview_arduino_usb, container, false);

        twDistance = (TextView) root.findViewById(R.id.tw_distance_value);
        twState = (TextView) root.findViewById(R.id.tw_state_value);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        usbUtil = new UsbDataUtil(getActivity());
        usbUtil.setOnReceiveNewData(new UsbDataUtil.OnReceiveNewData() {

            @Override
            public void getData(byte[] data) {
                try {
                    arduinoDistance = Double.valueOf(new String(data).trim()) / 100;
                    twDistance.setText(String.format("%s m", df.format(arduinoDistance)));
                } catch (NumberFormatException nfe) {
                }
            }

            @Override
            public void getStatus(String state) {
                twState.setText(state);
            }

            @Override
            public void isEnabled(boolean enabled) {
                if (!enabled) {
                    // reset of distance estimated of arduino
                    twDistance.setText(String.format("%s m", df.format(0)));
                    arduinoDistance = 0.00D;

                    twState.setTextColor(Color.RED);
                } else {
                    twState.setTextColor(Color.GREEN);
                }
            }

        });
    }

    @Override
    public void onPause() {
        super.onPause();
        usbUtil.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        usbUtil.onResume();
    }
}
