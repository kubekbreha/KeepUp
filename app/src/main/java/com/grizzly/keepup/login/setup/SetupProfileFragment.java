package com.grizzly.keepup.login.setup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetupProfileFragment extends Fragment {


    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private CircleImageView mSetupImageButton;
    private EditText mNameField;
    private Button mSubmitButton;
    private ViewPager pager;
    private boolean photoLoaded = true;


    public SetupProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_setup_profile, container, false);
        mSubmitButton = view.findViewById(R.id.profile_setup_button);
        mNameField = view.findViewById(R.id.profile_setup_name);
        mSetupImageButton = view.findViewById(R.id.profile_setup_image);

        pager = getActivity().findViewById(R.id.setup_view_pager);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");

        if (mDatabase.child(mAuth.getUid()).child("image") != null) {
            photoLoaded = false;
            mDatabase.child(mAuth.getUid()).child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(String.class);
                    mImageUri = Uri.parse(dataSnapshot.getValue(String.class));
                    Picasso.with(getApplicationContext()).load(uri).into(mSetupImageButton);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        setUpName();
        setUpImage();

        return view;
    }

    private void setUpName() {
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }

    private void setUpImage() {
        mSetupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
    }

    private void startSetupAccount() {
        final String name = mNameField.getText().toString().trim();
        final String userId = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(name) && mImageUri != null && photoLoaded) {

            mProgress.setMessage("Setting up");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabase.child(userId).child("name").setValue(name);
                    mDatabase.child(userId).child("image").setValue(downloadUri);
                    mDatabase.child(userId).child("mail").setValue(mAuth.getCurrentUser().getEmail());
                    mDatabase.child(userId).child("userId").setValue(mAuth.getCurrentUser().getUid());

                    mProgress.dismiss();
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
            });
        } else if (!TextUtils.isEmpty(name) && mImageUri != null && !photoLoaded) {
            mProgress.setMessage("Setting up");
            mProgress.show();

            mDatabase.child(userId).child("name").setValue(name);
            mDatabase.child(userId).child("mail").setValue(mAuth.getCurrentUser().getEmail());
            mDatabase.child(userId).child("userId").setValue(mAuth.getCurrentUser().getUid());
            mProgress.dismiss();
            pager.setCurrentItem(pager.getCurrentItem() + 1);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImageButton.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropper error", Toast.LENGTH_SHORT).show();
                Exception error = result.getError();
            }
        } else {
            Toast.makeText(getActivity(), "Error croping image", Toast.LENGTH_LONG);
        }
    }

}






