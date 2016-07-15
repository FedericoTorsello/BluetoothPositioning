package it.unibo.torsello.bluetoothpositioning.adapter;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.logic.IBeacon;


/**
 * Created by federico on 12/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyMapAdapter extends BaseAdapter {
    private ArrayMap<String, BluetoothDevice> mData = new ArrayMap<>();
    private Context context;

    public MyMapAdapter(Context context, ArrayMap<String, BluetoothDevice> data) {
        this.context = context;
        this.mData = data;
    }

    public void setData(ArrayMap<String, BluetoothDevice> map) {
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

        String deviceAddress = mData.keyAt(position);
        BluetoothDevice device = mData.valueAt(position);
//        IBeacon device1 = mData.valueAt(position);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

////        viewHolder.row_color.setBackgroundColor(myIBeacon.getColor());
////        viewHolder.row_name.setTextColor(myIBeacon.getColor());
        viewHolder.row_name.setText(String.format("%s %s", context.getString(R.string.name), device.getName()));
//        viewHolder.row_accuracy.setText(String.format(Locale.getDefault(), "%s %.2f m", context.getString(R.string.accuracy), device1.getDistanceInMetres()));
//        viewHolder.row_rssi.setText(String.format("%s %s", context.getString(R.string.rssi), device1.getLastRssi()));
//        viewHolder.row_addr.setText(String.format("%s %s", context.getString(R.string.address), device.getAddress()));
//        viewHolder.row_major.setText(String.format("%s %s", context.getString(R.string.major), device1.getMajor()));
//        viewHolder.row_minor.setText(String.format("%s %s", context.getString(R.string.minor), device1.getMinor()));
//        viewHolder.row_uuid.setText(String.format("%s %s", context.getString(R.string.uuid), device1.getUuid()));


        return convertView;
    }


    static class ViewHolder {
        ImageView row_color;
        TextView row_name;
        TextView row_accuracy;
        TextView row_rssi;
        TextView row_addr;
        TextView row_major;
        TextView row_minor;
        TextView row_uuid;
    }
}