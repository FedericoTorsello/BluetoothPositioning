package it.unibo.torsello.bluetoothpositioning.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.models.IBeacon;


/**
 * Created by federico on 12/07/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyArrayAdapter extends ArrayAdapter<IBeacon> {

    private List<IBeacon> myList = null;
    private Context context;

    public MyArrayAdapter(Context context, int textViewResourceId, List<IBeacon> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.myList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // reuse views
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.device_item2, parent, false);
            // configure view holder
            viewHolder = new ViewHolder();
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
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//            String deviceAddress = String.valueOf(getItem(position));
        IBeacon device = getItem(position);

//            IBeacon existingBeacon = bluetoothDeviceMap.get(scannedBeacon.address);
//                            scannedBeacon.calculateDistanceKalmanFilter();


        ////        viewHolder.row_color.setBackgroundColor(myIBeacon.getColor());
////        viewHolder.row_name.setTextColor(myIBeacon.getColor());
        viewHolder.row_name.setText(String.format("%s %s", context.getString(R.string.name), device.getName()));
        viewHolder.row_addr.setText(String.format("%s %s", context.getString(R.string.address), device.getAddress()));
        viewHolder.row_major.setText(String.format("%s %s", context.getString(R.string.major), device.getMajor()));
        viewHolder.row_minor.setText(String.format("%s %s", context.getString(R.string.minor), device.getMinor()));
        viewHolder.row_uuid.setText(String.format("%s %s", context.getString(R.string.uuid), device.getUuid()));

//            viewHolder.row_accuracy.setText(String.format(Locale.getDefault(), "%s %.2f m"
// , context.getString(R.string.accuracy), device.getDistanceInMetresKalmanFilter()));

        viewHolder.row_rssi.setText(String.format("%s %s", context.getString(R.string.rssi), device.getRssi()));
//            viewHolder.row_accuracy.setText(String.format(Locale.getDefault(),
//                    "%s KF%.2f m NOF%.2f m XXX%.2f m", context.getString(R.string.accuracy),
//                    device.getDistanceInMetresKalmanFilter(), device.getDistanceInMetresNoFiltered(), device.xxx));

//        viewHolder.row_accuracy.setText(String.format(Locale.getDefault(),
//                "%s NOF%.2f m  KF%.2f m ", context.getString(R.string.accuracy),
//                device.getDistanceInMetresNoFiltered(), device.getDist()));


        return convertView;

    }


    private class ViewHolder {
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