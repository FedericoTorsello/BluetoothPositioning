package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoCommunicatorService;
import it.unibo.torsello.bluetoothpositioning.utils.ByteArray;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MeasurementFrag extends Fragment {

    String setText;

    public static MeasurementFrag newInstance() {
        return new MeasurementFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArduinoUSB arduinoUSB = new ArduinoUSB(getActivity());
//        setText = arduinoUSB.getmTransferedDataList().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_measurement, container, false);
        TextView textView = (TextView) root.findViewById(R.id.real_distance);
//        textView.setText(setText);
        return root;
    }

}
