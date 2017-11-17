package smart.tuke.sk.keepup.fragments;

import android.os.Bundle;
import android.view.View;


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
                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(RunFragment.newInstance(mInt+1));
                }
            }
        });
        mButton.setText(getClass().getSimpleName() + " " + mInt);
    }
}
