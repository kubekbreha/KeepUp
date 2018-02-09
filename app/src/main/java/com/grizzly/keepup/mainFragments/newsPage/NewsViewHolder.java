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

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kubek on 2/7/2018.
 */

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private FirebaseAuth mAuth;

    public NewsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setRunDate(Long time) {
        TextView runDate = mView.findViewById(R.id.news_post_date);
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateText = dateFormat.format(date);
        runDate.setText(dateText);
    }

    public void setRunStats(int time, String distance) {
        TextView runStats = mView.findViewById(R.id.news_post_run_time_and_distance);
        runStats.setText("I runned " + distance + " in " + getTimeFromMilis(time));
    }

    public void setImage(Context context, String image) {
        ImageView imageView = mView.findViewById(R.id.news_post_image);
        Picasso.with(context).load(image).into(imageView);
    }

    public void setProfileImage(final Context context, DatabaseReference database) {
        final ImageView profileImageView = mView.findViewById(R.id.news_profile_image);
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


    public void setProfileName(DatabaseReference database) {
        final TextView profileName = mView.findViewById(R.id.news_profile_name);
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

    //----------------------------------------------------------------------------------------------
    private String getTimeFromMilis(int milis) {
        long second = (milis / 1000) % 60;
        long minute = (milis / (1000 * 60)) % 60;
        long hour = (milis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d:%d", hour, minute, second, milis);
    }


}
