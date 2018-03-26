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

package com.grizzly.keepup;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.login.LoginActivity;
import com.grizzly.keepup.login.setup.SetupActivity;

/**
 * Created by kubek on 1/30/18.
 */

/**
 * Splash activity in app start.
 */
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRelativeLayout = findViewById(R.id.splash_activity);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        userLogged();
    }

    /**
     * Check if user loggedIn.
     */
    private void userLogged(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            checkUserExist();
        }else{
            goToLogin();
        }
    }

    /**
     * Check if user have setUp profile
     */
    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            final String userId = mAuth.getCurrentUser().getUid();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(userId)) {
                        updateUI();
                    } else {
                        goToSetupActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }




    /**
     * This will switch activities. Used only after success login.
     */
    private void updateUI() {
        Intent accountIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(accountIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
        finish();
    }

    /**
     * Change activity to loginActivity.
     */
    private void goToLogin() {
        Intent accountIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(accountIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
        finish();
    }

    /**
     * Change activity to setupActivity.
     */
    private void goToSetupActivity() {
        Intent accountIntent = new Intent(SplashActivity.this, SetupActivity.class);
        startActivity(accountIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
        finish();
    }
}
