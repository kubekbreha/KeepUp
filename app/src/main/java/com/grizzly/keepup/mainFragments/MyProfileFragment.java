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

package com.grizzly.keepup.mainFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.R;
import com.grizzly.keepup.login.LoginActivity;
import com.grizzly.keepup.login.setup.SetupActivity;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.Date;
import java.util.List;

/**
 * Created by kubek on 1/31/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {

    private final static String TAG = "Profile fragment";

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPhoto;
    private ImageButton mButtonSignOut;
    private View mView;

    private TextView mExpandedTitle;
    private TextView mExpandedText;
    private CardView mExpandCard;
    private ValueLineChart mCubicValueLineChart;

    private CompactCalendarView compactCalendarView;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        initializeGoogle();
        super.onStart();
    }

    /**
     * Initialize google API.
     */
    private void initializeGoogle(){
        GoogleSignInOptions googleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOpt)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserPhoto = mView.findViewById(R.id.profile_fragment_image);
        mUserName = mView.findViewById(R.id.profile_fragment_name);
        mUserEmail = mView.findViewById(R.id.profile_fragment_mail);
        mUserEmail.setText(mAuth.getCurrentUser().getEmail());
        mButtonSignOut = mView.findViewById(R.id.button1);

        DatabaseReference refImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");
        getImageRef(refImage);
        getNameRef(refName);

        buttonSignOut();
        buttonSetUpProfile();

        mExpandedTitle = mView.findViewById(R.id.profile_expanded_title);
        mExpandedText = mView.findViewById(R.id.profile_expanded_text);
        mExpandCard = mView.findViewById(R.id.profile_expand_card_view);
        expandCardListener();

        compactCalendarView = mView.findViewById(R.id.compactcalendar_view);

        setCalendar();
        calendarListener();

        mCubicValueLineChart = mView.findViewById(R.id.cubiclinechart);
        setLineChart();

        return mView;
    }

    /**
     * Setting up line chart graph.
     */
    private void setLineChart(){
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("Jan", 2.4f));
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }


    /**
     * Handle clicks on specific calendar days.
     */
    private void calendarListener(){
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Toast.makeText(getContext(), "Day was clicked: " + dateClicked + " with events " + events, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Toast.makeText(getContext(), "Month was scrolled to: " + firstDayOfNewMonth, Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * Setting up calendar.
     */
    private void setCalendar(){
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getUid().toString()).child("runs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Long l = snapshot.child("runDate").getValue(Long.class);
                            Event ev2 = new Event(Color.BLACK, l , "Can send something.");
                            compactCalendarView.addEvent(ev2);
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
    private void expandCardListener(){
        mExpandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExpandedText.getVisibility() == View.GONE) {
                    mExpandedText.setVisibility(View.VISIBLE);
                } else {
                    mExpandedText.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Set listener on profile name.
     */
    private void getNameRef(DatabaseReference reference){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                mUserName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set listener on profile image.
     */
    private void getImageRef(DatabaseReference reference){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.getValue(String.class);
                Picasso.with(getContext()).load(uri).into(mUserPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set listener on signOut button.
     */
    private void buttonSignOut(){
        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                revokeAccess();
            }
        });
    }

    /**
     * Set listener on profile setUp.
     */
    private void buttonSetUpProfile(){
        mUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setupIntent = new Intent(getActivity(), SetupActivity.class);
                startActivity(setupIntent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    /**
     * Send user form MainActivity to MainActivity.
     */
    private void updateUI() {
        Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_SHORT).show();
        Intent accountIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(accountIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getActivity().finish();
    }


    /**
     * Log out googleUser.
     */
    private void revokeAccess() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI();
                    }
                });
    }
}
