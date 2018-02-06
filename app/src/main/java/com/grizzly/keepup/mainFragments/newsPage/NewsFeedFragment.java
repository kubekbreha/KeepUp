package com.grizzly.keepup.mainFragments.newsPage;


import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseListAdapter<NewsFeed> adapter;
    private View mView;
    private TextView userName;
    private TextView userRunStats;
    private ImageView userPhoto;
    private TextView runTime;
    private ImageView imageUri;
    private List<NewsFeed> allNews;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mView = view;
        mAuth = FirebaseAuth.getInstance();

        mSwipeRefreshLayout = view.findViewById(R.id.news_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }


    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayNewsFeed();
    }


    private void displayNewsFeed() {
        ListView newsList = mView.findViewById(R.id.news_feed_list);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs");
        adapter = new FirebaseListAdapter<NewsFeed>(getActivity(), NewsFeed.class, R.layout.news_row,
                ref) {
            @Override
            protected void populateView(View v, NewsFeed model, int position) {
                //get reference to to the value og list_item.xml


                imageUri = v.findViewById(R.id.news_post_image);
                runTime = v.findViewById(R.id.news_post_date);
                userPhoto = v.findViewById(R.id.news_profile_image);
                userName = v.findViewById(R.id.news_profile_name);
                userRunStats = v.findViewById(R.id.news_post_run_time_and_distance);

                DatabaseReference refImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
                refImage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uri = dataSnapshot.getValue(String.class);
                        Picasso.with(getContext()).load(uri).into(userPhoto);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                DatabaseReference refName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");
                refName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        userName.setText(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                userRunStats.setText( "I runned " + model.getDistance() + " in " + getTimeFromMilis(model.getTime()));
                Picasso.with(getContext()).load(model.getSpecific_run_image()).resize(100, 70).into(imageUri);
                runTime.setText(DateFormat.format("HH:mm", model.getRun_date()));
            }
        };
        newsList.setAdapter(adapter);
    }


    private String getTimeFromMilis(int milis){
        long second = (milis / 1000) % 60;
        long minute = (milis / (1000 * 60)) % 60;
        long hour   = (milis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d:%d", hour, minute, second, milis);
    }

}