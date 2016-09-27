package it.unibo.torsello.bluetoothpositioning.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ChartUtil implements OnChartValueSelectedListener {

    private LineChart mChart;
    private Thread thread;
    private FragmentActivity fragmentActivity;

    private ArrayList<ILineDataSet> dataSets;

    public ChartUtil(FragmentActivity fragmentActivity, LineChart chart) {
        this.fragmentActivity = fragmentActivity;
        this.mChart = chart;
        initializeChart();
    }

    private void initializeChart() {
        dataSets = new ArrayList<ILineDataSet>();

        mChart.setOnChartValueSelectedListener(this);

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

    public void updateDataSet(final Double arg0, final Double arg1, final Double arg2, final Double arg3) {
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

                                if (dataSets != null) {
                                    initializeDataChart(dataSets);
                                } else {
                                    throw new Error("Error: dataSet is null!!!");
                                }
                            } else {
                                if (data.getDataSetCount() > 0) {
                                    if (arg0 != null) {
                                        plotValue(data, 0, arg0.floatValue());
                                    }
                                    if (arg1 != null) {
                                        plotValue(data, 1, arg1.floatValue());
                                    }
                                    if (arg2 != null) {
                                        plotValue(data, 2, arg2.floatValue());
                                    }
                                    if (arg3 != null) {
                                        plotValue(data, 3, arg3.floatValue());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        thread.start();
    }

    public ArrayList<ILineDataSet> createDataSet(String arg0, String arg1, String arg2, String arg3) {
        // create a dataset and give it a type
        if (arg0 != null) {
            dataSets.add(createDataSet(arg0, getRandomColor())); // add the datasets
        }

        if (arg1 != null) {
            dataSets.add(createDataSet(arg1, getRandomColor()));
        }

        if (arg2 != null) {
            dataSets.add(createDataSet(arg2, getRandomColor()));
        }

        if (arg3 != null) {
            dataSets.add(createDataSet(arg3, getRandomColor()));
        }

        return dataSets;
    }

    private int getRandomColor() {
        Random rnd = new Random();
        int color = 0;
        while (color == 0) {
            color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
        }
        return color;
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

    public void saveImageChart() {
        mChart.saveToGallery("prova", 100);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

}
