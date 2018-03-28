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
import android.view.WindowManager;
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
import com.grizzly.keepup.mainFragments.newsPage.NewsDetailActivity;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeed;
import com.grizzly.keepup.mainFragments.newsPage.NewsViewHolder;
import com.grizzly.keepup.search.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by kubek on 2/14/18.
 */
public class ProfileActivity extends AppCompatActivity {


    private RecyclerView mNewsList;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<NewsFeed, NewsViewHolder> firebaseRecyclerAdapter;

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileMail;
    private Button mFollowButton;

    private FirebaseAuth mAuth;
    private String userId;
    private String userIdMe;
    private boolean following;
    private DatabaseReference mDatabase;

    private String image, name, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Change becouse circleview in xml is crashing often.
        setContentView(R.layout.activity_profile_runs);
        mAuth = FirebaseAuth.getInstance();

        Intent i = this.getIntent();
        userId = i.getExtras().getString("USER");
        userIdMe = mAuth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userIdMe);

        profileImage = findViewById(R.id.profile_activity_image);
        profileName = findViewById(R.id.profile_activity_name);
        profileMail = findViewById(R.id.profile_activity_mail);
        mFollowButton = findViewById(R.id.follow_button);

        if(Objects.equals(userId, mAuth.getUid())){
            mFollowButton.setVisibility(View.GONE);
        }

        mNewsList = findViewById(R.id.profile_activity_list);
        mNewsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mNewsList.setLayoutManager(mLayoutManager);

        setProfileImage(FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("image"));
        setProfileName(FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name"));
        setProfileMail(FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("mail"));

        followingButtonState();
        followButton();
        loadNews();

    }

    private void followingButtonState() {
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid())
                .child("followingUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    following = true;
                    mFollowButton.setText("FOLLOWING");
                    Drawable img = getResources().getDrawable(R.drawable.ic_check_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                } else {
                    following = false;
                    mFollowButton.setText("FOLLOW");
                    Drawable img = getResources().getDrawable(R.drawable.ic_add_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void followButton() {
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!following) {
                    mFollowButton.setText("FOLLOWING");
                    Drawable img = getResources().getDrawable(R.drawable.ic_check_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                    following = true;
                    addDataToUser();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).
                            child("followingUsers").child(userId).removeValue();
                    mFollowButton.setText("FOLLOW");
                    Drawable img = getResources().getDrawable(R.drawable.ic_add_black_24dp);
                    img.setBounds(0, 0, 80, 80);
                    mFollowButton.setCompoundDrawables(img, null, null, null);
                    following = false;
                }
            }
        });
    }


    private void setProfileImage(DatabaseReference reference) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue(String.class);
                String uri = dataSnapshot.getValue(String.class);
                Picasso.with(getApplicationContext()).load(uri).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setProfileName(DatabaseReference reference) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
                String name = dataSnapshot.getValue(String.class);
                profileName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setProfileMail(DatabaseReference reference) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mail = dataSnapshot.getValue(String.class);
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
                        FirebaseDatabase.getInstance().getReference()
                                .child("users").child(userId).child("runs").orderByChild("reversed_timestamp")) {

            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, final NewsFeed model, int position) {
                viewHolder.setRunDate(model.getRunDate());
                viewHolder.setImage(getApplicationContext(), model.getSpecificRunImage());
                viewHolder.setRunStats(model.getTime(), model.getDistance());
                viewHolder.setProfileImage(getApplicationContext(),
                        FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("image"));
                viewHolder.setProfileName(FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name"));

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
                intent.putExtra("PUSH_ID", model.getPushID());

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


    private void addDataToUser() {
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("followingUsers").child(userId)
                .setValue(new User(image, name, mail, userId));
    }
}
