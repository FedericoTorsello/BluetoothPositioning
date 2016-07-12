package it.unibo.torsello.bluetoothpositioning.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;


/**
 * Created by federico on 12/07/16.
 */
public class MyMapAdapter extends BaseAdapter {
    private ArrayMap<BluetoothDevice, Integer> mData = new ArrayMap<BluetoothDevice, Integer>();
    private Context context;

    public MyMapAdapter(Context context, ArrayMap<BluetoothDevice, Integer> data) {
        this.context = context;
        this.mData = data;
    }

    public void setData(ArrayMap<BluetoothDevice, Integer> map) {
        mData = map;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // reuse views
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
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

        BluetoothDevice device = mData.keyAt(position);
        Integer value = mData.valueAt(position);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.row_name.setText(String.format("%s %s", context.getString(R.string.name), device.getName()));
////        viewHolder.row_name.setTextColor(myIBeacon.getColor());
////        viewHolder.row_accuracy.setText(String.format("%s %.2f m", getContext().getString(R.string.accuracy), myIBeacon.getAccuracy()));
        viewHolder.row_rssi.setText(String.format("%s %s", context.getString(R.string.rssi), value));
        viewHolder.row_addr.setText(String.format("%s %s", context.getString(R.string.address), device.getAddress()));
//        viewHolder.row_major.setText(String.format("%s %s", context.getString(R.string.major), device.getMajor()));
//        viewHolder.row_minor.setText(String.format("%s %s", context.getString(R.string.minor), device.getMinor()));
//        viewHolder.row_uuid.setText(String.format("%s %s", context.getString(R.string.uuid), device.getUUID()));
////        viewHolder.row_color.setBackgroundColor(myIBeacon.getColor());
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
}