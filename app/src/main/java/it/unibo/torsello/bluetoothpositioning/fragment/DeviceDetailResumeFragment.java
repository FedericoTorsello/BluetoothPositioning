package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by federico on 12/10/16.
 */

public class DeviceDetailResumeFragment extends Fragment
        implements DeviceDetailInner0Fragment.OnRecordingResume {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private TextView textView;

    public static DeviceDetailResumeFragment newInstance() {
        DeviceDetailResumeFragment fragment = new DeviceDetailResumeFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "Resume");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_device_detail_resume, container, false);

        textView = (TextView) root.findViewById(R.id.textView_resume);

        return root;
    }

    @Override
    public void record(String newRecord) {
        textView.setText(newRecord);
    }
}
