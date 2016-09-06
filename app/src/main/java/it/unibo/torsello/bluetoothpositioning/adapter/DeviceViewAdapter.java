package it.unibo.torsello.bluetoothpositioning.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFrag;
import it.unibo.torsello.bluetoothpositioning.models.Device;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceViewAdapter extends RecyclerView.Adapter<DeviceViewAdapter.DeviceViewHolder> {

    private DecimalFormat df;
    private List<Device> deviceList;
    private FragmentActivity fragmentActivity;
    private String deviceDetailName;
    private Runnable runnable;

    public DeviceViewAdapter(final FragmentActivity fragmentActivity, List<Device> deviceList) {

        this.deviceList = new ArrayList<>();
        this.deviceList = deviceList;
        this.fragmentActivity = fragmentActivity;
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        runnable = new Runnable() {
            @Override
            public void run() {
                final Fragment newFrag = DeviceDetailFrag.newInstance(deviceDetailName);
                FragmentManager manager = fragmentActivity.getSupportFragmentManager();
                Fragment currentFrag = manager.findFragmentById(R.id.frameLayout);

                //Check if the new Fragment is the same
                //If it is, don't add to the back stack
                if (currentFrag == null || !currentFrag.getClass().equals(newFrag.getClass())) {
                    manager.beginTransaction()
//                            .add(R.id.frameLayout, newFrag)
                            .replace(R.id.frameLayout, newFrag)
                            .addToBackStack(null)
                            .commit();
                }
            }
        };
    }

    protected class DeviceViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView imageView;
        TextView macTextView;
        TextView majorTextView;
        TextView minorTextView;
        TextView measuredPowerTextView;
        TextView rssiTextView;
        TextView friendlyNameTextView;
        TextView defaultNameTextView;
        TextView distanceTextView;
        TextView uuidTextView;
        TextView nameSpaceTextView;
        TextView instanceTextView;
        TextView proximityTextView;
        TextView velocityTextView;
        TextView colorTextView;
        LinearLayout visibilityUUIDLinearLayout;
        LinearLayout visibilityNameSpaceLinearLayout;
        LinearLayout visibilityMajorMinorLinearLayout;
        LinearLayout visibilityInstanceLinearLayout;

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
            instanceTextView = (TextView) view.findViewById(R.id.value_instance);
//            velocityTextView = (TextView) view.findViewById(R.id.value_velocity);
            colorTextView = (TextView) view.findViewById(R.id.value_color);
            visibilityUUIDLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_uuid);
            visibilityNameSpaceLinearLayout = (LinearLayout) view.findViewById(R.id.visibilityNameSpace);
            visibilityMajorMinorLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_major_minor);
            visibilityInstanceLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_instance);
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(root);
        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, final int i) {

        int adapterPosition = holder.getAdapterPosition();

        final Beacon beacon = deviceList.get(adapterPosition).getBeacon();
        final Device device = deviceList.get(adapterPosition);

//        holder.velocityTextView.setText(String.valueOf(device.getVelocity()));

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

        Integer color = device.getColor();
        if (color != null) {
            holder.colorTextView.setText(color);
        } else {
            holder.colorTextView.setText(android.R.string.unknownName);
        }

        holder.distanceTextView.setText(String.format(Locale.getDefault(),
                "DIST_KF1:\t%sm \n" +
                        "DIST_KF2:\t%sm \n" +
//                        "DIST_KF3_1:\t%sm \n" +
//                        "DIST_KF3_2:\t%sm \n" +
                        "DIST_KF4:\t%sm \n" +
                        "DIST_A:\t%sm \n" +
                        "DIST_B:\t%sm \n" +
                        "DIST_C:\t%sm \n" +
                        "DIST_D:\t%sm",
                df.format(device.getDistKalmanFilter1()),
                df.format(device.getDistKalmanFilter2()),
//                df.format(device.getDistKalmanFilter3_1()),
//                df.format(device.getDistKalmanFilter3_2()),
                df.format(device.getDistKalmanFilter4()),

                df.format(beacon.getDistance()),
                df.format(device.getDistNoFilter1()),
                df.format(device.getDistNoFilter2()),
                df.format(device.getDistNoFilter3())));

        if (beacon.getServiceUuid() == 0xfeaa) {

            holder.visibilityUUIDLinearLayout.setVisibility(View.GONE);
            holder.visibilityMajorMinorLinearLayout.setVisibility(View.INVISIBLE);
            holder.visibilityNameSpaceLinearLayout.setVisibility(View.VISIBLE);
            holder.visibilityInstanceLinearLayout.setVisibility(View.VISIBLE);

            if (beacon.getBeaconTypeCode() == 0x00) {
                // Eddystone-UID
                if (beacon.getId1() != null) {
                    holder.nameSpaceTextView.setText(beacon.getId1().toString());
                } else {
                    holder.nameSpaceTextView.setText(android.R.string.unknownName);
                }

                if (beacon.getId2() != null) {
                    holder.instanceTextView.setText(beacon.getId2().toString());
                } else {
                    holder.instanceTextView.setText(android.R.string.unknownName);
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
            holder.visibilityMajorMinorLinearLayout.setVisibility(View.VISIBLE);
            holder.visibilityNameSpaceLinearLayout.setVisibility(View.GONE);
            holder.visibilityInstanceLinearLayout.setVisibility(View.GONE);

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

                deviceDetailName = device.getFriendlyName();
                if (deviceDetailName == null) {
                    deviceDetailName = device.getAddress();
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fragmentActivity.runOnUiThread(runnable);
                    }
                });

                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
