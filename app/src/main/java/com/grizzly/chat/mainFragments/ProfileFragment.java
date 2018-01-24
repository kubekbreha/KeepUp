package com.grizzly.chat.mainFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.grizzly.chat.MainActivity;
import com.grizzly.chat.R;
import com.grizzly.chat.chat.ChatActivity;
import com.grizzly.chat.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        Button button = (Button) view.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });


        Button buttonOut = (Button) view.findViewById(R.id.button2);
        buttonOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
