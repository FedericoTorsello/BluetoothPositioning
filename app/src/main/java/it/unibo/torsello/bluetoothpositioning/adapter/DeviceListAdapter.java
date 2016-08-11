package it.unibo.torsello.bluetoothpositioning.adapter;

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
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by federico on 21/07/16.
 * Displays basic information about beacon.
 */
public class DeviceListAdapter extends ArrayAdapter<Device> {

    private LayoutInflater inflater;
    private DecimalFormat df;

    public DeviceListAdapter(Context context, int textViewResourceId, List<Device> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
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

        try {
            Device device = getItem(position);
            Beacon beacon = getItem(position).getBeacon();

            holder.simpleNameTextView.setText(String.format("Simple name: %s", device.getSimpleName()));
            holder.defaultNameTextView.setText(String.format("Default name: %s", beacon.getBluetoothName()));

            holder.macTextView.setText(String.format("MAC: %s", beacon.getBluetoothAddress()));
            holder.distanceTextView.setText(String.format(Locale.getDefault(),
                    "DIST_KF1: %sm \n" +
                            "DIST_KF2: %sm \n" +
                            "DIST_KF3: %sm \n" +
                            "DIST_KF4: %sm \n" +
                            "DIST_A: %sm \n" +
                            "DIST_B: %sm \n" +
                            "DIST_C: %sm \n" +
                            "DIST_D: %sm",
                    df.format(device.getDistanceKalmanFilter1()),
                    df.format(device.getDistanceKalmanFilter2()),
                    df.format(device.getDistanceKalmanFilter3()),
                    df.format(device.getDistanceKalmanFilter4()),
                    df.format(device.getDist()),
                    df.format(beacon.getDistance()),
                    df.format(device.getRawDistance()),
                    df.format(device.getDistanceInternet())));

            holder.measuredPowerTextView.setText(String.format("MPower: %sdB", beacon.getTxPower()));
            holder.rssiTextView.setText(String.format("RSSI: %sdB", beacon.getRssi()));

            if (device.getImageBeacon() != null) {
                holder.imageView.setImageResource(device.getImageBeacon());
            } else {
                holder.imageView.setImageResource(R.drawable.beacon_unknown);
            }

            if (beacon.getServiceUuid() == 0xfeaa) {
                if (beacon.getBeaconTypeCode() == 0x00) {
                    // Eddystone-UID
                    holder.row_uuid.setText(String.format("NameSpace: %s\n" +
                            "Idemtif: %s", beacon.getId1(), beacon.getId2()));
                } else if (beacon.getBeaconTypeCode() == 0x10) {
                    // Eddystone-URL
                    // String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                } else if (beacon.getBeaconTypeCode() == 0x20) {
                    if (!beacon.getExtraDataFields().isEmpty()) {
                        // Eddystone-TLM
                    }
                }
            } else if (beacon.getServiceUuid() == 0xbeac) {
                // AltBeacon
            } else if (beacon.getBeaconTypeCode() == 0x0215) { //533 in dec)
                // AppleIBeacon
                holder.majorTextView.setText(String.format("Major: %s", beacon.getId2()));
                holder.minorTextView.setText(String.format("Minor: %s", beacon.getId3()));
                ;
                holder.row_uuid.setText(String.format("UUID: %s", beacon.getId1()));
            } else if (beacon.getBeaconTypeCode() == 0x0101) {
                // EstimoteNearable
            }

        } catch (NullPointerException e) {
            e.getStackTrace();
        }
        return view;
    }

    static class ViewHolder {
        final TextView macTextView;
        final TextView majorTextView;
        final TextView minorTextView;
        final TextView measuredPowerTextView;
        final TextView rssiTextView;
        final TextView simpleNameTextView;
        final TextView defaultNameTextView;
        final TextView distanceTextView;
        final TextView row_uuid;
        final ImageView imageView;

        ViewHolder(View view) {
            defaultNameTextView = (TextView) view.findViewById(R.id.row_default_name);
            distanceTextView = (TextView) view.findViewById(R.id.row_distance);
            macTextView = (TextView) view.findViewById(R.id.row_address);
            majorTextView = (TextView) view.findViewById(R.id.row_major);
            minorTextView = (TextView) view.findViewById(R.id.row_minor);
            measuredPowerTextView = (TextView) view.findViewById(R.id.row_power);
            rssiTextView = (TextView) view.findViewById(R.id.row_rssi);
            simpleNameTextView = (TextView) view.findViewById(R.id.simple_name);
            row_uuid = (TextView) view.findViewById(R.id.row_UUID);
            imageView = (ImageView) view.findViewById(R.id.imageBeacon);
        }
    }


}
