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
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.grizzly.keepup.mainFragments.Map.MapFragment;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeedFragment;
import com.grizzly.keepup.mainFragments.ProfileFragment;

/**
 * Created by kubek on 1/31/18.
 */

/**
 * Main activity of application.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Location";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        //ask to turn on location
        enableGPS();
        setupTabLayout();
    }

    /**
     * Setup tab layout.
     */
    private void setupTabLayout(){
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Setup tabs names and fragments which represent them.
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), "RUN");
        adapter.addFragment(new NewsFeedFragment(), "FEED");
        adapter.addFragment(new ProfileFragment(), "PROFILE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }

    /**
     * Ask to turn on Location Services if turned off.
     */
    private void enableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            Toast.makeText(MainActivity.this, "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Turn on location");
            alertDialog.setMessage("In order to use this app properly you need to turn on Location");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "TURN ON",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }
}
