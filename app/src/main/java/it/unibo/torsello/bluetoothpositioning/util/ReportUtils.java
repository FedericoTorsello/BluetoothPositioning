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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.activities.ApplicationActivity;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;

/**
 * Created by federico on 11/10/16.
 */

public class ReportUtils {

    private String idDeviceSelectedName;

    private FragmentActivity activity;

    private String rssiFilterSelected;
    private ArrayList<Double> arduinoValues;
    private ArrayList<Double> rawValues;
    private ArrayList<Double> altBeaconValues;

    private ArrayList<Double> kFilterValues;
    private String formattedDate;

    private String formattedTime;
    private DecimalFormat dfValues;

    private DecimalFormat dfJsonFile;

    private Gson gson;
    private File jsonFile;
    private int indexFile = 1;

    private SharedPreferences preferences;

    public ReportUtils(FragmentActivity fragmentActivity, String deviceName) {
        this.activity = fragmentActivity;
        this.idDeviceSelectedName = deviceName;

        arduinoValues = new ArrayList<>();
        rawValues = new ArrayList<>();
        altBeaconValues = new ArrayList<>();
        kFilterValues = new ArrayList<>();

        formattedDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        formattedTime = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        dfValues = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());

        dfJsonFile = new DecimalFormat("00", DecimalFormatSymbols.getInstance());


        gson = new GsonBuilder().setPrettyPrinting().create();

        preferences = getActivity().
                getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void createReport() {

        double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);

        String tempFilter = "";
        if (rssiFilterSelected != null) {
            tempFilter = rssiFilterSelected;
        }

        rssiFilterSelected = ((ApplicationActivity) getActivity()).getRssiFilterSelected();

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

            if (!rssiFilterSelected.equals(tempFilter)) {
                indexFile = 1;
            }

            jsonFile = new File(subDir3, getNameFile());

            if (jsonFile.exists()) {
                if (!rssiFilterSelected.equals(tempFilter)) {
                    indexFile = 1;
                } else {
                    String name = jsonFile.getName();
                    String substring = name.substring(name.length() - 7, name.length() - 5);
                    indexFile = Integer.parseInt(substring);
                    indexFile++;

                    jsonFile = new File(subDir3, getNameFile());
                }
            } else {
                indexFile = 1;
            }

            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeJsonFile();

            writeResumeFile();
        }
    }

    private String getNameFile() {
        return String.format(idDeviceSelectedName + " - %s.json", dfJsonFile.format(indexFile));
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

        sb.append("Reference: ").append(indexFile).append("m").append("\n\n");

        sb.append(appendResume("Raw", rawValues));

        sb.append(appendResume("AltBeacon", altBeaconValues));

        if (isKalmanFilterEnabled) {
            sb.append(appendResume("Kalman Filter", kFilterValues));
        } else {
            sb.append("Kalman Filter").append("\n");
            sb.append("no data - filter disabled").append("\n\n");
        }
        if (!arduinoValues.isEmpty()) {
            sb.append(appendResume("Arduino", arduinoValues));
        } else {
            sb.append("Arduino").append("\n");
            sb.append("no data - arduino not connected");
        }

        return String.valueOf(sb);
    }

    private String appendResume(String arg, ArrayList<Double> value) {

        StringBuilder sb = new StringBuilder();

        sb.append(arg).append("\n");
        sb.append("Min:\t").append(getMinAsString(value))
                .append("\t\tdeviation:\t").append(getDeviation(Collections.min(value))).append("\n");
        sb.append("Max:\t").append(getMaxAsString(value))
                .append("\t\tdeviation:\t").append(getDeviation(Collections.max(value))).append("\n");
        sb.append("Avg:\t").append(getAvgAsString(value))
                .append("\t\tdeviation:\t").append(getDeviation(getAvg(value))).append("\n");
        sb.append("\n\n");
        return String.valueOf(sb);
    }

    private String getMinAsString(ArrayList<Double> value) {
        return dfValues.format(Collections.min(value)) + "m";
    }

    private String getMaxAsString(ArrayList<Double> value) {
        return dfValues.format(Collections.max(value)) + "m";
    }

    private String getAvgAsString(ArrayList<Double> value) {
        return dfValues.format(getAvg(value)) + "m";
    }

    private Double getAvg(ArrayList<Double> value) {
        Double sum = 0D;
        if (!value.isEmpty()) {
            for (Double mark : value) {
                sum += mark;
            }
            return sum / value.size();
        }
        return sum;
    }

    private String getDeviation(Double value) {

        double deviation = (value - indexFile);
        if (deviation >= 0) {
            return "+" + dfValues.format(deviation) + "m";
        } else {
            return dfValues.format(deviation) + "m";
        }
    }

    private void writeResumeFile() {

        try {

            String fileTxtName = String.format(idDeviceSelectedName + " - %s.txt", dfJsonFile.format(indexFile));
            File myFile = new File(jsonFile.getParent(), fileTxtName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(getResume());
            myOutWriter.close();
            fOut.close();
            refreshDirectory(myFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeJsonFile() {

        double processNoise = preferences.getFloat(SettingConstants.KALMAN_NOISE_VALUE, 0);
        boolean isKalmanFilterEnabled = preferences.getBoolean(SettingConstants.KALMAN_FILTER_ENABLED, true);

        try {
            Writer writer = new FileWriter(jsonFile);

            JsonWriter jw = gson.newJsonWriter(writer);

            jw.beginObject();
            {
                jw.name("id").value("report " + dfJsonFile.format(indexFile));
                jw.name("date").value(formattedDate);
                jw.name("time").value(formattedTime);
                jw.name("filter").value(rssiFilterSelected);
                jw.name("reference").value(indexFile + "m");
                jw.name("distance_estimation");
                jw.beginObject();
                {
                    jw.name("raw");
                    jw.beginObject();
                    {
                        appendData(jw, rawValues);

                        jw.name("raw_values");
                        jw.beginArray();
                        for (Double value : rawValues) {
                            jw.value(value);
                        }
                        jw.endArray();
                    }
                    jw.endObject();

                    jw.name("altbeacon");
                    jw.beginObject();
                    {
                        appendData(jw, altBeaconValues);

                        jw.name("altbeacon_values");
                        jw.beginArray();
                        for (Double value : altBeaconValues) {
                            jw.value(value);
                        }
                        jw.endArray();
                    }
                    jw.endObject();


                    if (isKalmanFilterEnabled) {

                        jw.name("kFilter_processNoise").value(processNoise);

                        jw.name("kFilter");
                        jw.beginObject();
                        {
                            appendData(jw, kFilterValues);

                            jw.name("kFilter_values");
                            jw.beginArray();
                            for (Double value : kFilterValues) {
                                jw.value(value);
                            }
                            jw.endArray();
                        }
                        jw.endObject();

                    } else {
                        jw.name("kFilter").value(getActivity().getString(R.string.kalman_filter_disabled));
                    }

                    if (!arduinoValues.isEmpty()) {
                        jw.name("arduino");
                        jw.beginObject();
                        {
                            appendData(jw, arduinoValues);

                            jw.name("arduino_values");
                            jw.beginArray();
                            for (Double value : arduinoValues) {
                                jw.value(value);
                            }
                            jw.endArray();
                        }
                        jw.endObject();

                    } else {
                        jw.name("arduino").value("no data - arduino not connected");
                    }
                }
                jw.endObject();
            }
            jw.endObject();

            writer.close();

            refreshDirectory(jsonFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonWriter appendData(JsonWriter jw, ArrayList<Double> value) throws IOException {
        jw.name("min").value(getMinAsString(value));
        jw.name("max").value(getMaxAsString(value));
        jw.name("avg").value(getAvgAsString(value));
        jw.name("min dev").value(getDeviation(Collections.min(value)));
        jw.name("max dev").value(getDeviation(Collections.max(value)));
        jw.name("avg dev").value(getDeviation(getAvg(value)));
        return jw;
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