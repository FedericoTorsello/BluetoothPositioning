package it.unibo.torsello.bluetoothpositioning.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.torsello.bluetoothpositioning.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";


    public InnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment InnerFragment.
     */
    public static InnerFragment newInstance(String param1) {
        InnerFragment fragment = new InnerFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.inner_prova, container, false);
    }

}
