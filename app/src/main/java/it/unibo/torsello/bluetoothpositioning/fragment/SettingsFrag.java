package it.unibo.torsello.bluetoothpositioning.fragment;

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

    private SharedPreferences settings;
    private Switch dependentSwitch;
    private Switch autoSwitch;
    private Switch subscriptionSwitch;
    private SeekBar kalmanSeek;
    private TextView kalmanFilterValue;
    private Switch selfCorrectionSwitch;
    private Switch wdSwitch;
    private Switch gateSimulationSwitch;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private String mTitle;

    public static SettingsFrag newInstance(String message) {
        SettingsFrag f = new SettingsFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

//        dependentSwitch = (Switch) view.findViewById(R.id.dependentSwitch);
//        autoSwitch = (Switch) view.findViewById(R.id.automaticallySwitch);
//        subscriptionSwitch = (Switch) view.findViewById(R.id.subscriptionSwitch);
        kalmanSeek = (SeekBar) view.findViewById(R.id.kalmanSeek);
        kalmanFilterValue = (TextView) view.findViewById(R.id.kalmanValue);
        selfCorrectionSwitch = (Switch) view.findViewById(R.id.selfCorrectionSwitch);
        wdSwitch = (Switch) view.findViewById(R.id.walkDetectionSwitch);
//        gateSimulationSwitch = (Switch) view.findViewById(R.id.gateSimulationSwitch);

        settings = getActivity().getSharedPreferences(SettingConstants.SETTINGS_PREFERENCES, 0);

//        setUpDependentPriceSwitch();
//        setUpAutomaticPaymentSwitch();
//        setUpSubscriptionSwitch();
        setUpKalmanSeek();
        setUpSelfcorrectingSwitch();
        setUpWalkDetectionSwitch();
//        setUpGateSimulationSwitch();

        return view;

    }

    /* Sets the correct text and adds a onChange listener to the kalman filter seekbar */
    private void setUpKalmanSeek() {
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
//                ((BLEPublicTransport) getActivity().getApplication()).beaconHelper.updateProcessNoise(seekBar.getProgress());
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange listener to the self-correcting beacon switch */
    private void setUpSelfcorrectingSwitch() {
        boolean selfcorrection = settings.getBoolean(SettingConstants.SELF_CORRECTING_BEACON, true);
        selfCorrectionSwitch.setChecked(selfcorrection);
        selfCorrectionSwitch.setText((selfcorrection) ? R.string.settings_enabled : R.string.settings_disabled);
        selfCorrectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.SELF_CORRECTING_BEACON, isChecked);
                editor.apply();
//                ((BLEPublicTransport) getActivity().getApplication()).beaconHelper.updateSelfCorrection(isChecked);
                selfCorrectionSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
            }
        });
    }

    /* Sets the correct text and adds an onCheckedChange listener to the walk detection switch */
    private void setUpWalkDetectionSwitch() {
        boolean walkDetection = settings.getBoolean(SettingConstants.WALK_DETECTION, false);
        wdSwitch.setChecked(walkDetection);
        wdSwitch.setText((walkDetection) ? R.string.settings_enabled : R.string.settings_disabled);
        wdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SettingConstants.WALK_DETECTION, isChecked);
                editor.apply();
//                ((BLEPublicTransport) getActivity().getApplication()).updateWalkDetectionListener(isChecked);
                wdSwitch.setText((isChecked) ? R.string.settings_enabled : R.string.settings_disabled);
            }
        });
    }
}
