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

/**
 * Created by kubek on 2/8/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView mRunImage;
    private TextView mPostDate;
    private DatabaseReference mRefProfileImage;
    private DatabaseReference mRefProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.news_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mRefProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        mRefProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");

        //set full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mPostDate = findViewById(R.id.news_detail_post_date);
        mRunImage = findViewById(R.id.image_news_detail);

        //GET INTENT
        Intent i = this.getIntent();

        //RECEIVE DATA
        String dateRun = i.getExtras().getString("RUN_DATE");
        String image = i.getExtras().getString("RUN_STATS_IMAGE");

        //BIND DATA
        setProfileImage(getApplicationContext(), mRefProfileImage);
        setProfileName(getApplicationContext(), mRefProfileName);
        mPostDate.setText(dateRun);
        Picasso.with(getApplicationContext()).load(image).into(mRunImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NewsDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setProfileImage(final Context context, DatabaseReference database){
        final ImageView profileImageView = findViewById(R.id.news_detail_profile_image);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.getValue(String.class);
                Picasso.with(context).load(uri).into(profileImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setProfileName(Context applicationContext, DatabaseReference database){
        final TextView profileName = findViewById(R.id.news_detail_profile_name);
        database.addValueEventListener(new ValueEventListener() {
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
}