package it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.UsbMeasurementObservable;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;

public class DeviceChartFragment extends Fragment implements Observer {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String STRINGS = "STRINGS";

    private DeviceObservable myDeviceObservable;
    private UsbMeasurementObservable myUsbObservable;

    private String idDeviceSelected;
    private ChartUtil chartUtil;

    private Double arduinoValue = 0D;

    private ArrayList<String> stringArrayList;

    public static DeviceChartFragment newInstance(String message, String deviceName,
                                                  ArrayList<String> strings) {
        DeviceChartFragment fragment = new DeviceChartFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        args.putString(DEVICE_NAME, deviceName);
        args.putStringArrayList(STRINGS, strings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDeviceObservable = DeviceObservable.getInstance();
        myUsbObservable = UsbMeasurementObservable.getInstance();

        idDeviceSelected = getArguments().getString(DEVICE_NAME);
        stringArrayList = getArguments().getStringArrayList(STRINGS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_device_chart, container, false);

        LineChart lineChart = (LineChart) root.findViewById(R.id.chart);

        // add the charts
        chartUtil = new ChartUtil(getActivity(), lineChart);

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
                                }

                                if (s.equals(getString(R.string.chart_raw_distance))) {
                                    doubleArrayList.add(deviceSelected.getRawDistance());
                                }

                                if (s.equals(getString(R.string.chart_altbeacon))) {
                                    doubleArrayList.add(deviceSelected.getAltBeaconDistance());
                                }

                                if (s.equals(getString(R.string.chart_kalman_filter))) {
                                    doubleArrayList.add(deviceSelected.getKalmanFilterDistance());
                                }
                            }

                            chartUtil.updateDataSet(doubleArrayList);
                        }
                    }
                }
            }
        }
    }

}