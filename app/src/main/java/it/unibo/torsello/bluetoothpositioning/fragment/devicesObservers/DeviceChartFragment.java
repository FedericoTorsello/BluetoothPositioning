package it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.UsbMeasurementObservable;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;
import it.unibo.torsello.bluetoothpositioning.util.Report;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceChartFragment extends Fragment implements Observer {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String ID = "ID";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String STRINGS = "STRINGS";

    private DeviceObservable myDeviceObservable;
    private UsbMeasurementObservable myUsbObservable;

    private String chartName;
    private int id;
    private String idDeviceSelected;
    private ChartUtil chartUtil;

    private double arduinoValue = 0D;

    private Gson gson;
    private Report report;

    private String formattedDate;

    private ArrayList<String> stringArrayList;

    private boolean check;

    public static DeviceChartFragment newInstance(String message, int id, String deviceName,
                                                  ArrayList<String> strings) {
        DeviceChartFragment fragment = new DeviceChartFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        args.putInt(ID, id);
        args.putString(DEVICE_NAME, deviceName);
        args.putStringArrayList(STRINGS, strings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(ID);
        chartName = getArguments().getString(EXTRA_MESSAGE);
        idDeviceSelected = getArguments().getString(DEVICE_NAME);
        stringArrayList = getArguments().getStringArrayList(STRINGS);

        myDeviceObservable = DeviceObservable.getInstance();
        myUsbObservable = UsbMeasurementObservable.getInstance();

        chartUtil = new ChartUtil(getActivity());

        gson = new GsonBuilder().setPrettyPrinting().create();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(Calendar.getInstance().getTime());

        report = new Report();
        report.setId(id);
        report.setName(chartName);
        report.setDate(formattedDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_device_chart, container, false);

        Button button = (Button) root.findViewById(R.id.button_save_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartUtil.saveImageChart(chartName, formattedDate);
            }
        });

        ToggleButton toggle = (ToggleButton) root.findViewById(R.id.toggle_button_record_data);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = isChecked;
                if (isChecked) {
                    Snackbar.make(getActivity().findViewById(R.id.fab),
                            "Start recording", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fab),
                            "Stop recording", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        LineChart lineChart = (LineChart) root.findViewById(R.id.chart);

        // add the charts
        chartUtil.setChart(lineChart);

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
        chartUtil.initializeChart();
    }

    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof UsbMeasurementObservable) {
            if (arg instanceof Double) {
                arduinoValue = (Double) arg;
            }
        }

        if (o instanceof DeviceObservable) {
            if (arg instanceof List) {

                List<Device> devices = (List<Device>) arg;

                for (Device deviceSelected : devices) {
                    if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                            deviceSelected.getAddress().equals(idDeviceSelected)) {

                        if (chartUtil != null) {

                            chartUtil.createDataSet(stringArrayList);

                            ArrayList<Double> doubleArrayList = new ArrayList<>();

                            for (String s : stringArrayList) {
                                if (s.equals(getString(R.string.chart_arduino))) {
                                    doubleArrayList.add(arduinoValue);
                                    report.setArduinoValue(arduinoValue);
                                }

                                if (s.equals(getString(R.string.chart_raw_distance))) {
                                    doubleArrayList.add(deviceSelected.getRawDistance());
                                    report.setRawDistance(deviceSelected.getRawDistance());
                                }

                                if (s.equals(getString(R.string.chart_altbeacon))) {
                                    doubleArrayList.add(deviceSelected.getAltBeaconDistance());
                                    report.setAltBeaconDistance(deviceSelected.getAltBeaconDistance());
                                }

                                if (s.equals(getString(R.string.chart_kalman_filter))) {
                                    doubleArrayList.add(deviceSelected.getKalmanFilterDistance());
                                    report.setKalmanFilterDistance(deviceSelected.getKalmanFilterDistance());
                                }
                            }

                            chartUtil.updateDataSet(doubleArrayList);

                            if (check) {
                                String userJson = gson.toJson(report);
                            }
                        }
                    }
                }
            }
        }
    }

}
