package smart.tuke.sk.keepup;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        FirstFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener,
        ThirdFragment.OnFragmentInteractionListener {


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    selectedFragment = new FirstFragment();
                    transaction.replace(R.id.content, selectedFragment);
                    transaction.commit();
                    return true;


                case R.id.navigation_dashboard:

                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    selectedFragment = new SecondFragment();
                    transaction2.replace(R.id.content, selectedFragment);
                    transaction2.commit();
                    return true;


                case R.id.navigation_notifications:

                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    selectedFragment = new ThirdFragment();
                    transaction3.replace(R.id.content, selectedFragment);
                    transaction3.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottom);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new FirstFragment());
        transaction.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    //-----------------------------------TOP RIGHT CORNER MENU--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                Toast.makeText(MainActivity.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
                return true;

            case R.id.help:
                Toast.makeText(MainActivity.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //----------------------------------------------------------------------------------------------

}
