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

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kubek on 2/8/2018.
 */

/**
 * Detail activity of newsFeed.
 */
public class NewsDetailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView mRunImage;
    private TextView mPostDate;
    private DatabaseReference mRefProfileImage;
    private DatabaseReference mRefProfileName;
    private DatabaseReference mRefProfileMinutes;
    private DatabaseReference mRefProfileExpandable;
    private DatabaseReference mRefProfileDate;

    private TextView expandedTitle;
    private TextView expandedText;
    private CardView expandCard;

    private ValueLineChart mCubicValueLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        //GET INTENT
        Intent i = this.getIntent();

        //RECEIVE DATA
        String dateRun = i.getExtras().getString("RUN_DATE");
        String image = i.getExtras().getString("RUN_STATS_IMAGE");
        String pushID = i.getExtras().getString("PUSH_ID");
        String userID = i.getExtras().getString("USER_ID");
        System.out.println(pushID);


        Toolbar toolbar = findViewById(R.id.news_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mRefProfileImage = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("image");
        mRefProfileName = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("name");

        //set full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mPostDate = findViewById(R.id.news_detail_post_date);
        mRunImage = findViewById(R.id.image_news_detail);


        //BIND DATA
        setProfileImage(getApplicationContext(), mRefProfileImage);
        setProfileName(mRefProfileName);
        mPostDate.setText(dateRun);

        mRunImage.setTransitionName("thumbnailTransition");
        Picasso.with(getApplicationContext()).load(image).into(mRunImage);

        expandedTitle = findViewById(R.id.detail_expanded_title);
        expandedText = findViewById(R.id.detail_expanded_text);
        expandCard = findViewById(R.id.detail_expand_card_view);
        expandCardListener();

        mCubicValueLineChart = findViewById(R.id.cubiclinechart);

        mRefProfileMinutes = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("runs").child(pushID).child("minuteTimes");
        setLineChart(mRefProfileMinutes);

        mRefProfileExpandable = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("runs").child(pushID);
        setExpandableTitle(mRefProfileExpandable);
        setExpandableText(mRefProfileExpandable);

        mRefProfileDate = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("runs").child(pushID).child("runDate");
        setRunDate(mRefProfileDate);
    }

    /**
     * Setting up line chart graph.
     */
    private void setLineChart(DatabaseReference database) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>() {
                };
                ArrayList<Long> distancesList = dataSnapshot.getValue(t);

                if(distancesList != null) {
                    ValueLineSeries series = new ValueLineSeries();
                    series.setColor(0xFF56B7F1);

                    int i = 0;
                    for (Long number : distancesList) {
                        series.addPoint(new ValueLinePoint(i + "km", number));
                        i++;
                    }

                    mCubicValueLineChart.addSeries(series);
                    mCubicValueLineChart.startAnimation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Expand cardview.
     */
    private void expandCardListener() {
        expandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedText.getVisibility() == View.GONE) {
                    expandedText.setVisibility(View.VISIBLE);
                } else {
                    expandedText.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /*Intent intent = new Intent(NewsDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setRunDate(DatabaseReference reference) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long date = dataSnapshot.getValue(Long.class);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mPostDate.setText(mDay+"."+mMonth+"."+mYear);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set profile image in detail activity.
     */
    public void setProfileImage(final Context context, DatabaseReference database) {
        final ImageView profileImageView = findViewById(R.id.news_detail_profile_image);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
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


    /**
     * Set profile name in detail activity.
     */
    public void setProfileName(DatabaseReference database) {
        final TextView profileName = findViewById(R.id.news_detail_profile_name);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                profileName.setText(name + " was out running.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Set title in expandable card.
     *
     * @param database reference.
     */
    private void setExpandableTitle(DatabaseReference database){
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String distance = dataSnapshot.child("distance").getValue(String.class);
                Long time = dataSnapshot.child("time").getValue(Long.class);
                expandedTitle.setText("I runned "+distance+ " in " + time);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set text in expandable card.
     *
     * @param database reference.
     */
    private void setExpandableText(DatabaseReference database){
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String distance = dataSnapshot.child("distance").getValue(String.class);
                Long time = dataSnapshot.child("time").getValue(Long.class);
                expandedText.setText("I burned " + 10 + " calories. \n" + "My average time for" +
                        " one kilometer is " + 10 + "\nBest time for one kilometer is " + 10 );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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