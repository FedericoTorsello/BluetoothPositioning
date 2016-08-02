package it.unibo.torsello.bluetoothpositioning.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.models.IBeacon;

/**
 * Created by federico on 21/07/16.
 * Displays basic information about beacon.
 */
public class LeDeviceListAdapter extends ArrayAdapter<Beacon> {

    private LayoutInflater inflater;
    double oldRSSI, newRSSI, Cangle;
    double GPOSX, GPOSY;

    public LeDeviceListAdapter(Context context, int textViewResourceId, List<Beacon> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
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

        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        Beacon b = getItem(position);
        holder.macTextView.setText(String.format("MAC: %s", b.getBluetoothAddress()));
//        holder.nameTextView.setText(String.format(Locale.getDefault(), "DIST: %sm", df.format(b.getDist())));

//        holder.nameTextView.setText(String.format(Locale.getDefault(),
//                "DIST_1: %.2fm \n" +
//                        "DIST_2: %.2fm \n" +
//                        "DIST_KF1: %.2fm \n" +
//                        "DIST_NOF: %.2fm \n" +
//                        "DIST_A: %.2fm \n" +
//                        "DIST_B: %.2fm \n" +
//                        "DIST_C: %.2fm \n",
//                b.getDist(),
//                b.getDist2(),
//                b.getDistanceInMetresKalmanFilter(),
//                b.getDistanceInMetresNoFiltered(),
//                b.getDistance(),
//                b.getRawDistance(),
//                b.getDistanceWOSC()));

//        holder.majorTextView.setText(String.format("Major: %s", b.getMajor()));
//        holder.minorTextView.setText(String.format("Minor: %s", b.getMinor()));
//        holder.measuredPowerTextView.setText(String.format("MPower: %sdB", b.getRssi()));
//        holder.rssiTextView.setText(String.format("RSSI: %sdB", b.getRssi()));
//        holder.row_uuid.setText(String.format("UUID: %s", b.getUuid()));
//        if (b.getAddress().equals("D9:80:00:B7:16:78")) {
//            holder.imageView.setImageResource(R.drawable.beacon_mint);
//        } else {
//            holder.imageView.setImageResource(R.drawable.beacon_unknown);
//        }

        String a = "";
        if (b.getServiceUuid() == 0xfeaa) {
//            if (b.getBeaconTypeCode() == 0x00) {
            if (b.getParserIdentifier().equals("Eddystone-UID")) {
                // Eddystone-UID
                a = "Eddystone-UID";
                holder.row_uuid.setText(String.format("NameSpace: %s", b.getId1()));
                holder.majorTextView.setText(String.format("Idemtif: %s", b.getId2()));

                int switchs = 0;
                double posX = 0.0;
                double posY = 0.0;
                double counter = 1;
                char Id1[] = b.getId1().toString().toCharArray();
                char Id2[] = b.getId2().toString().toCharArray();
                int c = Id2[Id2.length - 1] - 48;
//                scanString.append("char:").append(c);
                for (int i = 0; i < 10; i++) {
                    if (Id1[Id1.length - (i + 1)] != 'a') {
                        if (switchs == 0)
                            posX = posX / 10 + (Id1[Id1.length - (i + 1)] - 48);
                        if (switchs == 1) {
                            posX = posX + (Id1[Id1.length - (i + 1)] - 48) * counter;
                            counter *= 10;
                        }
                    } else {
                        posX /= 10;
                        switchs = 1;
                        counter = 1;
                    }
                }
                counter = 1;
                switchs = 0;
                for (int i = 0; i < 10; i++) {
                    if (Id2[Id2.length - (i + 1)] != 'a') {
                        if (switchs == 0)
                            posY = posY / 10 + (Id2[Id2.length - (i + 1)] - 48);
                        if (switchs == 1) {
                            posY = posY + (Id2[Id2.length - (i + 1)] - 48) * counter;
                            counter *= 10;
                        }
                    } else {
                        posY /= 10;
                        switchs = 1;
                        counter = 1;
                    }
                }
                newRSSI = b.getRssi();
                double distance = Math.abs(Math.abs(oldRSSI) - Math.abs(newRSSI));

                if (distance > 6) {
//                    GPOSX += Math.cos(Cangle) * distance * 0.000001;
//                    GPOSY += Math.sin(Cangle) * distance * 0.000001;
                    oldRSSI = newRSSI;
                }
//                if (b.getRssi() > -65) {
//                    GPOSX = posX;
//                    GPOSY = posY;
////                    POS = new LatLng(posX, posY);
//                }
//                Log.i("xxxx", distance + " ");


            } else if (b.getBeaconTypeCode() == 0x10) {
                // Eddystone-URL
                // String url = UrlBeaconUrlCompressor.uncompress(b.getId1().toByteArray());
                a = "Eddystone-URL";
            } else if (b.getBeaconTypeCode() == 0x20) {
                if (!b.getExtraDataFields().isEmpty()) {
                    // Eddystone-TLM
                    a = "Eddystone-TLM";
                }
            }
        } else if (b.getServiceUuid() == 0xbeac) {
            // AltBeacon
            a = "AltBeacon";
        } else if (b.getBeaconTypeCode() == 0x0215) { //533 in dec)
            // AppleIBeacon
            a = "AppleIBeacon";
            holder.nameTextView.setText(String.format(Locale.getDefault(), "DIST: %sm", df.format(b.getDistance())));
            holder.majorTextView.setText(String.format("Major: %s", b.getId2()));
            holder.minorTextView.setText(String.format("Minor: %s", b.getId3()));
            holder.measuredPowerTextView.setText(String.format("MPower: %sdB", b.getTxPower()));
            holder.rssiTextView.setText(String.format("RSSI: %sdB", b.getRssi()));
            holder.row_uuid.setText(String.format("UUID: %s", b.getId1()));
            if (b.getBluetoothAddress().equals("D9:80:00:B7:16:78")) {
                holder.imageView.setImageResource(R.drawable.beacon_mint);
            } else {
                holder.imageView.setImageResource(R.drawable.beacon_unknown);
            }
        } else if (b.getBeaconTypeCode() == 0x0101) {
            // EstimoteNearable
            a = "EstimoteNearable";
        }
//        Log.i("Tipo_beacon", a);

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
