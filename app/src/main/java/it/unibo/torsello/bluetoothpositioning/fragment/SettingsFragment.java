package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KalmanFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class SettingsFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences preferences;
    private DecimalFormat df;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, "Settings");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preferences, container, false);
        setUpKalmanSeek(root);
        setSorting(root);
        setFiltering(root);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
        df = new DecimalFormat("0.00");
    }


    /* Sets the correct text and adds a onChange onSettingsListener to the kalman filter seekbar */
    private void setUpKalmanSeek(View root) {
        SeekBar kalmanSeek = (SeekBar) root.findViewById(R.id.kalmanSeek);
        int seekValue = preferences.getInt(SettingConstants.KALMAN_SEEK_VALUE, 83);
        kalmanSeek.setProgress(seekValue);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(SettingConstants.KALMAN_NOISE_VALUE,
                (float) KalmanFilter.getCalculatedNoise(seekValue));
        editor.apply();

        final TextView kalmanFilterValue = (TextView) root.findViewById(R.id.kalmanValue);
        kalmanFilterValue.setText(df.format(KalmanFilter.getCalculatedNoise(seekValue)));
        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekValue, boolean fromUser) {
                double calculatedNoise = KalmanFilter.getCalculatedNoise(seekValue);
                kalmanFilterValue.setText(df.format(calculatedNoise));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = preferences.edit();
                int progress = seekBar.getProgress();
                editor.putInt(SettingConstants.KALMAN_SEEK_VALUE, progress);
                editor.putFloat(SettingConstants.KALMAN_NOISE_VALUE,
                        (float) KalmanFilter.getCalculatedNoise(progress));
                editor.apply();
            }
        });
    }

    private void setSorting(View root) {
        RadioGroup rg = (RadioGroup) root.findViewById(R.id.radioGroupSortingMode);
        int checkedRadioButton = preferences.getInt(SettingConstants.DISTANCE_SORTING, rg.getCheckedRadioButtonId());
        rg.check(checkedRadioButton);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SettingConstants.DISTANCE_SORTING, checkedId);
                editor.apply();
            }
        });
    }

    private void setFiltering(View root) {
        RadioGroup rg = (RadioGroup) root.findViewById(R.id.radioGroupFilter);
        int checkedRadioButton = preferences.getInt(SettingConstants.FILTER_RSSI, rg.getCheckedRadioButtonId());
        rg.check(checkedRadioButton);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SettingConstants.FILTER_RSSI, checkedId);
                editor.apply();
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the walk detection switch */
//    private void setUpWalkDetectionSwitch() {
//        final Switch wdSwitch = (Switch) getActivity().findViewById(R.id.walkDetectionSwitch);
//        boolean walkDetection = preferences.getBoolean(SettingConstants.WALK_DETECTION, false);
//        wdSwitch.setChecked(walkDetection);
//        wdSwitch.setText((walkDetection) ? R.string.settings_enabled : R.string.settings_disabled);
//        wdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(SettingConstants.WALK_DETECTION, isChecked);
//                editor.apply();
//                wdSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
//                onSettingsListener.isWalkDetection(isChecked);
//            }
//        });
//    }

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

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the self-correcting beacon switch */
//    private void setUpArmaFilterSwitch(View root) {
//
//        final String armaFilterEnabled = String.format(getString(R.string.text_arma_filter),
//                getString(R.string.settings_enabled));
//        final String armaFilterDisabled = String.format(getString(R.string.text_arma_filter),
//                getString(R.string.settings_disabled));
//
//        Switch filterSwitch = (Switch) root.findViewById(R.id.switch_arma_filter);
//        boolean armaFilterChecked = preferences.getBoolean(SettingConstants.ARMA_FILTER, true);
//        filterSwitch.setChecked(armaFilterChecked);
//        filterSwitch.setText((armaFilterChecked) ? armaFilterEnabled : armaFilterDisabled);
//        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(SettingConstants.ARMA_FILTER, isChecked);
//                editor.apply();
//                buttonView.setText((isChecked) ? armaFilterEnabled : armaFilterDisabled);
//                onSettingsListener.isArmaFilter(isChecked);
//            }
//        });
//    }


}
