package smart.tuke.sk.keepup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import smart.tuke.sk.keepup.MainActivity;
import smart.tuke.sk.keepup.MapsActivity;


public class RunFragment extends BaseFragment {

    public static RunFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onStart() {
        super.onStart();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent( getActivity(),MapsActivity.class);
                getActivity().startActivity(myIntent);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
