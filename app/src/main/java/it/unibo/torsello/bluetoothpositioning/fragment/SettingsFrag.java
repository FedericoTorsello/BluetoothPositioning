package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.config.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.filter.KalmanFilter3;

/**
 * Created by federico on 18/07/16.
 */
public class SettingsFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences settings;
    private OnSettingsListener listener;

    public static SettingsFrag newInstance(String message) {
        SettingsFrag f = new SettingsFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    //     Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsListener) {
            listener = (OnSettingsListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement the OnAddDevicesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

        SeekBar kalmanSeek = (SeekBar) view.findViewById(R.id.kalmanSeek);
        TextView kalmanFilterValue = (TextView) view.findViewById(R.id.kalmanValue);
        Switch selfCorrectionSwitch = (Switch) view.findViewById(R.id.selfCorrectionSwitch);
        Switch wdSwitch = (Switch) view.findViewById(R.id.walkDetectionSwitch);
        Switch sortByDistanceSwitch = (Switch) view.findViewById(R.id.sortByDistanceSwitch);

        setUpKalmanSeek(kalmanSeek, kalmanFilterValue);
        setUpSelfcorrectingSwitch(selfCorrectionSwitch);
        setUpWalkDetectionSwitch(wdSwitch);
        setSortByDistance(sortByDistanceSwitch);

        return view;

    }

    /* Sets the correct text and adds a onChange listener to the kalman filter seekbar */
    private void setUpKalmanSeek(SeekBar kalmanSeek, final TextView kalmanFilterValue) {
        int kalmanSeekValue = settings.getInt(SettingConstants.KALMAN_SEEK_VALUE, 83);
        kalmanSeek.setProgress(kalmanSeekValue);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        kalmanFilterValue.setText(df.format(KalmanFilter3.getCalculatedNoise(kalmanSeekValue)));
        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                kalmanFilterValue.setText(df.format(KalmanFilter3.getCalculatedNoise(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SettingConstants.KALMAN_SEEK_VALUE, seekBar.getProgress());
                editor.apply();
                listener.updateProcessNoise(seekBar.getProgress());
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange listener to the self-correcting beacon switch */
    private void setUpSelfcorrectingSwitch(final Switch selfCorrectionSwitch) {
        boolean selfCorrection = settings.getBoolean(SettingConstants.SELF_CORRECTING_BEACON, true);
        selfCorrectionSwitch.setChecked(selfCorrection);
        selfCorrectionSwitch.setText((selfCorrection) ? R.string.settings_enabled : R.string.settings_disabled);
        selfCorrectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.SELF_CORRECTING_BEACON, isChecked);
                editor.apply();
//                listener.updateSelfCorrectionListener(isChecked);
                selfCorrectionSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange listener to the walk detection switch */
    private void setUpWalkDetectionSwitch(final Switch wdSwitch) {
        boolean walkDetection = settings.getBoolean(SettingConstants.WALK_DETECTION, false);
        wdSwitch.setChecked(walkDetection);
        wdSwitch.setText((walkDetection) ? R.string.settings_enabled : R.string.settings_disabled);
        wdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.WALK_DETECTION, isChecked);
                editor.apply();
                wdSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
                listener.updateWalkDetectionListener(isChecked);
            }
        });
    }


    private void setSortByDistance(final Switch sortByDistanceSwitch) {
        boolean setSortByDistance = settings.getBoolean(SettingConstants.SORT_BY_DISTANCE, true);
        sortByDistanceSwitch.setChecked(setSortByDistance);
        sortByDistanceSwitch.setText((setSortByDistance) ? R.string.settings_enabled : R.string.settings_disabled);
        sortByDistanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.SORT_BY_DISTANCE, isChecked);
                editor.apply();
                sortByDistanceSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
//                listener.updateSelfCorrectionListener(isChecked);
            }
        });
    }

    public interface OnSettingsListener {
        void updateWalkDetectionListener(boolean isChecked);

        //        void updateSelfCorrectionListener(boolean isChecked);
        void updateProcessNoise(int valueSeekBar);
    }
}
