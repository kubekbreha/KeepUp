package com.grizzly.keepup.mainFragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grizzly.keepup.R;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeed;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private Button mStartButton;

    private static final String TAG = "MapTAG";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;

    //database vars
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("run_images");


        mMapView.onResume(); // needed to get the map to display immediately

        mStartButton = view.findViewById(R.id.new_run_button);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSnapshot();
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new

                                     OnMapReadyCallback() {
                                         @Override
                                         public void onMapReady(GoogleMap mMap) {
                 //googleMap = mMap;
                 MapsInitializer.initialize(getContext());
                 mGoogleMap = mMap;
                 if (checkPermission()) {

                     mGoogleMap.setMyLocationEnabled(true);
                     try {
                         // Customise the styling of the base map using a JSON object defined
                         // in a raw resource file.
                         boolean success = mGoogleMap.setMapStyle(
                                 MapStyleOptions.loadRawResourceStyle(
                                         getActivity(), R.raw.map_style_json));

                         if (!success) {
                             Log.e(TAG, "Style parsing failed.");
                         }
                     } catch (Resources.NotFoundException e) {
                         Log.e(TAG, "Can't find style. Error: ", e);
                     }

                     //map changes
                     //mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
                     mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                     mGoogleMap.getUiSettings().setAllGesturesEnabled(false);


                 } else askPermission();

                 mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                     @Override
                     public void onMyLocationChange(Location location) {
                         mLastLocation = location;

                         if (mCurrLocationMarker != null) {
                             mCurrLocationMarker.remove();
                         }

                         //Place current location marker
                         LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                         mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                     }

                 });
             }
         });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    private static final int REQ_PERMISSION = 99;

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQ_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkPermission())
                        mGoogleMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied

                }
                break;
            }
        }

    }

    public boolean CheckGpsStatus() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    //-------------------------------------SCREENSHOT-----------------------------------------------
    private void uploadRun(Uri mRunImageUri) {
        if (mRunImageUri != null) {
            mProgress.setMessage("Uploading");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mRunImageUri.getLastPathSegment());

            filepath.putFile(mRunImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs")
                            .push().setValue(new NewsFeed(downloadUri));

                    mProgress.dismiss();
                }
            });
        }
    }


    private void takeSnapshot() {
        if (mGoogleMap == null) {
            return;
        }
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                uploadRun(getImageUri(getContext(), snapshot));
                // Callback is called from the main thread, so we can modify the ImageView safely.
                //Toast.makeText(getContext(), getImageUri(getContext(), snapshot).toString(), Toast.LENGTH_LONG).show();
            }
        };
        mGoogleMap.snapshot(callback);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}