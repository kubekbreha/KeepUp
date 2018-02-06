package com.grizzly.keepup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.grizzly.keepup.mainFragments.Map.MapFragment;
import com.grizzly.keepup.mainFragments.newsPage.NewsFeedFragment;
import com.grizzly.keepup.mainFragments.ProfileFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Location";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        mAuth = FirebaseAuth.getInstance();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        //ask to turn on location
        CheckEnableGPS();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), "RUN");
        adapter.addFragment(new NewsFeedFragment(), "FEED");
        adapter.addFragment(new ProfileFragment(), "PROFILE");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }


    private void CheckEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            Toast.makeText(MainActivity.this, "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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


}
