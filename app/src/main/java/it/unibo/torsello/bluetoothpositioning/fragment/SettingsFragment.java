package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import it.unibo.torsello.bluetoothpositioning.R;
import it.unibo.torsello.bluetoothpositioning.constant.KFilterConstants;
import it.unibo.torsello.bluetoothpositioning.constant.SettingConstants;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);
        df = new DecimalFormat("0.0#");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        setKalmanFilterSeekBar(root);

        setSorting(root);

        setFiltering(root);

        return root;
    }

    /* Sets the correct text and adds a onChange onSettingsListener to the kalman filter seekbar */
    private void setKalmanFilterSeekBar(View root) {
        SeekBar kalmanSeek = (SeekBar) root.findViewById(R.id.kalmanSeek);
        int seekValue = preferences.getInt(SettingConstants.KALMAN_SEEKBAR_VALUE, 4);
        kalmanSeek.setMax(10);
        kalmanSeek.setProgress(kalmanSeek.getMax() / 2);

        final TextView kalmanFilterValue = (TextView) root.findViewById(R.id.kalmanValue);
        kalmanFilterValue.setText(df.format(getCalculatedNoise(seekValue)));

        kalmanSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekValue, boolean fromUser) {
                kalmanFilterValue.setText(df.format(getCalculatedNoise(seekValue)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = preferences.edit();
                int progress = seekBar.getProgress();
                editor.putInt(SettingConstants.KALMAN_SEEKBAR_VALUE, progress);
                editor.putFloat(SettingConstants.KALMAN_NOISE_VALUE, getCalculatedNoise(progress));
                editor.apply();
                kalmanFilterValue.setText(df.format(getCalculatedNoise(progress)));
            }
        });
    }

    public static float getCalculatedNoise(int p) {
        double percent = (p / 10D);
        double noise = KFilterConstants.KALMAN_NOISE_MIN +
                (KFilterConstants.KALMAN_NOISE_MAX - KFilterConstants.KALMAN_NOISE_MIN) * percent;

        return (float) noise;

    }

    private void setSorting(View root) {
        RadioGroup rg = (RadioGroup) root.findViewById(R.id.radioGroupSortingMode);
        int checkedRadioButton;
        if (rg.getCheckedRadioButtonId() != 0) {
            checkedRadioButton = preferences.getInt(SettingConstants.DISTANCE_SORTING, rg.getCheckedRadioButtonId());
        } else {
            checkedRadioButton = 0;
        }
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
        int checkedRadioButton;
        if (rg.getCheckedRadioButtonId() != 0) {
            checkedRadioButton = preferences.getInt(SettingConstants.FILTER_RSSI, rg.getCheckedRadioButtonId());
        } else {
            checkedRadioButton = 0;
        }
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
//        final Switch wdSwitch = (Switch) getActivity().findViewById(INITIAL_PROCESS_NOISE.id.walkDetectionSwitch);
//        boolean walkDetection = preferences.getBoolean(SettingConstants.WALK_DETECTION, false);
//        wdSwitch.setChecked(walkDetection);
//        wdSwitch.setText((walkDetection) ? INITIAL_PROCESS_NOISE.string.settings_enabled : INITIAL_PROCESS_NOISE.string.settings_disabled);
//        wdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(SettingConstants.WALK_DETECTION, isChecked);
//                editor.apply();
//                wdSwitch.setText((isChecked) ? INITIAL_PROCESS_NOISE.string.settings_enabled : INITIAL_PROCESS_NOISE.string.settings_disabled);
//                onSettingsListener.isWalkDetection(isChecked);
//            }
//        });
//    }

}
