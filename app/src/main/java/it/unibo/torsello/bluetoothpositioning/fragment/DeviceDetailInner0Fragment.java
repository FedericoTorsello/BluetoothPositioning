package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.adapter.StatePagerAdapter;
import it.unibo.torsello.bluetoothpositioning.model.Device;
import it.unibo.torsello.bluetoothpositioning.observables.DeviceObservable;
import it.unibo.torsello.bluetoothpositioning.observables.UsbMeasurementObservable;
import it.unibo.torsello.bluetoothpositioning.util.ReportUtils;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class DeviceDetailInner0Fragment extends Fragment implements Observer {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String DEVICE_NAME = "DEVICE_NAME";

    private boolean check;

    private ReportUtils reportUtils;

    private DeviceObservable myDeviceObservable;
    private UsbMeasurementObservable myUsbObservable;

    private String idDeviceSelectedName;

    private OnRecordingResume onRecordingResume;

    private OnRecordingReport onRecordingReport;

    public interface OnRecordingResume {
        void record(String newRecord);

    }

    public interface OnRecordingReport {
        void record(String newRecord);
    }

    public static DeviceDetailInner0Fragment newInstance(String deviceName) {
        DeviceDetailInner0Fragment fragment = new DeviceDetailInner0Fragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "testing");
        args.putString(DEVICE_NAME, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idDeviceSelectedName = getArguments().getString(DEVICE_NAME);

        myDeviceObservable = DeviceObservable.getInstance();
        myUsbObservable = UsbMeasurementObservable.getInstance();

        reportUtils = new ReportUtils(getActivity(), idDeviceSelectedName);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof DeviceDetailReportFragment) {
            onRecordingReport = (OnRecordingReport) childFragment;
        }

        if (childFragment instanceof DeviceDetailResumeFragment) {
            onRecordingResume = (OnRecordingResume) childFragment;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_device_detail_inner_0, container, false);

        final ToggleButton toggle = (ToggleButton) root.findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = isChecked;
                if (isChecked) {

                    reportUtils.clearRecordedValues();

                    toggle.setClickable(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toggle.setClickable(true);
                            toggle.setChecked(false);

                            onRecordingReport.record(reportUtils.getJson());
                            onRecordingResume.record(reportUtils.getResume());
                        }
                    }, 5000);

                    Snackbar.make(getActivity().findViewById(R.id.fab),
                            "Start recording", Snackbar.LENGTH_SHORT).show();
                } else {
                    reportUtils.createJson();
                }
            }
        });

        addChildFragment(root);

        return root;
    }

    @Override
    public void onPause() {
        myDeviceObservable.deleteObserver(this);
        myUsbObservable.deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        myDeviceObservable.addObserver(this);
        myUsbObservable.addObserver(this);
    }

    private void addChildFragment(View root) {

        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.view_pager_inner_0);

        // avoid casual fragment's destruction
        mViewPager.setOffscreenPageLimit(getFragments().size());

        StatePagerAdapter myPageAdapter = new StatePagerAdapter(getChildFragmentManager(), getFragments());
        mViewPager.setAdapter(myPageAdapter);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        // nested fragment 0
        fragments.add(DeviceDetailResumeFragment.newInstance());

        // nested fragment 1
        fragments.add(DeviceDetailReportFragment.newInstance());

        return fragments;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof UsbMeasurementObservable) {
            if (arg instanceof Double) {
                Double arduinoDistance = (Double) arg;
                if (check) {
                    reportUtils.setArduinoValues(arduinoDistance);
                }
            }
        }

        if (observable instanceof DeviceObservable) {
            if (arg instanceof List) {

                List<Device> devices = (List<Device>) arg;

                for (Device deviceSelected : devices) {
                    if (deviceSelected.getFriendlyName().equals(idDeviceSelectedName) ||
                            deviceSelected.getAddress().equals(idDeviceSelectedName)) {

                        if (check) {
                            reportUtils.setAltBeaconValues(deviceSelected.getAltBeaconDistance());
                            reportUtils.setkFilterValues(deviceSelected.getKalmanFilterDistance());
                            reportUtils.setRawValues(deviceSelected.getRawDistance());
                        }
                    }
                }
            }
        }

    }

}
