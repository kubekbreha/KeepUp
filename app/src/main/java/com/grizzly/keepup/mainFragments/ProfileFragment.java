package com.grizzly.keepup.mainFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.grizzly.keepup.R;
import com.grizzly.keepup.chat.ChatActivity;
import com.grizzly.keepup.login.LoginActivity;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button button1;
    private Button button2;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userName;
        TextView userEmail;
        ImageView userPhoto;

        userPhoto = view.findViewById(R.id.profile_fragment_image);
        userName = view.findViewById(R.id.profile_fragment_name);
        userEmail = view.findViewById(R.id.profile_fragment_mail);

        //Picasso.with(getContext()).load(model.getSpecific_run_image()).into(imageUri);
        Picasso.with(getContext()).load("http://i.imgur.com/DvpvklR.png").into(userPhoto);

        userName.setText(mAuth.getCurrentUser().toString());
        userEmail.setText(mAuth.getCurrentUser().getEmail());



        Button buttonSignOut = (Button) view.findViewById(R.id.button1);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });


        Button buttonOut = (Button) view.findViewById(R.id.button2);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
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

}
