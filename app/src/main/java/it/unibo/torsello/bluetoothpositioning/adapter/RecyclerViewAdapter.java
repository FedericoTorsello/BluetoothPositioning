package it.unibo.torsello.bluetoothpositioning.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
import it.unibo.torsello.bluetoothpositioning.fragment.CollapsingToolbarLayoutFragment;
import it.unibo.torsello.bluetoothpositioning.fragment.DetailDeviceFrag;
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
    private FragmentManager fragmentManager;
    private FragmentActivity activity;

    public RecyclerViewAdapter(FragmentActivity activity, List<Device> deviceList) {
        devices = deviceList;
        df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
        mTypedValue = new TypedValue();
        this.activity = activity;
        fragmentManager = this.activity.getSupportFragmentManager();
        this.activity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    protected class DeviceViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final ImageView imageView;
        private final TextView macTextView;
        private final TextView majorTextView;
        private final TextView minorTextView;
        private final TextView measuredPowerTextView;
        private final TextView rssiTextView;
        private final TextView friendlyNameTextView;
        private final TextView defaultNameTextView;
        private final TextView distanceTextView;
        private final TextView uuidTextView;
        private final TextView nameSpaceTextView;
        private final TextView proximityTextView;
        private final LinearLayout visibilityUUIDLinearLayout;
        private final LinearLayout visibilityNameSpaceLinearLayout;

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
            visibilityUUIDLinearLayout = (LinearLayout) view.findViewById(R.id.visibility_uuid);
            visibilityNameSpaceLinearLayout = (LinearLayout) view.findViewById(R.id.visibilityNameSpace);
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        view.setBackgroundResource(mBackground);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, final int position) {

        try {
            final Device device = devices.get(position);
            Beacon beacon = devices.get(position).getBeacon();

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, DeviceDetailActivity.class);
//                    intent.putExtra(DeviceDetailActivity.EXTRA_NAME, device.getFriendlyName());
//                    context.startActivity(intent);

//                    fragmentManager.beginTransaction()
//                            .add(R.id.frameLayout, DetailDeviceFrag.newInstance())
//                            .addToBackStack(null)
//                            .commit();

                    fragmentManager.beginTransaction()
                            .add(R.id.frameLayout, new CollapsingToolbarLayoutFragment())
                            .addToBackStack(null)
                            .commit();

//                    DetailDeviceFrag fragment = DetailDeviceFrag.newInstance();
//                    fragment.show(fragmentManager,"ciao");
                }
            });

            holder.friendlyNameTextView.setText(device.getFriendlyName());
            holder.defaultNameTextView.setText(beacon.getBluetoothName());
            holder.macTextView.setText(beacon.getBluetoothAddress());
            holder.measuredPowerTextView.setText(String.valueOf(beacon.getTxPower()));
            holder.rssiTextView.setText(String.valueOf(beacon.getRssi()));
            holder.proximityTextView.setText(device.getProximity());

            holder.distanceTextView.setText(String.format(Locale.getDefault(),
                    "DIST_KF1: %sm \n" +
                            "DIST_KF2: %sm \n" +
                            "DIST_KF3: %sm \n" +
//                            "DIST_KF4: %sm \n" +
                            "DIST_A: %sm \n" +
                            "DIST_B: %sm \n" +
                            "DIST_C: %sm \n" +
                            "DIST_D: %sm",
                    df.format(device.getDistanceKalmanFilter1()),
                    df.format(device.getDistanceKalmanFilter2()),
                    df.format(device.getDistanceKalmanFilter3()),
//                    df.format(device.getDistanceKalmanFilter4()),
                    df.format(device.getDist()),
                    df.format(beacon.getDistance()),
                    df.format(device.getRawDistance()),
                    df.format(device.getDistanceInternet())));

            if (device.getImageBeacon() != null) {
                holder.imageView.setImageResource(device.getImageBeacon());
            } else {
                holder.imageView.setImageResource(R.drawable.beacon_unknown);
            }

            if (beacon.getServiceUuid() == 0xfeaa) {
                if (beacon.getBeaconTypeCode() == 0x00) {
                    holder.visibilityUUIDLinearLayout.setVisibility(View.GONE);
                    holder.visibilityNameSpaceLinearLayout.setVisibility(View.VISIBLE);

                    // Eddystone-UID
                    holder.nameSpaceTextView.setText(beacon.getId1().toString());
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
                holder.uuidTextView.setText(beacon.getId1().toString());
                holder.majorTextView.setText(beacon.getId2().toString());
                holder.minorTextView.setText(beacon.getId3().toString());
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
