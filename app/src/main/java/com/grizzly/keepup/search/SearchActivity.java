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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.grizzly.keepup.R;
import com.grizzly.keepup.following.ProfileActivity;

/**
 * Created by kubek on 2/14/18.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mConfirmButton;
    private RecyclerView mResultList;
    private TextView noResult;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");

        mSearchField = findViewById(R.id.search_bar);
        mConfirmButton = findViewById(R.id.search_confirm_button);
        noResult = findViewById(R.id.no_result_search);

        mResultList = findViewById(R.id.search_result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);
            }
        });
    }

    private void firebaseUserSearch(String searchText){
        final Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_row,
                UserViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                noResult.setVisibility(View.GONE);
                viewHolder.setDetails(getApplicationContext() ,model.getName(), model.getImage(), model.getMail());

                openDialogActivityUser(viewHolder, model.getUserId());
            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);
        if(firebaseRecyclerAdapter.getItemCount()==0){
            noResult.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Open dialog window in RecyclerView.
     */
    private void openDialogActivityUser(final UserViewHolder viewHolder, final String userId){
        viewHolder.getUserImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
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
