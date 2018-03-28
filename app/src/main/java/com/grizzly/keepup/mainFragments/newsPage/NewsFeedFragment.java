/*
* Copyright 2018 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.grizzly.keepup.mainFragments.newsPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.following.FollowedUsersList;
import com.grizzly.keepup.following.ProfileActivity;
import com.grizzly.keepup.R;
import com.grizzly.keepup.search.SearchActivity;
import com.grizzly.keepup.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kubek on 1/31/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeedFragment extends Fragment {

    private FirebaseAuth mAuth;

    private RecyclerView mNewsList;
    private DatabaseReference mDatabase;
    private DatabaseReference mRefProfileImage;
    private DatabaseReference mRefProfileName;
    private LinearLayoutManager mLayoutManager;
    private ImageButton mButtonFollowed;
    private ImageButton mButtonSearch;
    private FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder> firebaseRecyclerAdapter;


    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        mAuth = FirebaseAuth.getInstance();

        mButtonFollowed = view.findViewById(R.id.followed_button_news_feed);
        mButtonSearch = view.findViewById(R.id.search_button_news_feed);


        mNewsList = view.findViewById(R.id.news_feed_list);
        mNewsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mNewsList.setLayoutManager(mLayoutManager);

        mRefProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        mRefProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");


        //list of following people

        final List<String> commentKeys = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getUid().toString()).child("followingUsers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            commentKeys.add(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        Log.e("COUNT", String.valueOf(commentKeys.size()));

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getUid().toString()).child("runs");


        buttonOpenFollowed();
        buttonOpenSearch();
        loadNews();

        return view;
    }


    /**
     * Load news feeds form database.
     * Populate viewHolder
     */
    private void loadNews() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder>
                (NewsFeed.class, R.layout.news_row, NewsViewHolder.class,
                        mDatabase.orderByChild("reversedTimestamp")) {
            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, final NewsFeed model, int position) {
                viewHolder.setRunDate(model.getRunDate());
                viewHolder.setImage(getContext(), model.getSpecificRunImage());
                viewHolder.setRunStats(model.getTime(), model.getDistance());
                viewHolder.setProfileImage(getContext(), mRefProfileImage);
                viewHolder.setProfileName(mRefProfileName);

                openDialogActivityRun(viewHolder, model);
                openDialogActivityUser(viewHolder, model.getUserId());
            }
        };
        mNewsList.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * Open dialog window in RecyclerView.
     */
    private void openDialogActivityRun(final NewsViewHolder viewHolder, final NewsFeed model) {
        viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("RUN_DATE", model.getRunDate());
                intent.putExtra("RUN_STATS_IMAGE", model.getSpecificRunImage());
                intent.putExtra("PUSH_ID", model.getPushID());

                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    /**
     * Open dialog window in RecyclerView.
     */
    private void openDialogActivityUser(final NewsViewHolder viewHolder, final String userId) {
        viewHolder.getProfileImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("USER", userId);

                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    /**
     * Set listener on followed friends.
     */
    private void buttonOpenFollowed() {
        mButtonFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFollowed();
            }
        });
    }


    /**
     * Set listener on chat.
     */
    private void buttonOpenChat() {
        mButtonFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });
    }



    /**
     * Open chat activity.
     */
    private void openChat() {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        startActivity(chatIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Open followed activity.
     */
    private void openFollowed() {
        Intent followedIntent = new Intent(getActivity(), FollowedUsersList.class);
        startActivity(followedIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void buttonOpenSearch() {
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

}