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

package com.grizzly.keepup.mainFragments.Map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
 * Created by kubek on 1/21/18.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private static final int REQ_PERMISSION = 99;
    private static final String TAG = "MapFragment";
    private Button mStartButton;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationManager mLocationManager;

    //database vars
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;

    private Boolean mButtonStart = false;
    private boolean mServiceBound = false;
    private long mTimeWhenStopped;

    private Chronometer expandedChronometer;
    private TextView expandedDistance;
    private TextView expandedChronometerText;
    private TextView expandedDistanceText;
    private TextView notExpandedText;
    private CardView expandCard;
    private FrameLayout notExpandedFrame;
    private FrameLayout expandedFrame;

    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mView = view;

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("run_images");

        mStartButton = view.findViewById(R.id.new_run_button);

        expandCard = view.findViewById(R.id.card_view_map);
        expandedChronometer = view.findViewById(R.id.map_time_traveled);
        expandedDistance = view.findViewById(R.id.map_meters_traveled);
        expandedChronometerText = view.findViewById(R.id.map_time_traveled_text);
        expandedDistanceText = view.findViewById(R.id.map_meters_traveled_text);
        notExpandedText = view.findViewById(R.id.map_not_expanded_text);
        expandedFrame = view.findViewById(R.id.frame_statistic_expanded);
        notExpandedFrame = view.findViewById(R.id.frame_statistic_not_expanded);

        startButtonListener();
        showMap();
        expandCardListener();

        return view;
    }

    /**
     * Expand cardview.
     */
    private void expandCardListener() {
        expandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notExpandedText.getVisibility() == View.VISIBLE) {
                    //ibt_show_more.animate().rotation(0).start();
                    TransitionManager.beginDelayedTransition(expandCard, new AutoTransition().setDuration(124));
                    notExpandedText.setVisibility(View.GONE);
                    expandedFrame.setVisibility(View.VISIBLE);
                    notExpandedFrame.setVisibility(View.GONE);
                    expandedChronometer.setVisibility(View.VISIBLE);
                    expandedChronometerText.setVisibility(View.VISIBLE);
                    expandedDistance.setVisibility(View.VISIBLE);
                    expandedDistanceText.setVisibility(View.VISIBLE);
                } else {
                    //ibt_show_more.animate().rotation(180).start();
                    TransitionManager.beginDelayedTransition(expandCard, new AutoTransition().setDuration(124));
                    notExpandedText.setVisibility(View.VISIBLE);
                    expandedFrame.setVisibility(View.GONE);
                    notExpandedFrame.setVisibility(View.VISIBLE);
                    expandedChronometer.setVisibility(View.GONE);
                    expandedChronometerText.setVisibility(View.GONE);
                    expandedDistance.setVisibility(View.GONE);
                    expandedDistanceText.setVisibility(View.GONE);
                }
            }
        });

    }


    /**
     * Show map.
     * Called in onCreateView.
     */
    private void showMap() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
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

                                                     LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                     mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                                                     if (mButtonStart) {
                                                         LatLng latLngNew = new LatLng(location.getLatitude() + 0.02,
                                                                 location.getLongitude() + 0.02);
                                                         addPolyline(latLng, latLngNew);
                                                         float[] distance = getMeters(latLng, latLngNew);
                                                         //mDistanceTextView.setText((int) distance[0]);
                                                     }
                                                 }

                                             });
                                         }
                                     });
    }

    /**
     * Listener on startRun button.
     * Start run (chronometer).
     * On Stop takeSnapshot().
     */
    private void startButtonListener() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mButtonStart) {
                    startChronometer();
                    mButtonStart = true;
                    mStartButton.setText("stop");

                } else {
                    takeSnapshot();
                    mTimeWhenStopped = 0;
                    stopChronometer();
                    mButtonStart = false;
                    mStartButton.setText("start");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mButtonStart) {
            mStartButton.setText("stop");
            expandedChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenStopped);
            expandedChronometer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mTimeWhenStopped = expandedChronometer.getBase() - SystemClock.elapsedRealtime();
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

    /**
     * Get meters from start and end LatLng.
     */
    private float[] getMeters(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude,
                end.latitude, end.longitude, results);
        return results;
    }

    /**
     * Add polyline to map.
     */
    private void addPolyline(LatLng start, LatLng end) {
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(start.latitude, start.longitude))
                .add(new LatLng(end.latitude, end.longitude))
                .width(25)
                .color(Color.DKGRAY);

        // Get back the mutable Polyline
        Polyline polyline = mGoogleMap.addPolyline(rectOptions);
    }

    /**
     * Start chronometer.
     */
    private void startChronometer() {
        long systemCurrTime = SystemClock.elapsedRealtime();
        expandedChronometer.setBase(systemCurrTime);
        expandedChronometer.start();
    }

    /**
     * Stop chronometer.
     */
    private void stopChronometer() {
        expandedChronometer.stop();
    }

    /**
     * Check if permissions granted for Location and Storage.
     */
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


    /**
     * Ask permisions for Location and Storage.
     */
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
                    if (checkPermission()) mGoogleMap.setMyLocationEnabled(true);
                } else {
                    // Permission denied
                }
                break;
            }
        }
    }

    /**
     * Take snapshot of map.
     */
    private void takeSnapshot() {
        if (mGoogleMap == null) {
            return;
        }
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                uploadRun(getImageUri(getContext(), snapshot));
            }
        };
        mGoogleMap.snapshot(callback);
    }

    /**
     * Upload run to Firebase.
     */
    private void uploadRun(Uri mRunImageUri) {
        if (mRunImageUri != null) {
            mProgress.setMessage("Uploading");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mRunImageUri.getLastPathSegment());
            final int elapsedMillis = (int) (SystemClock.elapsedRealtime() - expandedChronometer.getBase());

            filepath.putFile(mRunImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs")
                            .push().setValue(new NewsFeed(downloadUri, expandedDistance.getText().toString(),
                            elapsedMillis));

                    mProgress.dismiss();
                }
            });
        }
    }

    /**
     * Get image uri from Bitmap.
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}