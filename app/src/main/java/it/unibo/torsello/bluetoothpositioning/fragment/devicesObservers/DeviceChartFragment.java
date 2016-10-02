package it.unibo.torsello.bluetoothpositioning.fragment.devicesObservers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.MyDeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.MyUsbObservable;
import it.unibo.torsello.bluetoothpositioning.util.ChartUtil;

public class DeviceChartFragment extends Fragment implements Observer {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private MyDeviceObservable myDeviceObservable;
    private MyUsbObservable myUsbObservable;

    private String idDeviceSelectedName;
    private ChartUtil chartUtil;

    private LineChart lineChart;

    private Double arduinoValue = 0D;

    public static DeviceChartFragment newInstance(String param1) {
        DeviceChartFragment fragment = new DeviceChartFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idDeviceSelectedName = getArguments().getString(EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chart, container, false);

        myDeviceObservable = MyDeviceObservable.getInstance();
        myUsbObservable = MyUsbObservable.getInstance();

        lineChart = (LineChart) root.findViewById(R.id.chart);

        // add the charts
//        chartUtil = new ChartUtil(getActivity(), (LineChart) root.findViewById(R.id.chart));

//        FrameLayout frameLayout = (FrameLayout) root.findViewById(R.id.frame_chart);
//        frameLayout.addView(new LineChart(getActivity()));

//        chartUtil = new ChartUtil(getActivity());

//        chartUtil.setmChart();

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
                    if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                            deviceSelected.getAddress().equals(idDeviceSelectedName)) {
                        if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                                deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                            if (chartUtil != null) {
                                chartUtil.createDataSet(getString(R.string.chart_arduino),
                                        getString(R.string.chart_raw_distance),
                                        getString(R.string.chart_altbeacon),
                                        getString(R.string.chart_kalman_filter));
                                chartUtil.updateDataSet(arduinoValue,
                                        deviceSelected.getRawDistance(),
                                        deviceSelected.getAltBeaconDistance(),
                                        deviceSelected.getKalmanFilterDistance());
                            }
                        }
                    }
                }
            }
        }
    }

}
