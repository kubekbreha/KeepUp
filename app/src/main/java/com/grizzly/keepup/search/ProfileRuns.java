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

package com.grizzly.keepup.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grizzly.keepup.R;
import com.grizzly.keepup.mainFragments.newsPage.NewsDetailActivity;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeed;
import com.grizzly.keepup.mainFragments.newsPage.NewsViewHolder;

/**
 * Created by kubek on 2/14/18.
 */
public class ProfileRuns extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private RecyclerView mNewsList;
    private DatabaseReference mDatabase;
    private DatabaseReference mRefProfileImage;
    private DatabaseReference mRefProfileName;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_runs);
        mAuth = FirebaseAuth.getInstance();

        mNewsList = findViewById(R.id.profile_activity_list);
        mNewsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mNewsList.setLayoutManager(mLayoutManager);

        mRefProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        mRefProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getUid().toString()).child("runs");

        loadNews();
    }


    /**
     * Load news feeds form database.
     * Populate viewHolder
     */
    private void loadNews() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder>
                (NewsFeed.class, R.layout.news_row, NewsViewHolder.class,
                        mDatabase.orderByChild("reversed_timestamp")) {
            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, final NewsFeed model, int position) {
                viewHolder.setRunDate(model.getRunDate());
                viewHolder.setImage(getApplicationContext(), model.getSpecificRunImage());
                viewHolder.setRunStats(model.getTime(), model.getDistance());
                viewHolder.setProfileImage(getApplicationContext(), mRefProfileImage);
                viewHolder.setProfileName(mRefProfileName);

                openDialogActivityRun(viewHolder, model);
            }
        };
        mNewsList.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * Open dialog window in RecyclerView.
     */
    private void openDialogActivityRun(final NewsViewHolder viewHolder, final NewsFeed model){
        viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("RUN_DATE", model.getRunDate());
                intent.putExtra("RUN_STATS_IMAGE", model.getSpecificRunImage());

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
