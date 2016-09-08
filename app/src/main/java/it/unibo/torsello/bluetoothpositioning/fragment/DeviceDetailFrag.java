package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.adapter.DeviceViewAdapter;
import it.unibo.torsello.bluetoothpositioning.models.Device;
import it.unibo.torsello.bluetoothpositioning.utils.CameraUtil;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailFrag extends Fragment implements ApplicationActivity.OnAddDevicesListener,
        OnChartValueSelectedListener {

    private final String TAG_CLASS = getClass().getSimpleName();
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private CameraUtil cameraUtil;
    private FrameLayout preview;
    private TextureView mTextureView;
    private DeviceViewAdapter deviceViewAdapter;
    private List<Device> deviceList;
    private Device device;

    private LineChart mChart;
    private Thread thread;

    String idDeviceSelected;

    public static DeviceDetailFrag newInstance(String message) {
        DeviceDetailFrag fragment = new DeviceDetailFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_device_details, container, false);

        getActivity().findViewById(R.id.sliding_tabs).setVisibility(View.GONE);

        ((CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar)).setTitle(idDeviceSelected);

        // add RecyclerView
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(deviceViewAdapter);
        ((FrameLayout) root.findViewById(R.id.frame_selected_device)).addView(recyclerView);

        initializeCamera(root);

        initializeChart(root);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idDeviceSelected = getArguments().getString(EXTRA_MESSAGE);
        deviceList = new ArrayList<>();
        deviceViewAdapter = new DeviceViewAdapter(getActivity(), deviceList);
    }

    @Override
    public void onPause() {
        super.onPause();

        mTextureView = null;
        preview.removeAllViews();
        preview = null;
        cameraUtil = null;
    }

    @Override
    public void updateInfoDevices(final List<Device> devices) {

        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }

        for (Device deviceSelected : devices) {
            if (deviceSelected.getFriendlyName().equals(idDeviceSelected) ||
                    deviceSelected.getAddress().equals(idDeviceSelected)) {
                device = deviceSelected;

                TextView a = (TextView) getActivity().findViewById(R.id.prova);
                a.setText(String.format(Locale.getDefault(), "%.2f", device.getDistNoFilter1()));


                if (thread != null)
                    thread.interrupt();

                thread = new Thread(runnable);

                thread.start();

                deviceList.add(deviceSelected);
            }
        }

        deviceViewAdapter.notifyDataSetChanged();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        LineData data = mChart.getData();

                        if (data == null) {
                            initializeDataChart();
                        } else {
                            if (data.getDataSetCount() >= 0) {
                                boh(data);
                            }
                        }
                    }
                });

            }
        }
    };

    private void boh(LineData data) {

        ILineDataSet set = data.getDataSetByIndex(0);
        ILineDataSet set2 = data.getDataSetByIndex(1);

        set.addEntry(new Entry(set.getEntryCount(), (float) device.getDistNoFilter1()));
        set2.addEntry(new Entry(set2.getEntryCount(), (float) device.getDistNoFilter2()));

        data.notifyDataChanged();

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(10);

        // move to the latest entry
        mChart.moveViewToX(data.getEntryCount());
    }

    @Override
    public void clearList() {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /**
     * Check if this device has a camera
     */
    private boolean isCameraHardwarePresent() {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void initializeCamera(View root) {

        cameraUtil = new CameraUtil(getActivity());

        if (isCameraHardwarePresent()) {

            preview = (FrameLayout) root.findViewById(R.id.camera_preview1);
            mTextureView = cameraUtil.getmTextureView();
            preview.addView(mTextureView);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the camera preview.
                    cameraUtil.safeCameraOpenInView(mTextureView.getSurfaceTexture());
                }
            });

            root.findViewById(R.id.fab_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraUtil.takePicture();
                }
            });

        } else {
            Toast.makeText(getActivity(), "No camera onCameraListener this device", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void initializeChart(View root) {

        mChart = new LineChart(getActivity());
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        Typeface mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        Typeface mTfBold = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Bold.ttf");

        // get the legend (only possible after setting data)
        Legend legend = mChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTypeface(mTfBold);
        legend.setFormSize(35);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setGridColor(Color.LTGRAY);
        xl.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfBold);

        // programatically add the chart
        ((FrameLayout) root.findViewById(R.id.frame1)).addView(mChart);
    }

    private void initializeDataChart() {

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(null, "DataSet 1");
        set1.setColor(ColorTemplate.getHoloBlue());

        LineDataSet set2 = new LineDataSet(null, "DataSet 2");
        set2.setColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);

        // create a data object with the datasets
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextColor(Color.RED);
        lineData.setValueTextSize(9f);
        lineData.setValueFormatter(new DefaultValueFormatter(2));

        // set data
        mChart.setData(lineData);
    }

}
