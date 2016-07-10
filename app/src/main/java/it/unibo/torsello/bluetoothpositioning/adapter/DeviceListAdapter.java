package it.unibo.torsello.bluetoothpositioning.adapter;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.unibo.torsello.bluetoothpositioning.R;


/**
 * Created by federico on 25/01/16.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> myList = null;

    public DeviceListAdapter(Context context, int textViewResourceId, List<BluetoothDevice> objects) {
        super(context, textViewResourceId, objects);
        this.myList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // reuse views
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_layout, parent, false);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.row_name = (TextView) convertView
                    .findViewById(R.id.row_name);
            viewHolder.row_accuracy = (TextView) convertView
                    .findViewById(R.id.row_accuracy);
            viewHolder.row_rssi = (TextView) convertView
                    .findViewById(R.id.row_rssi);
            viewHolder.row_addr = (TextView) convertView
                    .findViewById(R.id.row_address);
            viewHolder.row_major = (TextView) convertView
                    .findViewById(R.id.row_major);
            viewHolder.row_minor = (TextView) convertView
                    .findViewById(R.id.row_minor);
            viewHolder.row_uuid = (TextView) convertView
                    .findViewById(R.id.row_UUID);
            viewHolder.row_color = (ImageView) convertView
                    .findViewById(R.id.row_color);
            convertView.setTag(viewHolder);
        }

        final BluetoothDevice myIBeacon = getItem(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.row_name.setText(String.format("%s %s", getContext().getString(R.string.name), myIBeacon.getName()));
//        viewHolder.row_name.setTextColor(myIBeacon.getColor());
//        viewHolder.row_accuracy.setText(String.format("%s %.2f m", getContext().getString(R.string.accuracy), myIBeacon.getAccuracy()));
//        viewHolder.row_rssi.setText(String.format("%s %s", getContext().getString(R.string.rssi), myIBeacon.getRssi()));
        viewHolder.row_addr.setText(String.format("%s %s", getContext().getString(R.string.address), myIBeacon.getAddress()));
//        viewHolder.row_major.setText(String.format("%s %s", getContext().getString(R.string.major), myIBeacon.getMajor()));
//        viewHolder.row_minor.setText(String.format("%s %s", getContext().getString(R.string.minor), myIBeacon.getMinor()));
//        viewHolder.row_uuid.setText(String.format("%s %s", getContext().getString(R.string.uuid), myIBeacon.getUUID()));
//        viewHolder.row_color.setBackgroundColor(myIBeacon.getColor());
        return convertView;
    }


    static class ViewHolder {
        TextView row_name;
        TextView row_accuracy;
        TextView row_rssi;
        TextView row_addr;
        TextView row_major;
        TextView row_minor;
        TextView row_uuid;
        ImageView row_color;
    }

    @Override
    public void add(BluetoothDevice device) {
        if (!myList.contains(device)) {
            myList.add(device);
            notifyDataSetChanged();
        }
    }
}
