package smart.tuke.sk.keepup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smart.tuke.sk.keepup.R;

public class FriendsFragment extends BaseFragment {


    public static FriendsFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

    }
}
