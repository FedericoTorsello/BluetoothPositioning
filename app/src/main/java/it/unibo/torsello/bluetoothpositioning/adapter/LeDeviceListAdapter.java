package it.unibo.torsello.bluetoothpositioning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;

/**
 * Created by federico on 21/07/16.
 * Displays basic information about beacon.
 */
public class LeDeviceListAdapter extends ArrayAdapter<IBeacon> {

    private LayoutInflater inflater;

    public LeDeviceListAdapter(Context context, int textViewResourceId, List<IBeacon> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.device_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        IBeacon beacon = getItem(position);
        holder.macTextView.setText(String.format("MAC: %s", beacon.address));
        holder.nameTextView.setText(String.format(Locale.getDefault(), "DIST: %sm", df.format(beacon.getDist())));
        holder.majorTextView.setText(String.format("Major: %s", beacon.major));
        holder.minorTextView.setText(String.format("Minor: %s", beacon.minor));
        holder.measuredPowerTextView.setText(String.format("MPower: %sdB", beacon.txPower));
        holder.rssiTextView.setText(String.format("RSSI: %sdB", beacon.getLastRssi()));
        holder.row_uuid.setText(String.format("UUID: %s", beacon.uuid));
        if (beacon.address.equals("D9:80:00:B7:16:78")) {
            holder.imageView.setImageResource(R.drawable.beacon_mint);
        } else {
            holder.imageView.setImageResource(R.drawable.beacon_unknown);
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
        final TextView row_uuid;
        final ImageView imageView;

        ViewHolder(View view) {
            macTextView = (TextView) view.findViewById(R.id.row_address);
            majorTextView = (TextView) view.findViewById(R.id.row_major);
            minorTextView = (TextView) view.findViewById(R.id.row_minor);
            measuredPowerTextView = (TextView) view.findViewById(R.id.row_power);
            rssiTextView = (TextView) view.findViewById(R.id.row_rssi);
            nameTextView = (TextView) view.findViewById(R.id.row_name);
            row_uuid = (TextView) view.findViewById(R.id.row_UUID);
            imageView = (ImageView) view.findViewById(R.id.imageBeacon);
        }
    }
}
