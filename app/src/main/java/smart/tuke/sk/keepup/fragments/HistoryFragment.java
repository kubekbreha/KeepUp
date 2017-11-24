package smart.tuke.sk.keepup.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import smart.tuke.sk.keepup.R;
import smart.tuke.sk.keepup.history.RecyclerViewAdapter;
import smart.tuke.sk.keepup.history.RunsHistory;


public class HistoryFragment extends BaseFragment {

    public static HistoryFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_history, container, false);


        List<RunsHistory> data = fill_with_data();


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(data, getActivity().getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    public List<RunsHistory> fill_with_data() {

        List<RunsHistory> data = new ArrayList<>();
        Random randomGenerator = new Random();


        for (int i = 0; i < 10; i++) {
            data.add(new RunsHistory("Run " + i, "Today i had run this amount of km: "
                    + randomGenerator.nextInt(20), R.drawable.ic_launcher_background));
        }

        return data;
    }

}
