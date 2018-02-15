package com.grizzly.keepup.login.setup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grizzly.keepup.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetupRunnerFragment extends Fragment {


    public SetupRunnerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        

        return inflater.inflate(R.layout.fragment_setup_runner, container, false);
    }

}
