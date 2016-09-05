package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.utils.ArduinoCommunicatorService;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class MeasurementFrag extends Fragment implements OnChartValueSelectedListener {

    private BarChart mChart;
    private RectF mOnValueSelectedRectF;
    private Typeface tf;

    //    private LineChart mChart;
    private OnArduinoListener onArduinoListener;

    public interface OnArduinoListener {
        BroadcastReceiver getBroadcastReceiver();
    }

    public static MeasurementFrag newInstance() {
        return new MeasurementFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_measurement, container, false);
        mChart = (BarChart) root.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        setData(12, 50);

//        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv);

//        mChart.setData(generateBarData(1, 20000, 12));

        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnValueSelectedRectF = new RectF();
        onArduinoListener = new ArduinoUSB(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        createService();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    private void createService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoCommunicatorService.DATA_RECEIVED_INTENT);
        filter.addAction(ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT);
        getActivity().registerReceiver(onArduinoListener.getBroadcastReceiver(), filter);
    }

    public void unregisterReceiver() {
        getActivity().unregisterReceiver(onArduinoListener.getBroadcastReceiver());
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
//        if (e == null)
//            return;
//
//        RectF bounds = mOnValueSelectedRectF;
//        mChart.getBarBounds((BarEntry) e, bounds);
//        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);
//
//        Log.i("bounds", bounds.toString());
//        Log.i("position", position.toString());
//
//        Log.i("x-index",
//                "low: " + mChart.getLowestVisibleX() + ", high: "
//                        + mChart.getHighestVisibleX());
//
//        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }

//    private void setData(int count, float range) {
//
//        float start = 0f;
//
//        mChart.getXAxis().setAxisMinValue(start);
//        mChart.getXAxis().setAxisMaxValue(start + count + 2);
//
//        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//
//        for (int i = (int) start; i < start + count + 1; i++) {
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);
//            yVals1.add(new BarEntry(i + 1f, val));
//        }
//
//        BarDataSet set1;
//
//        if (mChart.getData() != null &&
//                mChart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
//            set1.setValues(yVals1);
//            mChart.getData().notifyDataChanged();
//            mChart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(yVals1, "The year 2017");
//            set1.setColors(ColorTemplate.MATERIAL_COLORS);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
////            data.setValueTypeface(mTfLight);
//            data.setBarWidth(0.9f);
//
//            mChart.setData(data);
//        }
//    }


}
