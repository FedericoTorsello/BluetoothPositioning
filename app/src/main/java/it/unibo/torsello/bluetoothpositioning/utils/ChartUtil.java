package it.unibo.torsello.bluetoothpositioning.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ChartUtil {

    private LineChart mChart;
    private Thread thread;
    private FragmentActivity fragmentActivity;

    public ChartUtil(FragmentActivity fragmentActivity, LineChart chart) {
        this.fragmentActivity = fragmentActivity;
        this.mChart = chart;
        initializeChart();
    }

    private void initializeChart() {

//        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        Typeface mTfLight = Typeface.createFromAsset(fragmentActivity.getAssets(), "OpenSans-Light.ttf");
        Typeface mTfBold = Typeface.createFromAsset(fragmentActivity.getAssets(), "OpenSans-Bold.ttf");

        // get the legend (only possible after setting data)

        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(7f);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setGridColor(Color.LTGRAY);
        xl.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfBold);

    }

    public void updateDataSet(final Device device) {
        if (thread != null)
            thread.interrupt();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (fragmentActivity.getApplication() != null) {

                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            LineData data = mChart.getData();

                            if (data == null) {
                                // create a dataset and give it a type
                                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                dataSets.add(createDataSet("KF_1", ColorTemplate.getHoloBlue()));
//                            dataSets.add(createDataSet("KF_2", Color.GREEN));
//                            dataSets.add(createDataSet("KF_3", Color.RED));
//                            dataSets.add(createDataSet("RAW_1", Color.YELLOW)); // add the datasets
//                            dataSets.add(createDataSet("RAW_2", Color.BLACK));
//                            dataSets.add(createDataSet("RAW_3", Color.BLUE));
//                            dataSets.add(createDataSet("RAW_4", Color.CYAN));

//                            dataSets.add(createDataSet("ARDUINO", Color.RED));

                                initializeDataChart(dataSets);
                            } else {
                                if (data.getDataSetCount() >= 0) {
                                    plotValue(data, 0, (float) device.getDistKalmanFilter1());
//                                plotValue(data, 1, (float) device.getDistKalmanFilter2());
//                                plotValue(data, 2, (float) device.getDistKalmanFilter4());
//                                plotValue(data, 3, (float) device.getBeacon().getDistance());
//                                plotValue(data, 4, (float) device.getDistNoFilter1());
//                                plotValue(data, 5, (float) device.getDistNoFilter2());
//                                plotValue(data, 6, (float) device.getDistNoFilter3());

//                                plotValue(data, 0, (float) /*arduino*/);
                                }
                            }
                        }
                    });

                }
            }
        });

        thread.start();
    }

    private LineDataSet createDataSet(String nameDataSet, int color) {
        LineDataSet set = new LineDataSet(null, nameDataSet);
        set.setColor(color);
        return set;
    }

    private void plotValue(LineData data, int index, float value) {

        ILineDataSet set = data.getDataSetByIndex(index);

        set.addEntry(new Entry(set.getEntryCount(), value));

        data.notifyDataChanged();

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(10);

        // move to the latest entry
        mChart.moveViewToX(data.getEntryCount());
    }

    private void initializeDataChart(ArrayList<ILineDataSet> dataSets) {

        // create a data object with the datasets
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextColor(Color.RED);
        lineData.setValueTextSize(9f);
        lineData.setValueFormatter(new DefaultValueFormatter(2));

        // set data
        mChart.setData(lineData);
    }

//    @Override
//    public void onValueSelected(Entry e, Highlight h) {
//        Log.i("Entry selected", e.toString());
//    }

//    @Override
//    public void onNothingSelected() {
//        Log.i("Nothing selected", "Nothing selected.");
//    }
}
