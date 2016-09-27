package it.unibo.torsello.bluetoothpositioning.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DeviceDetailFragmentProva;
import it.unibo.torsello.bluetoothpositioning.model.Device;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceCardViewAdapter extends RecyclerView.Adapter<DeviceCardViewAdapter.DeviceViewHolder> {

    private DecimalFormat df;
    private List<Device> deviceList;
    private FragmentActivity fragmentActivity;

    public DeviceCardViewAdapter(final FragmentActivity fragmentActivity, List<Device> deviceList) {

        this.deviceList = new ArrayList<>();
        this.deviceList = deviceList;
        this.fragmentActivity = fragmentActivity;

        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        return new DeviceViewHolder(root);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, final int position) {

        final Beacon beacon = deviceList.get(position).getBeacon();
        final Device device = deviceList.get(position);

        setInfoDevice(holder, beacon);

        setDistance(holder, device);

        final Integer imageBeacon = device.getImageBeacon();
        if (imageBeacon != null) {
            holder.imageView.setImageResource(imageBeacon);
        } else {
            holder.imageView.setImageResource(R.drawable.beacon_unknown);
        }

        holder.rssiTextView.setText(String.format("%sdb", beacon.getTxPower()));

        holder.txPowerTextView.setText(String.format("%sdb", beacon.getRssi()));

        final String friendlyName = device.getFriendlyName();
        if (friendlyName != null) {
            holder.friendlyNameTextView.setText(friendlyName);
        } else {
            holder.friendlyNameTextView.setText(android.R.string.unknownName);
        }

        final String bluetoothName = beacon.getBluetoothName();
        if (bluetoothName != null) {
            holder.defaultNameTextView.setText(bluetoothName);
        } else {
            holder.defaultNameTextView.setText(android.R.string.unknownName);
        }

        final String macAddress = beacon.getBluetoothAddress();
        if (macAddress != null) {
            holder.macTextView.setText(macAddress);
        } else {
            holder.macTextView.setText(android.R.string.unknownName);
        }

        final String proximity = device.getProximity();
        if (proximity != null) {
            holder.proximityTextView.setText(proximity);
        } else {
            holder.proximityTextView.setText(android.R.string.unknownName);
        }

        final Integer color = device.getColor();
        if (color != null) {
            holder.colorTextView.setText(color);
        } else {
            holder.colorTextView.setText(android.R.string.unknownName);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deviceDetailName;
                if (device.getFriendlyName() != null) {
                    deviceDetailName = device.getFriendlyName();
                } else {
                    deviceDetailName = device.getAddress();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fragmentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Fragment currentFrag = fragmentActivity.getSupportFragmentManager()
                                        .findFragmentById(R.id.contentMainLayout);

//                                List<Fragment> fragments = new ArrayList<Fragment>();
//                                fragments.add(DeviceDetailFragment.newInstance(deviceDetailName));
//                                fragments.add(InnerFragment.newInstance(deviceDetailName));

//                                Fragment fragment = ViewPagerFragment.newInstance(fragments);
                                Fragment fragment = DeviceDetailFragmentProva.newInstance(deviceDetailName);
                                // check if the new Fragment is the same
                                // if it is, don't add to the back stack
                                if (currentFrag == null || !(currentFrag.getClass().equals(fragment.getClass()))) {

                                    fragmentActivity.getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.contentMainLayout, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void setDistance(DeviceViewHolder holder, Device device) {
        holder.altbeaconDistanceTextView.setText(String.format("%sm", df.format(device.getAltBeaconDistance())));

        holder.standardRawDistanceTextView.setText(String.format("%sm", df.format(device.getRawDistance())));

        holder.kalmanFilterDistanceTextView.setText(String.format("%s", df.format(device.getKalmanFilterDistance())));
    }

    private void setInfoDevice(DeviceViewHolder holder, Beacon beacon) {
        if (beacon.getServiceUuid() == 0xfeaa) {

            holder.visibilityUUIDLinearLayout.setVisibility(View.GONE);
            holder.visibilityNameSpaceLinearLayout.setVisibility(View.VISIBLE);

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

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;
        TextView macTextView;
        TextView majorTextView;
        TextView minorTextView;
        TextView txPowerTextView;
        TextView rssiTextView;
        TextView friendlyNameTextView;
        TextView defaultNameTextView;
        TextView uuidTextView;
        TextView nameSpaceTextView;
        TextView instanceTextView;
        TextView proximityTextView;
        TextView colorTextView;
        TextView altbeaconDistanceTextView;
        TextView standardRawDistanceTextView;
        TextView kalmanFilterDistanceTextView;

        LinearLayout visibilityUUIDLinearLayout;
        LinearLayout visibilityNameSpaceLinearLayout;

        private DeviceViewHolder(View view) {

            super(view);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageBeacon);
            defaultNameTextView = (TextView) view.findViewById(R.id.value_default_name);
            friendlyNameTextView = (TextView) view.findViewById(R.id.value_friendly_name);
            macTextView = (TextView) view.findViewById(R.id.value_mac_address);
            majorTextView = (TextView) view.findViewById(R.id.value_major);
            minorTextView = (TextView) view.findViewById(R.id.value_minor);
            txPowerTextView = (TextView) view.findViewById(R.id.value_power);
            rssiTextView = (TextView) view.findViewById(R.id.value_rssi);
            uuidTextView = (TextView) view.findViewById(R.id.value_uuid);
            nameSpaceTextView = (TextView) view.findViewById(R.id.value_name_space);
            proximityTextView = (TextView) view.findViewById(R.id.value_proximity);
            instanceTextView = (TextView) view.findViewById(R.id.value_instance);
            colorTextView = (TextView) view.findViewById(R.id.value_color);

            altbeaconDistanceTextView = (TextView) view.findViewById(R.id.value_altbeacon_distance);
            kalmanFilterDistanceTextView = (TextView) view.findViewById(R.id.value_kalman_filter_distance);
            standardRawDistanceTextView = (TextView) view.findViewById(R.id.value_standard_raw_distance);

            visibilityUUIDLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_uuid_minor_major_nmb);
            visibilityNameSpaceLinearLayout = (LinearLayout) view.findViewById(R.id.visibilityNameSpace_Instance);
        }

    }
}
