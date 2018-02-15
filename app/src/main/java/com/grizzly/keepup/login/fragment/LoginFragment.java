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

package com.grizzly.keepup.login.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grizzly.keepup.MainActivity;
import com.grizzly.keepup.R;
import com.grizzly.keepup.login.setup.SetupActivity;

/**
 * Created by kubek on 1/21/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LOGIN_FRAGMENT";

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 0;
    private static final int FACEBOOK_LOG_IN_REQUEST_CODE = 64206;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient; // for google sign in
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;
    private CallbackManager mFacebookCallbackManager; // for facebook log in

    //SignIn buttons
    private Button mFacebookButton;
    private Button mGoogleButton;
    private Button mRegisterButton;
    private Button mLoginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Create instance of fragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFBAuthentication();
        initFBGoogleSignIn();
        initFBAuthState();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        mRegisterButton = view.findViewById(R.id.register_button);
        mLoginButton = view.findViewById(R.id.login_button);
        mGoogleButton = view.findViewById(R.id.login_button_google);

        mFacebookButton = view.findViewById(R.id.login_button_facebook);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFBFacebookLogIn();
            }
        });

        //gradient
        mRelativeLayout = view.findViewById(R.id.layout_login);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setEnterFadeDuration(4500);
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogleSignIn();
            }
        });

        registerButtonListener();
        loginButtonListener();

        return view;
    }

    /**
     * Listener on mLoginButton.
     * On click send to LoginFragment.
     */
    private void loginButtonListener(){
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                LoginEmailFragment fragment = new LoginEmailFragment();
                fm.beginTransaction()
                        //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.login_to_be_replaced, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
    }

    /**
     * Listener on mRegisterButton.
     * On click send to RegisterFragment.
     */
    private void registerButtonListener(){
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                RegisterFragment fragment = new RegisterFragment();
                fm.beginTransaction()
                        //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.login_to_be_replaced, fragment)
                        .addToBackStack(null)   // add to manager " will remember this fragment  - for navigation purpose"
                        .commit();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FACEBOOK_LOG_IN_REQUEST_CODE) {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    //TODO: Is mAuth there enought ?
    /**
     * Initialize facebook variable.
     */
    private void initFBAuthentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Get state of facebook authentication.
     */
    private void initFBAuthState() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String message;
                if (firebaseUser != null) {
                    message = "onAuthStateChanged signed in : " + firebaseUser.getUid();
                } else {
                    message = "onAuthStateChanged signed out";
                }
                Log.d(TAG, message);
            }
        };
    }


    /**
     * Check if I'm already signed in.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            checkUserExist();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    /**
     * Init google logIn page.
     */
    private void initFBFacebookLogIn() {
        Toast.makeText(getActivity(), "facebook login", Toast.LENGTH_SHORT).show();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess");
                Toast.makeText(getActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "onCancel", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "onError", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error : " + error.getMessage());
            }

        });
    }

    //TODO : Fix facebook logIn!

    /**
     * This take care of facebook logIn.
     * If succeed send to checkUserExist() method.
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), user.toString(), Toast.LENGTH_SHORT).show();
                            mFacebookButton.setEnabled(true);
                            checkUserExist();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "WTF", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            mFacebookButton.setEnabled(true);
                        }
                    }
                });
    }

    /**
     * Intent google logIn.
     */
    private void signInWithGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }


    /**
     * Initialize variables need for google signIn.
     */
    private void initFBGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        Context context = getContext();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, connectionResult.getErrorMessage());
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    /**
     * This take care of google logIn.
     * If succeed send to checkUserExist() method.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUserExist();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Google authentication error " + e.getMessage());
            }
        });
    }

    /**
     * Checking if user already exist in database.
     * If not send user to SetupActivity.
     */
    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            final String userId = mAuth.getCurrentUser().getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(userId)) {
                        updateUI();
                    } else {
                        Toast.makeText(getActivity(), "You need to set up your acount ", Toast.LENGTH_SHORT).show();
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
     * Open MainActivity.
     * Called when user is authenticated.
     */
    private void updateUI() {
        Intent accountIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(accountIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getActivity().finish();
    }

    /**
     * Open SetupActivity.
     * Called when user is logged for first time and don't have set profile picture and name.
     */
    private void goToSetupActivity() {
        Intent accountIntent = new Intent(getActivity(), SetupActivity.class);
        startActivity(accountIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getActivity().finish();
    }

}
