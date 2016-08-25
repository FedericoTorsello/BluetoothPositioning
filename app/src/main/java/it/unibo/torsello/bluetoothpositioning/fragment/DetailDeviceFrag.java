package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by federico on 14/08/16.
 */
public class DetailDeviceFrag extends DialogFragment {

    public static DetailDeviceFrag newInstance() {
        return new DetailDeviceFrag();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.device_detail, container, false);
        setHasOptionsMenu(true);

        return root;
    }

}
