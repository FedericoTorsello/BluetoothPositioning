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
            Beacon b = getItem(position).getBeacon();

            holder.macTextView.setText(String.format("MAC: %s", b.getBluetoothAddress()));
            holder.nameTextView.setText(String.format(Locale.getDefault(),
                    "DIST_1: %sm \n" +
                            "DIST_KF1: %sm \n" +
                            "DIST_NOF: %sm \n" +
                            "DIST_A: %sm \n" +
                            "DIST_B: %sm \n" +
                            "DIST_C: %sm \n",
                    df.format(device.getDist()),
                    df.format(device.getDistanceInMetresKalmanFilter()),
                    df.format(device.getDistanceInMetresNoFiltered()),
                    df.format(device.getDistance()),
                    df.format(device.getRawDistance()),
                    df.format(device.getDistanceWOSC())));

            holder.measuredPowerTextView.setText(String.format("MPower: %sdB", b.getTxPower()));
            holder.rssiTextView.setText(String.format("RSSI: %sdB", b.getRssi()));

            if (b.getBluetoothAddress().equals("D9:80:00:B7:16:78")) {
                holder.imageView.setImageResource(R.drawable.beacon_mint);
            } else {
                holder.imageView.setImageResource(R.drawable.beacon_unknown);
            }

            if (b.getServiceUuid() == 0xfeaa) {
                if (b.getBeaconTypeCode() == 0x00) {
                    // Eddystone-UID
                    holder.row_uuid.setText(String.format("NameSpace: %s\n" +
                            "Idemtif: %s", b.getId1(), b.getId2()));
                } else if (b.getBeaconTypeCode() == 0x10) {
                    // Eddystone-URL
                    // String url = UrlBeaconUrlCompressor.uncompress(b.getId1().toByteArray());
                } else if (b.getBeaconTypeCode() == 0x20) {
                    if (!b.getExtraDataFields().isEmpty()) {
                        // Eddystone-TLM
                    }
                }
            } else if (b.getServiceUuid() == 0xbeac) {
                // AltBeacon
            } else if (b.getBeaconTypeCode() == 0x0215) { //533 in dec)
                // AppleIBeacon
                holder.majorTextView.setText(String.format("Major: %s", b.getId2()));
                holder.minorTextView.setText(String.format("Minor: %s", b.getId3()));
                ;
                holder.row_uuid.setText(String.format("UUID: %s", b.getId1()));
            } else if (b.getBeaconTypeCode() == 0x0101) {
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
