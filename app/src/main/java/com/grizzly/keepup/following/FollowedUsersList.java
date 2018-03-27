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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.grizzly.keepup.R;
import com.grizzly.keepup.search.User;
import com.grizzly.keepup.search.UserViewHolder;

/**
 * Created by kubek on 3/27/18.
 */
public class FollowedUsersList extends AppCompatActivity {

    private RecyclerView mFollowedList;
    private TextView noFollowed;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String userIdMe;
    private String userIdOpened;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users_list);

        mAuth = FirebaseAuth.getInstance();
        userIdMe = mAuth.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users").child(userIdMe).child("followingUsers");
        noFollowed = findViewById(R.id.no_followed_search);



        mFollowedList = findViewById(R.id.followed_result_list);
        mFollowedList.setHasFixedSize(true);
        mFollowedList.setLayoutManager(new LinearLayoutManager(this));

        showFollowedList();
    }

    private void showFollowedList(){
        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_row,
                UserViewHolder.class,
                mUserDatabase
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                noFollowed.setVisibility(View.GONE);
                viewHolder.setDetails(getApplicationContext() ,model.getName(), model.getImage(), model.getMail());

                openDialogActivityUser(viewHolder, model.getUserId());

            }
        };
        mFollowedList.setAdapter(firebaseRecyclerAdapter);
        if(firebaseRecyclerAdapter.getItemCount()==0){
            noFollowed.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Open dialog window in RecyclerView.
     */
    private void openDialogActivityUser(final UserViewHolder viewHolder, final String userId){
        viewHolder.getUserImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowedUsersList.this, ProfileActivity.class);
                intent.putExtra("USER", userId);
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
