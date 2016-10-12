package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;

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

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceChartFragment extends Fragment implements Observer {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String ID = "ID";
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String STRINGS = "STRINGS";

    private DeviceObservable myDeviceObservable;
    private UsbMeasurementObservable myUsbObservable;

    private String chartName;
    private int id;
    private String idDeviceSelected;
    private ChartUtil chartUtil;

    private double arduinoDistance = 0D;

    private String formattedDate;

    private ArrayList<String> stringArrayList;

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

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(Calendar.getInstance().getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_device_detail_chart, container, false);

        Button button = (Button) root.findViewById(R.id.button_save_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartUtil.saveImageChart(chartName, formattedDate);
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
    public void update(Observable observable, Object arg) {

        if (observable instanceof UsbMeasurementObservable) {
            if (arg instanceof Double) {
                arduinoDistance = (Double) arg;
            }
        }

        if (observable instanceof DeviceObservable) {
            if (arg instanceof List) {

                List<Device> devices = (List<Device>) arg;

                for (Device deviceSelected : devices) {
                    if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                            deviceSelected.getAddress().equals(idDeviceSelected)) {

                        if (chartUtil != null) {

                            chartUtil.createDataSet(stringArrayList);

                            ArrayList<Double> dataSetForUpdates = new ArrayList<>();

                            for (String s : stringArrayList) {
                                if (s.equals(getString(R.string.chart_arduino))) {
                                    dataSetForUpdates.add(arduinoDistance);
                                }

                                if (s.equals(getString(R.string.chart_raw_distance))) {
                                    dataSetForUpdates.add(deviceSelected.getRawDistance());
                                }

                                if (s.equals(getString(R.string.chart_altbeacon))) {
                                    dataSetForUpdates.add(deviceSelected.getAltBeaconDistance());

                                }

                                if (s.equals(getString(R.string.chart_kalman_filter))) {
                                    dataSetForUpdates.add(deviceSelected.getKalmanFilterDistance());
                                }
                            }

                            chartUtil.updateDataSet(dataSetForUpdates);
                        }
                    }
                }
            }
        }
    }


}
