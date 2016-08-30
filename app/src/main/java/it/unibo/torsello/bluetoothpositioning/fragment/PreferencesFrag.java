package it.unibo.torsello.bluetoothpositioning.fragment;

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
import it.unibo.torsello.bluetoothpositioning.constants.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.kalman_filter.KalmanFilter3;

/**
 * Created by federico on 18/07/16.
 */
public class PreferencesFrag extends Fragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences preferences;

    private OnSettingsListener onSettingsListener;
    private DecimalFormat df;
    private KalmanFilter3 kalmanFilter3;

    public interface OnSettingsListener {
        void updateKalmanNoise(double value);

        void isSelfCorrection(boolean isChecked);

        void isWalkDetection(boolean isChecked);
    }

    public static PreferencesFrag newInstance(String message) {
        PreferencesFrag fragment = new PreferencesFrag();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        kalmanFilter3 = KalmanFilter3.getInstance();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpKalmanSeek();
//        setUpSelfcorrectingSwitch();
        setUpWalkDetectionSwitch();
        setSortByDistance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_preferences, container, false);
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
        SeekBar kalmanSeek = (SeekBar) getActivity().findViewById(R.id.kalmanSeek);
        int seekValue = preferences.getInt(SettingConstants.KALMAN_SEEK_VALUE, 83);
        kalmanSeek.setProgress(seekValue);

        final TextView kalmanFilterValue = (TextView) getActivity().findViewById(R.id.kalmanValue);
        kalmanFilterValue.setText(df.format(kalmanFilter3.getCalculatedNoise(seekValue)));
        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekValue, boolean fromUser) {
                kalmanFilterValue.setText(df.format(kalmanFilter3.getCalculatedNoise(seekValue)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = preferences.edit();
                int progress = seekBar.getProgress();
                editor.putInt(SettingConstants.KALMAN_SEEK_VALUE, progress);
                editor.apply();
                onSettingsListener.updateKalmanNoise(kalmanFilter3.getCalculatedNoise(progress));
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the self-correcting beacon switch */
//    private void setUpSelfcorrectingSwitch() {
//        final Switch selfCorrectionSwitch = (Switch) getActivity().findViewById(R.id.selfCorrectionSwitch);
//        boolean selfCorrection = preferences.getBoolean(SettingConstants.SELF_CORRECTING_BEACON, true);
//        selfCorrectionSwitch.setChecked(selfCorrection);
//        selfCorrectionSwitch.setText((selfCorrection) ? R.string.settings_enabled : R.string.settings_disabled);
//        selfCorrectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(SettingConstants.SELF_CORRECTING_BEACON, isChecked);
//                editor.apply();
//                selfCorrectionSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
//            }
//        });
//    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the walk detection switch */
    private void setUpWalkDetectionSwitch() {
        final Switch wdSwitch = (Switch) getActivity().findViewById(R.id.walkDetectionSwitch);
        boolean walkDetection = preferences.getBoolean(SettingConstants.WALK_DETECTION, false);
        wdSwitch.setChecked(walkDetection);
        wdSwitch.setText((walkDetection) ? R.string.settings_enabled : R.string.settings_disabled);
        wdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SettingConstants.WALK_DETECTION, isChecked);
                editor.apply();
                wdSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
                onSettingsListener.isWalkDetection(isChecked);
            }
        });
    }


    private void setSortByDistance() {
        final Switch sortByDistanceSwitch = (Switch) getActivity().findViewById(R.id.sortByDistanceSwitch);
        boolean setSortByDistance = preferences.getBoolean(SettingConstants.SORT_BY_DISTANCE, true);
        sortByDistanceSwitch.setChecked(setSortByDistance);
        sortByDistanceSwitch.setText((setSortByDistance) ? R.string.settings_enabled : R.string.settings_disabled);
        sortByDistanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SettingConstants.SORT_BY_DISTANCE, isChecked);
                editor.apply();
                sortByDistanceSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
            }
        });
    }

}
