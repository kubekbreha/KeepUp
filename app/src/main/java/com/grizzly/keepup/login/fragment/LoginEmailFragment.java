package com.grizzly.keepup.login.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginEmailFragment extends Fragment {

    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgres;
    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;


    public LoginEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        View v = inflater.inflate(R.layout.fragment_login_email, container, false);

        mRelativeLayout = v.findViewById(R.id.email_login_gradient);

        mLoginButton = v.findViewById(R.id.login_email_button);
        mLoginEmailField = v.findViewById(R.id.login_email);
        mLoginPasswordField = v.findViewById(R.id.login_password);

        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setEnterFadeDuration(4500);
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mProgres = new ProgressDialog(getContext());
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }

        });
        return v;
    }

    private void checkLogin() {
        String email = mLoginEmailField.getText().toString().trim();
        String password = mLoginPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgres.setMessage("Checking Login");
            mProgres.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgres.dismiss();
                        checkUserExist();
                    } else {
                        mProgres.dismiss();
                        Toast.makeText(getActivity(), "Failed to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    updateUI();
                } else {
                    Toast.makeText(getActivity(), "You need to set up your acount ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    private void updateUI() {
        Intent accountIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(accountIntent);
        getActivity().finish();
    }

}