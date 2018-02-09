package com.grizzly.keepup.mainFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.R;
import com.grizzly.keepup.chat.ChatActivity;
import com.grizzly.keepup.login.LoginActivity;
import com.grizzly.keepup.login.SetupActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by kubek on 1/31/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private final static String TAG = "Profile fragment";

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;


    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPhoto;
    private ImageButton mButtonSignOut;
    private ImageButton mButtonChat;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        GoogleSignInOptions googleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOpt)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserPhoto = view.findViewById(R.id.profile_fragment_image);
        mUserName = view.findViewById(R.id.profile_fragment_name);
        mUserEmail = view.findViewById(R.id.profile_fragment_mail);
        mUserEmail.setText(mAuth.getCurrentUser().getEmail());

        DatabaseReference refImage = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("image");
        refImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.getValue(String.class);
                Picasso.with(getContext()).load(uri).into(mUserPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference refName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("name");
        refName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                mUserName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mButtonSignOut = view.findViewById(R.id.button1);
        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                revokeAccess();
            }
        });

        mButtonChat = view.findViewById(R.id.button2);
        mButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });

        mUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setupIntent = new Intent(getActivity(), SetupActivity.class);
                startActivity(setupIntent);
            }
        });
        return view;
    }


    private void updateUI() {
        Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_SHORT).show();
        Intent accountIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(accountIntent);
        getActivity().finish();
    }


    private void openChat() {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        startActivity(chatIntent);
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI();
                    }
                });
    }
}
