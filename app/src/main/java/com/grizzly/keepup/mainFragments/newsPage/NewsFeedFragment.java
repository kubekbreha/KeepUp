package com.grizzly.keepup.mainFragments.newsPage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grizzly.keepup.R;

/**
 * Created by kubek on 1/31/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeedFragment extends Fragment {

    private FirebaseAuth mAuth;

    private RecyclerView newsList;
    private DatabaseReference mDatabase;
    private DatabaseReference refProfileImage;
    private DatabaseReference refProfileName;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager layoutManager;

    private static int TOTAL_ITEMS_TO_LOAD = 2;
    private int mCurrentPage = 1;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        mAuth = FirebaseAuth.getInstance();

        newsList = view.findViewById(R.id.news_feed_list);
        newsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        newsList.setLayoutManager(layoutManager);

        mRefreshLayout = view.findViewById(R.id.news_swipe_load);

        refProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        refProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        loadNews();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                loadNews();
            }
        });
    }

    private void loadNews() {
        mDatabase = (DatabaseReference) FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getUid().toString()).child("runs");

        FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder>
                (NewsFeed.class, R.layout.news_row, NewsViewHolder.class,
                        mDatabase.limitToFirst(mCurrentPage * TOTAL_ITEMS_TO_LOAD).orderByChild("reversed_timestamp")) {
            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, final NewsFeed model, int position) {
                viewHolder.setRunDate(model.getmRunDate());
                viewHolder.setImage(getContext(), model.getmSpecificRunImage());
                viewHolder.setRunStats(model.getTime(), model.getDistance());
                viewHolder.setProfileImage(getContext(), refProfileImage);
                viewHolder.setProfileName(refProfileName);


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                        intent.putExtra("RUN_DATE", model.getmRunDate());
                        intent.putExtra("RUN_STATS_IMAGE", model.getmSpecificRunImage());

                        startActivity(intent);
                    }
                });

            }
        };
        mRefreshLayout.setRefreshing(false);
        newsList.setAdapter(firebaseRecyclerAdapter);
    }
}