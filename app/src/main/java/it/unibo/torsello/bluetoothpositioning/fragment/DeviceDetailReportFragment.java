package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailReportFragment extends Fragment
        implements DeviceDetailInner0Fragment.OnRecordingReport {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private TextView textView;

    public static DeviceDetailReportFragment newInstance() {
        DeviceDetailReportFragment fragment = new DeviceDetailReportFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "report");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_device_detail_report, container, false);

        textView = (TextView) root.findViewById(R.id.textView_report);

        return root;
    }

    @Override
    public void record(String newRecord) {
        textView.setText(newRecord);
    }

}
