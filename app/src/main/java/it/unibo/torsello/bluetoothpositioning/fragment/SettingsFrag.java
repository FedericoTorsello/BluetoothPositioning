package it.unibo.torsello.bluetoothpositioning.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences settings;

    private OnSettingsListener onSettingsListener;

    public interface OnSettingsListener {
        void updateKalmanNoise(double value);

        void isSelfCorrection(boolean isChecked);

        void isWalkDetection(boolean isChecked);
    }

    public interface Listener {
        public void isWalkDetection(boolean isChecked);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }


    public static SettingsFrag newInstance(String message) {
        SettingsFrag fragment = new SettingsFrag();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpKalmanSeek();
        setUpSelfcorrectingSwitch();
        setUpWalkDetectionSwitch();
        setSortByDistance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_frag, container, false);
    }

    //     Store the onSettingsListener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsListener) {
            onSettingsListener = (OnSettingsListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement the Listener ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSettingsListener = null;
    }


    /* Sets the correct text and adds a onChange onSettingsListener to the kalman filter seekbar */
    private void setUpKalmanSeek() {
        final SeekBar kalmanSeek = (SeekBar) getActivity().findViewById(R.id.kalmanSeek);
        final TextView kalmanFilterValue = (TextView) getActivity().findViewById(R.id.kalmanValue);
        int kalmanSeekValue = settings.getInt(SettingConstants.KALMAN_SEEK_VALUE, 83);
        kalmanSeek.setProgress(kalmanSeekValue);
        final DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        kalmanFilterValue.setText(df.format(KalmanFilter3.getCalculatedNoise(kalmanSeekValue)));
        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kalmanFilterValue.setText(df.format(KalmanFilter3.getCalculatedNoise(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = settings.edit();
                int progress = seekBar.getProgress();
                editor.putInt(SettingConstants.KALMAN_SEEK_VALUE, progress);
                editor.apply();
                onSettingsListener.updateKalmanNoise(KalmanFilter3.getCalculatedNoise(progress));
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the self-correcting beacon switch */
    private void setUpSelfcorrectingSwitch() {
        final Switch selfCorrectionSwitch = (Switch) getActivity().findViewById(R.id.selfCorrectionSwitch);
        boolean selfCorrection = settings.getBoolean(SettingConstants.SELF_CORRECTING_BEACON, true);
        selfCorrectionSwitch.setChecked(selfCorrection);
        selfCorrectionSwitch.setText((selfCorrection) ? R.string.settings_enabled : R.string.settings_disabled);
        selfCorrectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.SELF_CORRECTING_BEACON, isChecked);
                editor.apply();
                selfCorrectionSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the walk detection switch */
    private void setUpWalkDetectionSwitch() {
        final Switch wdSwitch = (Switch) getActivity().findViewById(R.id.walkDetectionSwitch);
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
                onSettingsListener.isWalkDetection(isChecked);
            }
        });
    }


    private void setSortByDistance() {
        final Switch sortByDistanceSwitch = (Switch) getActivity().findViewById(R.id.sortByDistanceSwitch);
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
            }
        });
    }

}
