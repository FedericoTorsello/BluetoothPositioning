package it.unibo.torsello.bluetoothpositioning.examplesCamera;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class SaveImageTask extends AsyncTask<byte[], Void, Void> {

    private static final String TAG = "SaveImageTask";
    FragmentActivity fragmentActivity;

    public SaveImageTask(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    protected Void doInBackground(byte[]... data) {

        // Write to SD Card
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/camtest");
//                    dir.mkdirs();

            String fileName = String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(data[0]);
            outStream.flush();
            outStream.close();

            Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

            refreshGallery(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        fragmentActivity.sendBroadcast(mediaScanIntent);
    }
}
