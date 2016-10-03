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
import it.unibo.torsello.bluetoothpositioning.observables.MyDeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.MyUsbObservable;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;

public class DeviceChartFragment extends Fragment implements Observer {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String STRINGS = "STRINGS";

    private MyDeviceObservable myDeviceObservable;
    private MyUsbObservable myUsbObservable;

    private String idDeviceSelected;
    private ChartUtil chartUtil;

    private Double arduinoValue = 0D;

    private ArrayList<String> stringArrayList;

    private Double param0, param1, param2, param3;


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
        idDeviceSelected = getArguments().getString(DEVICE_NAME);
        stringArrayList = getArguments().getStringArrayList(STRINGS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chart, container, false);

        myDeviceObservable = MyDeviceObservable.getInstance();
        myUsbObservable = MyUsbObservable.getInstance();

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

        if (o instanceof MyUsbObservable) {
            if (arg instanceof Double) {
                arduinoValue = (Double) arg;
            }
        }

        if (o instanceof MyDeviceObservable) {
            if (arg instanceof List) {

                List<Device> devices = (List<Device>) arg;

                for (Device deviceSelected : devices) {
                    if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                            deviceSelected.getAddress().equals(idDeviceSelected)) {

                        if (chartUtil != null) {

                            chartUtil.createDataSet(stringArrayList);

//                            if (stringArrayList != null){
//                                if (stringArrayList.equals(getString(R.string.chart_arduino))){
//                                    param0 = arduinoValue;
//                                } else if (stringArrayList.equals(getString(R.string.chart_raw_distance))){
//                                    param0 = deviceSelected.getRawDistance();
//                                } else if (stringArrayList.equals(getString(R.string.chart_altbeacon))){
//                                    param0 = deviceSelected.getAltBeaconDistance();
//                                } else if (stringArrayList.equals(getString(R.string.chart_kalman_filter))){
//                                    param0 = deviceSelected.getKalmanFilterDistance();
//                                }
//                            } else {
//                                param0 = null;
//                            }
//
//                            if (arg1 != null){
//                                if (arg1.equals(getString(R.string.chart_arduino))){
//                                    param1 = arduinoValue;
//                                } else if (arg1.equals(getString(R.string.chart_raw_distance))){
//                                    param1 = deviceSelected.getRawDistance();
//                                } else if (arg1.equals(getString(R.string.chart_altbeacon))){
//                                    param1 = deviceSelected.getAltBeaconDistance();
//                                } else if (arg1.equals(getString(R.string.chart_kalman_filter))){
//                                    param1 = deviceSelected.getKalmanFilterDistance();
//                                }
//                            }else {
//                                param1 = null;
//                            }
//
//                            if (arg2 != null){
//                                if (arg2.equals(getString(R.string.chart_arduino))){
//                                    param2 = arduinoValue;
//                                } else if (arg2.equals(getString(R.string.chart_raw_distance))){
//                                    param2 = deviceSelected.getRawDistance();
//                                } else if (arg2.equals(getString(R.string.chart_altbeacon))){
//                                    param2 = deviceSelected.getAltBeaconDistance();
//                                } else if (arg2.equals(getString(R.string.chart_kalman_filter))){
//                                    param2 = deviceSelected.getKalmanFilterDistance();
//                                }
//                            }else {
//                                param2 = null;
//                            }
//
//                            if (arg3 != null){
//                                if (arg3.equals(getString(R.string.chart_arduino))){
//                                    param3 = arduinoValue;
//                                } else if (arg3.equals(getString(R.string.chart_raw_distance))){
//                                    param3 = deviceSelected.getRawDistance();
//                                } else if (arg3.equals(getString(R.string.chart_altbeacon))){
//                                    param3 = deviceSelected.getAltBeaconDistance();
//                                } else if (arg3.equals(getString(R.string.chart_kalman_filter))){
//                                    param3 = deviceSelected.getKalmanFilterDistance();
//                                }
//                            }else {
//                                param3 = null;
//                            }

//                            chartUtil.updateDataSet(param0, param1, param2, param3);

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
