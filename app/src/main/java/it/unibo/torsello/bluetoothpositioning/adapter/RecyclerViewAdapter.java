package it.unibo.torsello.bluetoothpositioning.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by federico on 21/08/16.
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.DeviceViewHolder> {

    private DecimalFormat df;
    private final TypedValue mTypedValue;
    private List<Device> devices;
    private int mBackground;

    public RecyclerViewAdapter(FragmentActivity activity, List<Device> deviceList) {
        devices = deviceList;
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
        mTypedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    protected static class DeviceViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private final TextView macTextView;
        private final TextView majorTextView;
        private final TextView minorTextView;
        private final TextView measuredPowerTextView;
        private final TextView rssiTextView;
        private final TextView simpleNameTextView;
        private final TextView defaultNameTextView;
        private final TextView distanceTextView;
        private final TextView row_uuid;
        private final ImageView imageView;

        private DeviceViewHolder(View view) {
            super(view);
            mView = view;
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

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item2, parent, false);
        view.setBackgroundResource(mBackground);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, final int position) {

        try {
            Device device = devices.get(position);
            Beacon beacon = devices.get(position).getBeacon();

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, CheeseDetailActivity.class);
//                    intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
//                    context.startActivity(intent);
                    Snackbar.make(v, String.valueOf(holder.getAdapterPosition()), Snackbar.LENGTH_SHORT).show();
                }
            });

            holder.simpleNameTextView.setText(String.format("Friendly name: %s", device.getSimpleName()));
            holder.defaultNameTextView.setText(String.format("Default name: %s", beacon.getBluetoothName()));
            holder.macTextView.setText(String.format("MAC address: %s", beacon.getBluetoothAddress()));
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
                            "ID: %s", beacon.getId1(), beacon.getId2()));
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
                holder.row_uuid.setText(String.format("UUID: %s", beacon.getId1()));
            } else if (beacon.getBeaconTypeCode() == 0x0101) {
                // EstimoteNearable
            }

        } catch (NullPointerException e) {
            e.getStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
