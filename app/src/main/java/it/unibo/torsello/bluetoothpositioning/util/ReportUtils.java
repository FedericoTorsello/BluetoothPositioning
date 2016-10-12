package it.unibo.torsello.bluetoothpositioning.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;

/**
 * Created by federico on 11/10/16.
 */

public class ReportUtils {

    private Gson gson;

    private String idDeviceSelectedName;

    private int indexFile = 0;
    private String rssiFilterSelected;

    private ArrayList<Double> arduinoValues;
    private ArrayList<Double> rawValues;
    private ArrayList<Double> altBeaconValues;
    private ArrayList<Double> kFilterValues;

    private SimpleDateFormat sdf;


    private DecimalFormat df;
    private FragmentActivity activity;

    private File jsonFile;


    private SharedPreferences preferences;

    public ReportUtils(FragmentActivity fragmentActivity, String deviceName) {
        this.activity = fragmentActivity;
        this.idDeviceSelectedName = deviceName;

        arduinoValues = new ArrayList<>();
        rawValues = new ArrayList<>();
        altBeaconValues = new ArrayList<>();
        kFilterValues = new ArrayList<>();

        sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        df = new DecimalFormat("00", DecimalFormatSymbols.getInstance());

        gson = new GsonBuilder().setPrettyPrinting().create();

        preferences = getActivity().
                getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void createJson() {

        String formattedDate = sdf.format(Calendar.getInstance().getTime());

        double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);

        String tempFilter = "";
        if (rssiFilterSelected != null) {
            tempFilter = rssiFilterSelected;
        }

        rssiFilterSelected = ((ApplicationActivity) getActivity()).getRssiFilterSelected();

        if (!rssiFilterSelected.equals(tempFilter)) {
            indexFile = 0;
        }

        if (isExternalStorageWritable()) {
            File root = Environment.getExternalStorageDirectory();

            File dir = new File(root.getAbsolutePath()
                    + File.separator
                    + getActivity().getString(R.string.app_name)
                    + " Report");
            dir.mkdirs();

            File subDir1 = new File(dir.getAbsolutePath() + File.separator
                    + formattedDate);
            subDir1.mkdirs();

            File subDir2 = new File(subDir1.getAbsolutePath() + File.separator
                    + "KF process noise " + processNoise);
            subDir2.mkdirs();

            File subDir3 = new File(subDir2.getAbsolutePath() + File.separator
                    + rssiFilterSelected);
            subDir3.mkdirs();

            String fileName = String.format(idDeviceSelectedName + " - %s.json", df.format(indexFile));

            jsonFile = new File(subDir3, fileName);

            if (jsonFile.exists()) {
                if (!rssiFilterSelected.equals(tempFilter)) {
                    indexFile = 0;
                } else {
                    String name = jsonFile.getName();
                    String substring = name.substring(name.length() - 7, name.length() - 5);
                    indexFile = Integer.parseInt(substring);
                    indexFile++;
                    fileName = String.format(idDeviceSelectedName + " - %s.json", df.format(indexFile));
                    jsonFile = new File(subDir3, fileName);
                }

            } else {
                indexFile = 0;
            }

            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeJsonFile();

            refreshDirectory(jsonFile);

        }
    }

    public String getJson() {

        String temp = "";

        try {
            FileInputStream fin = new FileInputStream(jsonFile);

            int chars;

            while ((chars = fin.read()) != -1) {
                temp = temp + Character.toString((char) chars);
            }

            //string temp contains all the data of the file.
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void clearRecordedValues() {
        arduinoValues.clear();
        rawValues.clear();
        altBeaconValues.clear();
        kFilterValues.clear();
    }

    public String getResume() {

        boolean isKalmanFilterEnabled = preferences
                .getBoolean(SettingConstants.KALMAN_FILTER_ENABLED, false);

        StringBuilder sb = new StringBuilder();

        String min = "Min ";
        String max = "Max ";
        String avg = "AVG ";

        String arduino = "Arduino values: ";
        String altBeacon = "AltBeacon values: ";
        String raw = "Raw values: ";
        String kf = "Kalman Filter values: ";

        if (!arduinoValues.isEmpty()) {
            sb.append(min).append(arduino).append(Collections.min(arduinoValues)).append("\n");
            sb.append(max).append(arduino).append(Collections.max(arduinoValues)).append("\n");
            sb.append(avg).append(arduino).append(calculateAverage(arduinoValues)).append("\n");

            sb.append("\n\n");
        }

        sb.append(min).append(raw).append(Collections.min(rawValues)).append("\n");
        sb.append(max).append(raw).append(Collections.max(rawValues)).append("\n");
        sb.append(avg).append(raw).append(calculateAverage(rawValues)).append("\n");

        sb.append("\n\n");

        sb.append(min).append(altBeacon).append(Collections.min(altBeaconValues)).append("\n");
        sb.append(max).append(altBeacon).append(Collections.max(altBeaconValues)).append("\n");
        sb.append(avg).append(altBeacon).append(calculateAverage(altBeaconValues)).append("\n");

        sb.append("\n\n");

        if (isKalmanFilterEnabled) {
            sb.append(min).append(kf).append(Collections.min(kFilterValues)).append("\n");
            sb.append(max).append(kf).append(Collections.max(kFilterValues)).append("\n");
            sb.append(avg).append(kf).append(calculateAverage(kFilterValues)).append("\n");
        }

        return String.valueOf(sb);
    }

    private double calculateAverage(ArrayList<Double> value) {
        Double sum = 0D;
        if (!value.isEmpty()) {
            for (Double mark : value) {
                sum += mark;
            }
            return sum / value.size();
        }
        return sum;
    }

    private void writeJsonFile() {
        String formattedDate = sdf.format(Calendar.getInstance().getTime());
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String time = sdf2.format(Calendar.getInstance().getTime());

        double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);
        boolean isKalmanFilterEnabled = preferences.getBoolean(SettingConstants.KALMAN_FILTER_ENABLED, false);

        try {
            Writer writer = new FileWriter(jsonFile);

            JsonWriter jw = gson.newJsonWriter(writer);

            jw.beginObject();
            {
                jw.name("id").value("report " + df.format(indexFile));
                jw.name("date").value(formattedDate);
                jw.name("time").value(time);
                jw.name("filter").value(rssiFilterSelected);
                jw.name("distance_estimation");
                jw.beginObject();
                {
                    jw.name("arduino");
                    if (arduinoValues.isEmpty()) {
                        jw.value("no data");
                    } else {
                        jw.beginArray();
                        for (Double value : arduinoValues) {
                            jw.value(value);
                        }
                        jw.endArray();
                    }

                    jw.name("raw");
                    jw.beginArray();
                    for (Double value : rawValues) {
                        jw.value(value);
                    }
                    jw.endArray();

                    jw.name("altbeacon");
                    jw.beginArray();
                    for (Double value : altBeaconValues) {
                        jw.value(value);
                    }
                    jw.endArray();

                    jw.name("kFilter");
                    if (isKalmanFilterEnabled) {
                        jw.name("kFilter_processNoise").value(processNoise);
                        jw.beginArray();
                        for (Double value : kFilterValues) {
                            jw.value(value);
                        }
                        jw.endArray();
                    } else {
                        jw.value(getActivity().getString(R.string.kalman_filter_disabled));
                    }
                }
                jw.endObject();
            }
            jw.endObject();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void refreshDirectory(File file) {
        if (file != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(mediaScanIntent);

            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Report saved", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Report retrieval failed", Snackbar.LENGTH_SHORT).show();
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.d("sure", "not writeable");
        return false;
    }

    public void setAltBeaconValues(Double altBeaconValues) {
        this.altBeaconValues.add(altBeaconValues);
    }

    public void setArduinoValues(Double arduinoValues) {
        this.arduinoValues.add(arduinoValues);
    }

    public void setkFilterValues(Double kFilterValues) {
        this.kFilterValues.add(kFilterValues);
    }

    public void setRawValues(Double rawValues) {
        this.rawValues.add(rawValues);
    }
}