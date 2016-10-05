package it.unibo.torsello.bluetoothpositioning.task;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class SaveImageTask extends AsyncTask<byte[], Void, Void> {

    private FragmentActivity activity;

    public SaveImageTask(FragmentActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    private FragmentActivity getActivity() {
        return activity;
    }

    @Override
    protected Void doInBackground(byte[]... data) {

        // Write to SD Card
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/" + getActivity().getString(R.string.app_name));
            dir.mkdirs();

            String fileName = String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(data[0]);
            outStream.flush();
            outStream.close();

            refreshGallery(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void refreshGallery(File file) {
        if (file != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(mediaScanIntent);

            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Your picture has been saved", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.fab),
                    "Image retrieval failed", Snackbar.LENGTH_SHORT).show();
        }
    }
}
