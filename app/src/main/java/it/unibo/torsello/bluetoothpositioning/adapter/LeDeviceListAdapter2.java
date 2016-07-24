package it.unibo.torsello.bluetoothpositioning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;


/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class LeDeviceListAdapter2 extends ArrayAdapter<IBeacon> {

    private List<IBeacon> myList = null;
    private Context context;
    private LayoutInflater inflater;

    public LeDeviceListAdapter2(Context context, int textViewResourceId, List<IBeacon> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
        this.myList = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(IBeacon beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.nameTextView.setText(String.format("MAC: %s", beacon.address));
//    holder.macTextView.setText(String.format(Locale.getDefault(),"Acc.: Est(%.2fm)-Rad(%.2fm)-KF(%.2fm)", Utils.computeAccuracy(beacon),Utils.computeAccuracyRN(beacon),Utils.computeAccuracyKF(beacon)));

        holder.macTextView.setText(String.format(Locale.getDefault(), "DIST: %.2fm", beacon.getDist()));

        holder.majorTextView.setText("Major: " + beacon.major);
        holder.minorTextView.setText("Minor: " + beacon.minor);
        holder.measuredPowerTextView.setText("MPower: " + beacon.txPower);
        holder.rssiTextView.setText("RSSI: " + beacon.getLastRssi());
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.device_item, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView macTextView;
        final TextView majorTextView;
        final TextView minorTextView;
        final TextView measuredPowerTextView;
        final TextView rssiTextView;
        final TextView nameTextView;

        ViewHolder(View view) {
            macTextView = (TextView) view.findViewWithTag("mac");
            majorTextView = (TextView) view.findViewWithTag("major");
            minorTextView = (TextView) view.findViewWithTag("minor");
            measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
            nameTextView = (TextView) view.findViewWithTag("name");
        }
    }
}
