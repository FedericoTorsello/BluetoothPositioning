package it.unibo.torsello.bluetoothpositioning.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by ocabafox on 7/8/2015.
 */
public class DetailDeviceFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static DetailDeviceFrag newInstance(String message) {
        DetailDeviceFrag fragment = new DetailDeviceFrag();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_details, container, false);
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbarLayout != null) {
            String title = getArguments().getString(EXTRA_MESSAGE);
            collapsingToolbarLayout.setTitle(title);
            //collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }
        return root;
    }
}
