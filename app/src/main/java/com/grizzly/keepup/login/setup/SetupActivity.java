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

package com.grizzly.keepup.login.setup;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.grizzly.keepup.R;

/**
 * Created by kubek on 1/21/18.
 */

/**
 * Activity where profile picture and name is set.
 */
public class SetupActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private AnimationDrawable mAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        mRelativeLayout = findViewById(R.id.setup_gradient);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        //set full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mViewPager = findViewById(R.id.setup_view_pager);
        setupViewPager(mViewPager);
    }

    /**
     * Setup tabs names and fragments which represent them.
     */
    private void setupViewPager(ViewPager viewPager) {
        SliderAdapter adapter = new SliderAdapter(getSupportFragmentManager());

        adapter.addFragment(new SetupProfileFragment(), "ProfileSetup");
        adapter.addFragment(new SetupRunnerFragment(), "RunnerSetup");
        viewPager.setAdapter(adapter);
    }

}