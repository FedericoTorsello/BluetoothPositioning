package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unibo.torsello.bluetoothpositioning.R;

public class UsbFragment extends Fragment {

    private TextView twDistance;
    private TextView twDetails;
    private TextView twState;
    private UsbUtil usbUtil;


    public static UsbFragment newInstance() {
        return new UsbFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_usb, container, false);

        twDistance = (TextView) root.findViewById(R.id.tw_distance);
        twDetails = (TextView) root.findViewById(R.id.tw_details);
        twState = (TextView) root.findViewById(R.id.tw_state);

        usbUtil = new UsbUtil(getActivity());
        usbUtil.setOnReceiveNewData(new UsbUtil.OnReceiveNewData() {
            @Override
            public void getData(byte[] data) {
                final String text = new String(data).trim();
                if (!text.isEmpty()) {
                    twDistance.setText(text);
                }
            }

            @Override
            public void getStatus(String state) {
                twState.setText(state);
            }

            @Override
            public void getDetails(String details) {
                twDetails.setText(details);
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        usbUtil.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        usbUtil.resume();
    }
}
