package com.grizzly.keepup.login.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mRelativeLayout = (RelativeLayout) v.findViewById(R.id.register_gradient);
        mNameField = v.findViewById(R.id.edit_name);
        mEmailField = v.findViewById(R.id.edit_email);
        mPasswordField = v.findViewById(R.id.edit_password);
        mRegisterButton = v.findViewById(R.id.confirm_register_button);

        // Inflate the layout for this fragment
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setEnterFadeDuration(4500);
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
        return v;
    }


    private void doRegister() {
        final String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setMessage("Singin Up");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        mProgress.dismiss();
                        updateUI();
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(getActivity(), "Registration error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**
     * This will switch activities. Used only after success login.
     */
    private void updateUI() {
        Toast.makeText(getActivity(), "registered", Toast.LENGTH_SHORT).show();

        Intent accountIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(accountIntent);
        getActivity().finish();
    }

}