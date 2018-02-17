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

package com.grizzly.keepup.following;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.R;
import com.grizzly.keepup.login.setup.SetupActivity;
import com.grizzly.keepup.mainFragments.newsPage.NewsDetailActivity;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeed;
import com.grizzly.keepup.mainFragments.newsPage.NewsViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kubek on 2/14/18.
 */
public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private RecyclerView mNewsList;
    private DatabaseReference mDatabase;
    private DatabaseReference mRefProfileImage;
    private DatabaseReference mRefProfileName;
    private DatabaseReference mRefProfileMail;
    private DatabaseReference mRefPrfileUser;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder> firebaseRecyclerAdapter;

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileMail;
    private Button mFollowButton;
    private ArrayList<String> followingUsers;

    private String userId;
    private boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Change becouse circleview in xml is crashing often.
        setContentView(R.layout.activity_profile_runs);
        mAuth = FirebaseAuth.getInstance();

        Intent i = this.getIntent();
        userId = i.getExtras().getString("USER");

        profileImage = findViewById(R.id.profile_activity_image);
        profileName = findViewById(R.id.profile_activity_name);
        profileMail = findViewById(R.id.profile_activity_mail);
        mFollowButton = findViewById(R.id.follow_button);

        mNewsList = findViewById(R.id.profile_activity_list);
        mNewsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mNewsList.setLayoutManager(mLayoutManager);

        mRefProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("image");
        mRefProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name");
        mRefProfileMail = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("mail");
        mRefPrfileUser = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("runs");

        followingUsers = new ArrayList<>();

        setProfileImage(mRefProfileImage);
        setProfileName(mRefProfileName);
        setProfileMail(mRefProfileMail);


        setUpArray();

        followButton();
        loadNews();

    }

    private void followingButtonState() {
        mRefPrfileUser.child(mAuth.getUid()).child("followingUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> yourStringArray = dataSnapshot.getValue(t);
                for(String val : yourStringArray){
                    if(val.equals(userId)){
                        mFollowButton.setText("FOLLOWING");
                        Drawable img = getResources().getDrawable(R.drawable.ic_check_black_24dp);
                        img.setBounds(0, 0, 80, 80);
                        mFollowButton.setCompoundDrawables(img, null, null, null);
                        following = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpArray() {
        mRefPrfileUser.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("followingUsers")) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    followingUsers = dataSnapshot.child("followingUsers").getValue(t);
                    followingButtonState();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void followButton() {
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!following) {
                    followingUsers.add(userId);
                    mFollowButton.setText("FOLLOWING");
                    Drawable img = getResources().getDrawable(R.drawable.ic_check_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                    following = true;
                } else {
                    followingUsers.remove(userId);
                    mFollowButton.setText("FOLLOW");
                    Drawable img = getResources().getDrawable(R.drawable.ic_add_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                    following = false;
                }
                mRefPrfileUser.child(mAuth.getUid()).child("followingUsers").setValue(followingUsers);
            }
        });
    }


    private void setProfileImage(DatabaseReference reference) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.getValue(String.class);
                Picasso.with(getApplicationContext()).load(uri).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setProfileName(DatabaseReference reference) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                profileName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setProfileMail(DatabaseReference reference) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mail = dataSnapshot.getValue(String.class);
                profileMail.setText(mail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    private void openDialogActivityRun(final NewsViewHolder viewHolder, final NewsFeed model) {
        viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("RUN_DATE", model.getRunDate());
                intent.putExtra("RUN_STATS_IMAGE", model.getSpecificRunImage());

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
