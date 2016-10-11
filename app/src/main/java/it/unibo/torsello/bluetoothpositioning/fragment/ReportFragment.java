package it.unibo.torsello.bluetoothpositioning.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.UsbMeasurementObservable;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ReportFragment extends Fragment implements Observer {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String DEVICE_NAME = "DEVICE_NAME";

    private DeviceObservable myDeviceObservable;
    private UsbMeasurementObservable myUsbObservable;

    private String idDeviceSelectedName;

    private boolean check;
    private double arduinoActualValue = 0D;
    private int i = 0;
    private String formattedDate;

    private ArrayList<Double> arduinoValues;
    private ArrayList<Double> rawValues;
    private ArrayList<Double> altBeaconValues;
    private ArrayList<Double> kFilterValues;

    private Gson gson;

    public static ReportFragment newInstance(String message, String deviceName) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        args.putString(DEVICE_NAME, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDeviceObservable = DeviceObservable.getInstance();
        myUsbObservable = UsbMeasurementObservable.getInstance();

        gson = new GsonBuilder().setPrettyPrinting().create();

        idDeviceSelectedName = getArguments().getString(DEVICE_NAME);

        arduinoValues = new ArrayList<>();
        rawValues = new ArrayList<>();
        altBeaconValues = new ArrayList<>();
        kFilterValues = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(Calendar.getInstance().getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_report, container, false);

        final ToggleButton toggle = (ToggleButton) root.findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = isChecked;
                if (isChecked) {

                    arduinoValues.clear();
                    rawValues.clear();
                    altBeaconValues.clear();
                    kFilterValues.clear();

                    toggle.setClickable(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            toggle.setClickable(true);
                            toggle.setChecked(false);


                            File pathRoot = Environment.getExternalStorageDirectory();
                            File dir = new File(pathRoot.getAbsolutePath() + "/"
                                    + getActivity().getString(R.string.app_name)
                                    + " - report");
                            dir.mkdir();

                            String rssiFilterSelected = ((ApplicationActivity) getActivity()).getRssiFilterSelected();
                            String fileName = formattedDate + " - " + idDeviceSelectedName
                                    + " - " + rssiFilterSelected + " - " + i;

                            File newFile = new File(dir, fileName + ".json");

                            try {
//                                Reader reader = new FileReader(newFile);

                                FileInputStream fin = new FileInputStream(newFile);

                                int c;
                                String temp = "";
                                while ((c = fin.read()) != -1) {
                                    temp = temp + Character.toString((char) c);
                                }

//string temp contains all the data of the file.
                                fin.close();


                                TextView textView = (TextView) root.findViewById(R.id.textView);
                                textView.append(temp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }, 5000);

                    Snackbar.make(getActivity().findViewById(R.id.fab),
                            "Start recording", Snackbar.LENGTH_SHORT).show();
                } else {
                    export();
                }
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        myDeviceObservable.deleteObserver(this);
        myUsbObservable.deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        myDeviceObservable.addObserver(this);
        myUsbObservable.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UsbMeasurementObservable) {
            if (arg instanceof Double) {
                arduinoActualValue = (Double) arg;
            }
        }

        if (arg instanceof List) {

            List<Device> devices = (List<Device>) arg;

            for (Device deviceSelected : devices) {
                if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                        deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                    if (check) {
                        arduinoValues.add(arduinoActualValue);
                        rawValues.add(deviceSelected.getRawDistance());
                        altBeaconValues.add(deviceSelected.getAltBeaconDistance());
                        kFilterValues.add(deviceSelected.getKalmanFilterDistance());
                    }
                }
            }
        }
    }

    private void export() {
        if (isExternalStorageWritable()) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/"
                    + getActivity().getString(R.string.app_name)
                    + " - report");
            dir.mkdir();

            String rssiFilterSelected = ((ApplicationActivity) getActivity()).getRssiFilterSelected();
            String fileName = formattedDate + " - " + idDeviceSelectedName
                    + " - " + rssiFilterSelected + " - " + i;

            File newFile = new File(dir, fileName + ".json");

            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writJsonFile(newFile);

            refreshDirectory(newFile);
        }
    }

    private void writJsonFile(File jsonFile) {
        try {
            Writer writer = new FileWriter(jsonFile);

            JsonWriter jw = gson.newJsonWriter(writer);

            SharedPreferences preferences = getActivity().
                    getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
            double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);

            jw.beginObject();
            {
                jw.name("id").value("report " + formattedDate);
                jw.name("distance_estimation");
                jw.beginObject();
                {
                    jw.name("arduino");
                    jw.beginArray();
                    for (Double value : arduinoValues) {
                        jw.value(value);
                    }
                    jw.endArray();

                    jw.name("raw");
                    jw.beginArray();
                    for (Double value : rawValues) {
                        jw.value(value);
                    }
                    jw.endArray();

                    jw.name("altbeacon");
                    jw.beginArray();
                    for (Double value : altBeaconValues) {
                        jw.value(value);
                    }
                    jw.endArray();

                    jw.name("kFilter_processNoise").value(processNoise);

                    jw.name("kFilter");
                    jw.beginArray();
                    for (Double value : kFilterValues) {
                        jw.value(value);
                    }
                    jw.endArray();
                }
                jw.endObject();
            }
            jw.endObject();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void refreshDirectory(File file) {
        if (file != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(mediaScanIntent);

            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Report saved", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Report retrieval failed", Snackbar.LENGTH_SHORT).show();
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.d("sure", "not writeable");
        return false;
    }

}
