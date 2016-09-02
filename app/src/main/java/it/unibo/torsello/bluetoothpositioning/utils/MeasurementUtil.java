package it.unibo.torsello.bluetoothpositioning.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

//import org.altbeacon.beacon.Beacon;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 * <p>
 * Utility class for performing measurements on a test beacon
 */
public class MeasurementUtil {
    private double lastDistance;
    private double lastRawDistance;
    private double lastDistanceWOSC;
    private double lastAltBeaconDistance;
    private double processNoise;

    public MeasurementUtil() {
    }

    public double getMeasurement() {
        return lastDistance;
    }

    public double getAltBeaconMeasurement() {
        return lastAltBeaconDistance;
    }

    public double getRawMeasurement() {
        return lastRawDistance;
    }

    public double getWOSCMeasurement() {
        return lastDistanceWOSC;
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

    public void export(String data) {
        if (isExternalStorageWritable()) {
            String filename = "/Documents/" + processNoise + "_" + System.currentTimeMillis() + ".txt";
            File root = Environment.getExternalStorageDirectory();
            File textfile = new File(root, filename);

            try {
                textfile.createNewFile();
                FileOutputStream f = new FileOutputStream(textfile);
                PrintWriter out = new PrintWriter(f);
                out.print(data);
                out.flush();
                out.close();
                f.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
