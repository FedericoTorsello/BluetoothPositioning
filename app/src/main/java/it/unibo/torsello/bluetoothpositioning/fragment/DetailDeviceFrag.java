package it.unibo.torsello.bluetoothpositioning.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.altbeacon.beacon.Region;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * Created by ocabafox on 7/8/2015.
 */
public class DetailDeviceFrag extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

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


        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab2);
        assert fab != null;
        Snackbar.make(fab, R.string.snackbar_start_scanning, Snackbar.LENGTH_LONG).show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                Snackbar.make(view, "ciao", Snackbar.LENGTH_SHORT).show();
            }

        });


        return root;
    }
}
