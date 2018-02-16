package com.grizzly.keepup.login.setup;


import android.app.ProgressDialog;
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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("personal");

        pickGender();
        setUpName();
    }


    private void pickGender(){
        mFemaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = false;
            }
        });
        mMaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            mDatabase.push().setValue(age);
            mDatabase.push().setValue(weight);
            mDatabase.push().setValue(gender);
        }
    }

}
