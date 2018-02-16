
package com.grizzly.keepup.login.setup;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.graphics.drawable.AnimationDrawable;
        import android.net.Uri;
        import android.os.Build;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.EditText;
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

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kubek on 1/21/18.
 */

/**
 * Activity where profile picture and name is set.
 */
public class SetupActivity extends AppCompatActivity {

    @BindView(R.id.profile_image_button)
    CircleImageView mSetupImageButton;
    @BindView(R.id.setup_name)
    EditText mNameField;
    @BindView(R.id.submit_setup)
    Button mSubmitButton;

    private static final int GALLERY_REQUEST = 1;

    private Uri mImageUri;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRelativeLayout;
    private boolean photoLoaded = true;

    public SetupActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        mRelativeLayout = findViewById(R.id.setup_gradient);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setExitFadeDuration(4500);
        mAnimationDrawable.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);
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

        submitButton();
        setUpImage();
    }

    /**
     * Listener on mSubmitButton.
     * Confirm setup onClick.
     */
    private void submitButton() {
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }

    /**
     * Listener on mSetupImageButton.
     * Set image on click.
     * Open gallery.
     */
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


    /**
     * Load image to imageView.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImageButton.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            Toast.makeText(SetupActivity.this, "Error croping image", Toast.LENGTH_LONG);
        }
    }

    /**
     * Setup account.
     * Sending data to database.
     */
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
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
                }
            });
        } else if (!TextUtils.isEmpty(name) && mImageUri != null && !photoLoaded) {
            mProgress.setMessage("Setting up");
            mProgress.show();

            mDatabase.child(userId).child("name").setValue(name);
            mDatabase.child(userId).child("mail").setValue(mAuth.getCurrentUser().getEmail());
            mDatabase.child(userId).child("userId").setValue(mAuth.getCurrentUser().getUid());

            mProgress.dismiss();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}