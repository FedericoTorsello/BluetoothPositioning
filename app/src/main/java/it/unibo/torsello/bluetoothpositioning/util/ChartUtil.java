package it.unibo.torsello.bluetoothpositioning.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class ChartUtil implements OnChartValueSelectedListener {

    private LineChart chart;
    private Thread thread;
    private FragmentActivity activity;

    private ArrayList<ILineDataSet> dataSets;

    public ChartUtil(FragmentActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setChart(LineChart chart) {
        this.chart = chart;
    }

    public void initializeChart() {
        dataSets = new ArrayList<ILineDataSet>();

        chart.setOnChartValueSelectedListener(this);

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        chart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY);

        Typeface mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        Typeface mTfBold = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Bold.ttf");

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(7f);

        XAxis xl = chart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setGridColor(Color.LTGRAY);
        xl.setTextColor(Color.WHITE);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTfBold);

    }

    public void updateDataSet(final ArrayList<Double> doubleArrayList) {
        if (thread != null)
            thread.interrupt();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            LineData data = chart.getData();

                            if (data == null) {
                                if (dataSets != null) {
                                    initializeDataChart(dataSets);
                                } else {
                                    throw new Error("Error: dataSet is null!!!");
                                }
                            } else {
                                if (data.getDataSetCount() > 0) {

                                    for (int i = 0; i < doubleArrayList.size(); i++) {
                                        plotValue(data, i, doubleArrayList.get(i));
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

    public void createDataSet(ArrayList<String> args) {
        // create a dataset and give it a type

        for (String s : args) {
            if (s != null) {
                if (s.equals(getActivity().getString(R.string.chart_arduino))) {
                    dataSets.add(createDataSet(s, Color.RED));
                } else {
                    dataSets.add(createDataSet(s, getRandomColor()));
                }
            }
        }

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

    private void plotValue(LineData data, int index, Double value) {

        ILineDataSet set = data.getDataSetByIndex(index);

        set.addEntry(new Entry(set.getEntryCount(), value.floatValue()));

        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(10);

        // move to the latest entry
        chart.moveViewToX(data.getEntryCount());
    }

    private void initializeDataChart(ArrayList<ILineDataSet> dataSets) {

        // create a data object with the datasets
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextColor(Color.RED);
        lineData.setValueTextSize(9f);
        lineData.setValueFormatter(new DefaultValueFormatter(2));

        // set data
        chart.setData(lineData);
    }

    public void saveImageChart(String chartName, String formattedDate) {
        String nameImage = chartName + " " + System.currentTimeMillis();
        chart.saveToGallery(nameImage, chartName
                + " - " + formattedDate, null, Bitmap.CompressFormat.JPEG, 100);

        Snackbar.make(getActivity().findViewById(R.id.fab), chartName + " stored"
                , Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Snackbar.make(getActivity().findViewById(R.id.fab), e.copy() + " selected"
                , Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
//        Log.i("Nothing selected", "Nothing selected.");
    }

}
