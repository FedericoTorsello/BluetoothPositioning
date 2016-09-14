package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MeasurementFragment extends Fragment {

    private ArduinoUtil arduinoUtil;
    private TextView textView;

    public static MeasurementFragment newInstance() {
        return new MeasurementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_measurement, container, false);

        textView = (TextView) getActivity().findViewById(R.id.arduinoMis);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arduinoUtil = new ArduinoUtil(getActivity()
//                , textView
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        arduinoUtil.createService();


    }

    @Override
    public void onPause() {
        super.onPause();

        arduinoUtil.unregisterReceiver();
    }


}
