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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grizzly.keepup.R;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeed;
import com.grizzly.keepup.service.StopwatchService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

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
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;

    private Boolean mButtonStart = false;
    private boolean mServiceBound = false;

    private TextView notExpandedDistance;
    private TextView notExpandedTime;
    private ImageView notExpandedImageDistance;
    private ImageView notExpandedImageTime;

    private CardView expandCard;
    private FrameLayout notExpandedFrame;
    private FrameLayout expandedFrame;

    private TextView expandedTime;
    private TextView expandedDistance;
    private TextView expandedCalories;
    private TextView expandedTempo;
    private ImageView expandedTimeImage;
    private ImageView expandedDistanceImage;
    private ImageView expandedCaloriesImage;
    private ImageView expandedTempoImage;


    private StopwatchService mStopwatchService;
    private Thread stopwatchThread;

    private ArrayList<LatLng> polylinePoints;
    private Polyline line;
    private LatLngBounds.Builder builder;
    private boolean checkedGPS;

    private Timer timer;
    private String tempFirebaseRoute;
    private List<Integer> minuteDistance = new ArrayList<>();

    public static int minutes = 0;
    private int minutesPolicy = 0;

    private Random rand = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


        mProgress = new ProgressDialog(getContext());
        mStorageImage = FirebaseStorage.getInstance().getReference().child("run_images");

        mStartButton = view.findViewById(R.id.new_run_button);

        //clickable card
        expandCard = view.findViewById(R.id.card_view_map);
        //not expanded items
        notExpandedDistance = view.findViewById(R.id.map_not_expanded_distance);
        notExpandedTime = view.findViewById(R.id.map_not_expanded_time);
        notExpandedImageDistance = view.findViewById(R.id.map_not_expanded_distance_icon);
        notExpandedImageTime = view.findViewById(R.id.map_not_expanded_time_icon);
        //frame visibility
        expandedFrame = view.findViewById(R.id.frame_statistic_expanded);
        notExpandedFrame = view.findViewById(R.id.frame_statistic_not_expanded);
        //expanded items
        expandedTime = view.findViewById(R.id.map_expanded_time);
        expandedDistance = view.findViewById(R.id.map_expanded_distance);
        expandedCalories = view.findViewById(R.id.map_expanded_calories);
        expandedTempo = view.findViewById(R.id.map_expanded_tempo);
        expandedTimeImage = view.findViewById(R.id.map_expanded_time_icon);
        expandedDistanceImage = view.findViewById(R.id.map_expanded_distance_icon);
        expandedCaloriesImage = view.findViewById(R.id.map_expanded_calories_icon);
        expandedTempoImage = view.findViewById(R.id.map_expanded_tempo_icon);

        polylinePoints = new ArrayList<>();


        startButtonListener();
        expandCardListener();
        showMap();

        return view;
    }


    /**
     * Start stopwatch threat and refresh time textView every second.
     */
    private void startStopwatchThread() {
        stopwatchThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                expandedTime.setText(mStopwatchService.getTimestampString());
                                notExpandedTime.setText(mStopwatchService.getTimestampString());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        timer = new Timer();
        stopwatchThread.start();
        timer.schedule(new SendMinuteData(), 0, 5000);
    }


    /**
     * Expand cardview.
     */
    private void expandCardListener() {
        expandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notExpandedDistance.getVisibility() == View.VISIBLE) {
                    //ibt_show_more.animate().rotation(0).start();
                    TransitionManager.beginDelayedTransition(expandCard, new AutoTransition().setDuration(124));
                    expandedFrame.setVisibility(View.VISIBLE);
                    notExpandedFrame.setVisibility(View.GONE);

                    notExpandedDistance.setVisibility(View.GONE);
                    notExpandedTime.setVisibility(View.GONE);
                    notExpandedImageDistance.setVisibility(View.GONE);
                    notExpandedImageTime.setVisibility(View.GONE);

                    expandedTime.setVisibility(View.VISIBLE);
                    expandedDistance.setVisibility(View.VISIBLE);
                    expandedCalories.setVisibility(View.VISIBLE);
                    expandedTempo.setVisibility(View.VISIBLE);
                    expandedTimeImage.setVisibility(View.VISIBLE);
                    expandedDistanceImage.setVisibility(View.VISIBLE);
                    expandedCaloriesImage.setVisibility(View.VISIBLE);
                    expandedTempoImage.setVisibility(View.VISIBLE);

                } else {
                    //ibt_show_more.animate().rotation(180).start();
                    TransitionManager.beginDelayedTransition(expandCard, new AutoTransition().setDuration(124));
                    expandedFrame.setVisibility(View.GONE);
                    notExpandedFrame.setVisibility(View.VISIBLE);

                    notExpandedDistance.setVisibility(View.VISIBLE);
                    notExpandedTime.setVisibility(View.VISIBLE);
                    notExpandedImageDistance.setVisibility(View.VISIBLE);
                    notExpandedImageTime.setVisibility(View.VISIBLE);

                    expandedTime.setVisibility(View.GONE);
                    expandedDistance.setVisibility(View.GONE);
                    expandedCalories.setVisibility(View.GONE);
                    expandedTempo.setVisibility(View.GONE);
                    expandedTimeImage.setVisibility(View.GONE);
                    expandedDistanceImage.setVisibility(View.GONE);
                    expandedCaloriesImage.setVisibility(View.GONE);
                    expandedTempoImage.setVisibility(View.GONE);
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
                                             if (!checkedGPS) {
                                                 enableGPS();
                                                 checkedGPS = true;
                                             }

                                             if (checkPermission()) {
                                                 loadMap();
                                                 mGoogleMap.setMyLocationEnabled(true);
                                             } else {
                                                 askPermission();
                                                 showMap();
                                             }

                                             mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                 @Override
                                                 public void onMyLocationChange(Location location) {
                                                     mLastLocation = location;

                                                     if (mCurrLocationMarker != null) {
                                                         mCurrLocationMarker.remove();
                                                     }

                                                     //TODO: Test this.
                                                     builder = new LatLngBounds.Builder();
                                                     for (int i = 0; i < polylinePoints.size(); i++) {
                                                         builder.include(polylinePoints.get(i));
                                                     }


                                                     LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                     mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                                                     if (mButtonStart) {
                                                         polylinePoints.add(latLng);
                                                         redrawLine();
                                                         if (polylinePoints.size() != 0) {
                                                             int meters = (int) getMeters();
                                                             expandedDistance.setText("" + meters);
                                                             notExpandedDistance.setText("" + meters);


                                                         }
                                                     }

                                                     if (minutes == minutesPolicy) {
                                                         //minuteDistance.add((int)getMeters());
                                                         minuteDistance.add(rand.nextInt(10) + 1);
                                                         minutesPolicy++;
                                                         System.out.println("added");
                                                     }
                                                 }

                                             });
                                         }
                                     });

    }

    /**
     * Ask to turn on Location Services if turned off.
     */
    private void enableGPS() {
        String provider = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            Toast.makeText(getActivity(), "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Turn on location");
            alertDialog.setMessage("In order to use this app properly you need to turn on Location");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "TURN ON",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }

    /**
     * Listener on startRun button.
     * Start run (chronometer).
     * On Stop takeSnapshot().`
     * <p>
     * Every run time delayed for about 1,5 sec.
     */
    private void startButtonListener() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mButtonStart) {
                    tempFirebaseRoute = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs")
                            .push().getKey();
                    mButtonStart = true;

                    Intent intent = new Intent(getActivity(), StopwatchService.class);
                    getActivity().startService(intent);
                    getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);


                    mStartButton.setText("stop");
                    mStartButton.setBackground(getResources().getDrawable(R.drawable.button_background_gradient_red));
                    startStopwatchThread();
                } else {
                    stopwatchThread.interrupt();
                    timer.cancel();
                    timer.purge();
                    Intent intent = new Intent(getActivity(),
                            StopwatchService.class);
                    getActivity().stopService(intent);

                    takeSnapshot();
                    if (mServiceBound) {
                        getActivity().unbindService(mServiceConnection);
                        mServiceBound = false;
                    }

                    mButtonStart = false;

                    mStartButton.setText("start");
                    mStartButton.setBackground(getResources().getDrawable(R.drawable.button_background_gradient_green));
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
            mStartButton.setBackground(getResources().getDrawable(R.drawable.button_background_gradient_red));
        }
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
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void loadMap() {
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
    }


    /**
     * Get meters from start and end LatLng.
     */
    private float getMeters() {
        float[] results = new float[1];

        float returned = 0;
        for (int i = 0; i < polylinePoints.size(); i++) {
            Location.distanceBetween(polylinePoints.get(i).latitude, polylinePoints.get(i).longitude,
                    polylinePoints.get(i).latitude, polylinePoints.get(i).longitude, results);
            returned += results[0];
        }
        return returned;
    }


    /**
     * Add polylines to map from array.
     */
    private void redrawLine() {

        mGoogleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(8).color(R.color.colorAccent).geodesic(true);
        for (int i = 0; i < polylinePoints.size(); i++) {
            LatLng point = polylinePoints.get(i);
            options.add(point);
        }
        //addMarker(); //add Marker in current position
        line = mGoogleMap.addPolyline(options); //add Polyline
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
        loadMap();
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


            final float elapsedMillis = mStopwatchService.getTimestamp();


            filepath.putFile(mRunImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();


                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs")
                            .child(tempFirebaseRoute).setValue(new NewsFeed(downloadUri, expandedDistance.getText().toString(),
                            (int) elapsedMillis, mAuth.getUid(), minuteDistance, tempFirebaseRoute));

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


    /**
     * Dont konw yet :D
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StopwatchService.MyBinder myBinder = (StopwatchService.MyBinder) service;
            mStopwatchService = myBinder.getService();
            mServiceBound = true;
        }
    };


}