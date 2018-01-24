package com.grizzly.chat.mainFragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grizzly.chat.MainActivity;
import com.grizzly.chat.R;

import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeedFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;
    private Uri mRunImageUri;
    private ProgressDialog mProgress;

    public NewsFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProgress = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("runs");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("run_images");


        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Toast.makeText(getContext(),"new run",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).takeSnapShootActivity();

                //uploadRun();
            }
        });
        return view;
    }


    private void uploadRun(){
        final String user_id = mAuth.getCurrentUser().getUid();

        if (mRunImageUri != null) {
            mProgress.setMessage("Uploading");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mRunImageUri.getLastPathSegment());

            filepath.putFile(mRunImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    mDatabase.child(user_id).child("specific_run_image").setValue(downloadUri);

                    mProgress.dismiss();
                }
            });
        }

    }





}
