package it.unibo.torsello.bluetoothpositioning.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.models.IBeacon;

/**
 * Created by federico on 21/07/16.
 * Displays basic information about beacon.
 */
public class LeDeviceListAdapter extends ArrayAdapter<Beacon> {

    private LayoutInflater inflater;

    public LeDeviceListAdapter(Context context, int textViewResourceId, List<Beacon> objects) {
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

        Beacon b = getItem(position);
        holder.macTextView.setText(String.format("MAC: %s", b.getBluetoothAddress()));
//        holder.nameTextView.setText(String.format(Locale.getDefault(), "DIST: %sm", df.format(b.getDist())));

//        holder.nameTextView.setText(String.format(Locale.getDefault(),
//                "DIST_1: %.2fm \n" +
//                        "DIST_2: %.2fm \n" +
//                        "DIST_KF1: %.2fm \n" +
//                        "DIST_NOF: %.2fm \n" +
//                        "DIST_A: %.2fm \n" +
//                        "DIST_B: %.2fm \n" +
//                        "DIST_C: %.2fm \n",
//                b.getDist(),
//                b.getDist2(),
//                b.getDistanceInMetresKalmanFilter(),
//                b.getDistanceInMetresNoFiltered(),
//                b.getDistance(),
//                b.getRawDistance(),
//                b.getDistanceWOSC()));

//        holder.majorTextView.setText(String.format("Major: %s", b.getMajor()));
//        holder.minorTextView.setText(String.format("Minor: %s", b.getMinor()));
//        holder.measuredPowerTextView.setText(String.format("MPower: %sdB", b.getRssi()));
//        holder.rssiTextView.setText(String.format("RSSI: %sdB", b.getRssi()));
//        holder.row_uuid.setText(String.format("UUID: %s", b.getUuid()));
//        if (b.getAddress().equals("D9:80:00:B7:16:78")) {
//            holder.imageView.setImageResource(R.drawable.beacon_mint);
//        } else {
//            holder.imageView.setImageResource(R.drawable.beacon_unknown);
//        }

        holder.nameTextView.setText(String.format(Locale.getDefault(), "DIST: %sm", df.format(b.getDistance())));
        holder.majorTextView.setText(String.format("Major: %s", b.getId2()));
        holder.minorTextView.setText(String.format("Minor: %s", b.getId3()));
        holder.measuredPowerTextView.setText(String.format("MPower: %sdB", b.getTxPower()));
        holder.rssiTextView.setText(String.format("RSSI: %sdB", b.getRssi()));
        holder.row_uuid.setText(String.format("UUID: %s", b.getId1()));
        if (b.getBluetoothAddress().equals("D9:80:00:B7:16:78")) {
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
