package it.unibo.torsello.bluetoothpositioning.fragment;

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

    private TextView twDistance;
    private TextView twState;
    private UsbDataUtil usbUtil;

    public static UsbMeasurementFragment newInstance() {
        return new UsbMeasurementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_usb, container, false);

        initializeArduinoDistance(root);


        return root;
    }

    private void initializeArduinoDistance(View root) {
        twDistance = (TextView) root.findViewById(R.id.tw_distance_value);
        twState = (TextView) root.findViewById(R.id.tw_state_value);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usbUtil = new UsbDataUtil(getActivity());

        usbUtil.setOnReceiveNewData(new UsbDataUtil.OnReceiveNewData() {

            @Override
            public void getData(byte[] data) {

                final String text = new String(data);

                try {
                    float meter = Float.valueOf(text.trim()) / 100;
                    DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
                    twDistance.setText(String.format("%s m", df.format(meter)));
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
                    twDistance.setText("0.00 m");
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
