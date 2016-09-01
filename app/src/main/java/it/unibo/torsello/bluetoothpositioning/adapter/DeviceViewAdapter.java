package it.unibo.torsello.bluetoothpositioning.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFrag;
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by federico on 21/08/16.
 */
public class DeviceViewAdapter extends RecyclerView.Adapter<DeviceViewAdapter.DeviceViewHolder> {

    private DecimalFormat df;
    private List<Device> deviceList;
    private FragmentManager fragmentManager;
    private FragmentActivity fragmentActivity;

    public DeviceViewAdapter(FragmentActivity fragmentActivity, List<Device> deviceList) {
        this.deviceList = deviceList;
        this.fragmentActivity = fragmentActivity;
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
    }

    protected class DeviceViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private ImageView imageView;
        private TextView macTextView;
        private TextView majorTextView;
        private TextView minorTextView;
        private TextView measuredPowerTextView;
        private TextView rssiTextView;
        private TextView friendlyNameTextView;
        private TextView defaultNameTextView;
        private TextView distanceTextView;
        private TextView uuidTextView;
        private TextView nameSpaceTextView;
        private TextView proximityTextView;
        private TextView velocityTextView;
        private TextView colorTextView;
        private LinearLayout visibilityUUIDLinearLayout;
        private LinearLayout visibilityNameSpaceLinearLayout;

        private DeviceViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (ImageView) view.findViewById(R.id.imageBeacon);
            defaultNameTextView = (TextView) view.findViewById(R.id.value_default_name);
            friendlyNameTextView = (TextView) view.findViewById(R.id.value_friendly_name);
            distanceTextView = (TextView) view.findViewById(R.id.value_distance);
            macTextView = (TextView) view.findViewById(R.id.value_mac_address);
            majorTextView = (TextView) view.findViewById(R.id.value_major);
            minorTextView = (TextView) view.findViewById(R.id.value_minor);
            measuredPowerTextView = (TextView) view.findViewById(R.id.value_power);
            rssiTextView = (TextView) view.findViewById(R.id.value_rssi);
            uuidTextView = (TextView) view.findViewById(R.id.value_uuid);
            nameSpaceTextView = (TextView) view.findViewById(R.id.value_name_space);
            proximityTextView = (TextView) view.findViewById(R.id.value_proximity);
            velocityTextView = (TextView) view.findViewById(R.id.value_velocity);
            colorTextView = (TextView) view.findViewById(R.id.value_color);
            visibilityUUIDLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_uuid);
            visibilityNameSpaceLinearLayout = (LinearLayout) view.findViewById(R.id.visibilityNameSpace);
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        return new DeviceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, final int i) {

        final Beacon beacon = deviceList.get(holder.getAdapterPosition()).getBeacon();
        final Device device = deviceList.get(holder.getAdapterPosition());

        holder.velocityTextView.setText(String.valueOf(device.getVelocity()));

        Integer imageBeacon = device.getImageBeacon();
        if (imageBeacon != null) {
            holder.imageView.setImageResource(imageBeacon);
        } else {
            holder.imageView.setImageResource(R.drawable.unknown_beacon);
        }

        String txPower = String.valueOf(beacon.getTxPower());
        holder.measuredPowerTextView.setText(txPower);

        String rssi = String.valueOf(beacon.getRssi());
        holder.rssiTextView.setText(rssi);

        String friendlyName = device.getFriendlyName();
        if (friendlyName != null) {
            holder.friendlyNameTextView.setText(friendlyName);
        } else {
            holder.friendlyNameTextView.setText(android.R.string.unknownName);
        }

        String bluetoothName = beacon.getBluetoothName();
        if (bluetoothName != null) {
            holder.defaultNameTextView.setText(bluetoothName);
        } else {
            holder.defaultNameTextView.setText(android.R.string.unknownName);
        }

        String bluetoothAddress = beacon.getBluetoothAddress();
        if (bluetoothAddress != null) {
            holder.macTextView.setText(bluetoothAddress);
        } else {
            holder.macTextView.setText(android.R.string.unknownName);
        }

        String proximity = device.getProximity();
        if (proximity != null) {
            holder.proximityTextView.setText(proximity);
        } else {
            holder.proximityTextView.setText(android.R.string.unknownName);
        }

        String color = device.getColor();
        if (color != null) {
            holder.colorTextView.setText(color);
        } else {
            holder.colorTextView.setText(android.R.string.unknownName);
        }

        holder.distanceTextView.setText(String.format(Locale.getDefault(),
                "DIST_KF1:\t%sm \n" +
                        "DIST_KF2:\t%sm \n" +
                        "DIST_KF3_1:\t%sm \n" +
                        "DIST_KF3_2:\t%sm \n" +
                        "DIST_KF4:\t%sm \n" +
                        "DIST_A:\t%sm \n" +
                        "DIST_B:\t%sm \n" +
                        "DIST_C:\t%sm \n" +
                        "DIST_D:\t%sm",
                df.format(device.getDistKalmanFilter1()),
                df.format(device.getDistKalmanFilter2()),
                df.format(device.getDistKalmanFilter3_1()),
                df.format(device.getDistKalmanFilter3_2()),
                df.format(device.getDistKalmanFilter4()),

                df.format(beacon.getDistance()),
                df.format(device.getDistNoFilter1()),
                df.format(device.getDistNoFilter2()),
                df.format(device.getDistNoFilter3())));

//        holder.distanceTextView.setText(String.valueOf("DIST_KF1: \n"+device.getDistKalmanFilter4() +
//                "\nDIST_KF1: \n" + device.getDistNoFilter1()));

        if (beacon.getServiceUuid() == 0xfeaa) {
            if (beacon.getBeaconTypeCode() == 0x00) {
                holder.visibilityUUIDLinearLayout.setVisibility(View.GONE);
                holder.visibilityNameSpaceLinearLayout.setVisibility(View.VISIBLE);

                // Eddystone-UID
                if (beacon.getId1() != null) {
                    holder.nameSpaceTextView.setText(beacon.getId1().toString());
                } else {
                    holder.nameSpaceTextView.setText(android.R.string.unknownName);
                }

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
            holder.visibilityUUIDLinearLayout.setVisibility(View.VISIBLE);
            holder.visibilityNameSpaceLinearLayout.setVisibility(View.GONE);
            // AppleIBeacon
            if (beacon.getId1() != null) {
                holder.uuidTextView.setText(beacon.getId1().toString());
            } else {
                holder.uuidTextView.setText(android.R.string.unknownName);
            }

            if (beacon.getId2() != null) {
                holder.majorTextView.setText(beacon.getId2().toString());
            } else {
                holder.majorTextView.setText(android.R.string.unknownName);
            }

            if (beacon.getId3() != null) {
                holder.minorTextView.setText(beacon.getId3().toString());
            } else {
                holder.minorTextView.setText(android.R.string.unknownName);
            }

        } else if (beacon.getBeaconTypeCode() == 0x0101) {
            // EstimoteNearable
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title;
                if (device.getFriendlyName() != null) {
                    title = device.getFriendlyName();
                } else {
                    title = device.getAddress();
                }

                Fragment deviceDetailFrag = DeviceDetailFrag.newInstance(title);
                if (!deviceDetailFrag.isInLayout())
                fragmentManager.beginTransaction()
                        .add(R.id.frameLayout, deviceDetailFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
