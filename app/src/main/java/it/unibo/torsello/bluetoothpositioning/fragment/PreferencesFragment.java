package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;
import it.unibo.torsello.bluetoothpositioning.kalmanFilter.KalmanFilter;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class PreferencesFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private SharedPreferences preferences;

    private OnSettingsListener onSettingsListener;
    private DecimalFormat df;

    public static PreferencesFragment newInstance(String message) {
        PreferencesFragment fragment = new PreferencesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_preferences, container, false);

        setUpKalmanSeek(root);
//        setUpSelfcorrectingSwitch();
//        setUpWalkDetectionSwitch();
        setSorting(root);

        setUpArmaFilterSwitch(root);

        return root;
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
    private void setUpKalmanSeek(View root) {
        SeekBar kalmanSeek = (SeekBar) root.findViewById(R.id.kalmanSeek);
        int seekValue = preferences.getInt(SettingConstants.KALMAN_SEEK_VALUE, 83);
        kalmanSeek.setProgress(seekValue);

        final TextView kalmanFilterValue = (TextView) root.findViewById(R.id.kalmanValue);
        kalmanFilterValue.setText(df.format(KalmanFilter.getCalculatedNoise(seekValue)));
        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekValue, boolean fromUser) {
                double calculatedNoise = KalmanFilter.getCalculatedNoise(seekValue);
                onSettingsListener.updateKalmanNoise(calculatedNoise);
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
                editor.apply();
                onSettingsListener.updateKalmanNoise(KalmanFilter.getCalculatedNoise(progress));
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

    private void setSorting(View root) {
        final RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.radioGroup);
        int checkedRadioButton = preferences.getInt(SettingConstants.DISTANCE_SORTING, radioGroup.getChildAt(0).getId());
        radioGroup.check(checkedRadioButton);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SettingConstants.DISTANCE_SORTING, checkedId);
                editor.apply();
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange onSettingsListener to the self-correcting beacon switch */
    private void setUpArmaFilterSwitch(View root) {

        final String armaFilterEnabled = String.format(getString(R.string.text_arma_filter),
                getString(R.string.settings_enabled));
        final String armaFilterDisabled = String.format(getString(R.string.text_arma_filter),
                getString(R.string.settings_disabled));

        Switch filterSwitch = (Switch) root.findViewById(R.id.switch_arma_filter);
        boolean armaFilterChecked = preferences.getBoolean(SettingConstants.ARMA_FILTER, true);
        filterSwitch.setChecked(armaFilterChecked);
        filterSwitch.setText((armaFilterChecked) ? armaFilterEnabled : armaFilterDisabled);
        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SettingConstants.ARMA_FILTER, isChecked);
                editor.apply();
                buttonView.setText((isChecked) ? armaFilterEnabled : armaFilterDisabled);
            }
        });
    }

    public interface OnSettingsListener {
        void updateKalmanNoise(double value);

        void isSelfCorrection(boolean isChecked);

        void isWalkDetection(boolean isChecked);

        void isArmaFilter(boolean isChecked);
    }

}
