package smart.tuke.sk.keepup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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


    /*@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);

        List<RunsHistory> data = fill_with_data();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(data, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_history, container, false);


        List<RunsHistory> data = fill_with_data();


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(data, getActivity().getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return rootView;
    }







    @Override
    public void onStart() {
        super.onStart();

    }





    public List<RunsHistory> fill_with_data() {

        List<RunsHistory> data = new ArrayList<>();

        data.add(new RunsHistory("Batman vs Superman", "Following the destruction of Metropolis, Batman embarks on a personal vendetta against Superman ", 0));
        data.add(new RunsHistory("X-Men: Apocalypse", "X-Men: Apocalypse is an upcoming American superhero film based on the X-Men characters that appear in Marvel Comics ", 0));
        data.add(new RunsHistory("Captain America: Civil War", "A feud between Captain America and Iron Man leaves the Avengers in turmoil.  ", 0));
        data.add(new RunsHistory("Kung Fu Panda 3", "After reuniting with his long-lost father, Po  must train a village of pandas", 0));
        data.add(new RunsHistory("Warcraft", "Fleeing their dying home to colonize another, fearsome orc warriors invade the peaceful realm of Azeroth. ", 0));
        data.add(new RunsHistory("Alice in Wonderland", "Alice in Wonderland: Through the Looking Glass ", 0));

        return data;
    }
}
