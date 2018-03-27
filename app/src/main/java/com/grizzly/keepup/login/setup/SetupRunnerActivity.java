package com.grizzly.keepup.login.setup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetupRunnerActivity extends AppCompatActivity {

    private ImageButton mFemaleButton;
    private ImageButton mMaleButton;
    private EditText mUserAge;
    private EditText mUserWeight;
    private Button mConfirmButton;
    private Button sublineMale;
    private Button sublineFemale;

    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private boolean gender;
    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;

    public SetupRunnerActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_runner);

        mRelativeLayout = findViewById(R.id.settings_runner);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        sublineFemale = findViewById(R.id.female_subline);
        sublineMale = findViewById(R.id.male_subline);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        mFemaleButton = findViewById(R.id.setup_female);
        mMaleButton = findViewById(R.id.setup_male);
        mUserAge = findViewById(R.id.runner_setup_age);
        mUserWeight = findViewById(R.id.runner_setup_weight);
        mConfirmButton = findViewById(R.id.runner_setup_button);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());

        pickGender();
        setUpName();
    }


    private void pickGender() {
        mFemaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sublineFemale.setVisibility(View.VISIBLE);
                sublineMale.setVisibility(View.GONE);
                gender = false;
            }
        });
        mMaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sublineFemale.setVisibility(View.GONE);
                sublineMale.setVisibility(View.VISIBLE);
                gender = true;
            }
        });
    }


    private void setUpName() {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }


    private void startSetupAccount() {
        final String age = mUserAge.getText().toString().trim();
        final String weight = mUserWeight.getText().toString().trim();

        if (!TextUtils.isEmpty(age) && !TextUtils.isEmpty(weight)) {
            mProgress.setMessage("Setting up");
            mProgress.show();

            mDatabase.child("personal").child("age").setValue(age);
            mDatabase.child("personal").child("weight").setValue(weight);
            mDatabase.child("personal").child("gender").setValue(gender);
        }
        mProgress.dismiss();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MainActivity.isActive()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent accountIntent = new Intent(this, MainActivity.class);
            startActivity(accountIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}
