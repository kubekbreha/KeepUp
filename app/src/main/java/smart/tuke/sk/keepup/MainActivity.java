package smart.tuke.sk.keepup;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import smart.tuke.sk.keepup.fragments.BaseFragment;
import smart.tuke.sk.keepup.fragments.FriendsFragment;
import smart.tuke.sk.keepup.fragments.RunFragment;
import smart.tuke.sk.keepup.fragments.HistoryFragment;

public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener {


    //Better convention to properly name the indices what they are in your app
    private final int INDEX_FRIENDS = FragNavController.TAB1;
    private final int INDEX_HISTORY = FragNavController.TAB2;
    private final int INDEX_RUN = FragNavController.TAB3;

    private BottomBar mBottomBar;
    private FragNavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottom);

        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(INDEX_FRIENDS);
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container)
                .transactionListener(this)
                .rootFragmentListener(this, 5)
                .build();


        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_friends:
                        mNavController.switchTab(INDEX_FRIENDS);
                        break;
                    case R.id.tab_history:
                        mNavController.switchTab(INDEX_HISTORY);
                        break;
                    case R.id.tab_run:
                        mNavController.switchTab(INDEX_RUN);
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                mNavController.clearStack();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case INDEX_FRIENDS:
                return FriendsFragment.newInstance(0);
            case INDEX_HISTORY:
                return HistoryFragment.newInstance(0);
            case INDEX_RUN:
                return RunFragment.newInstance(0);

        }
        throw new IllegalStateException("Need to send an index that we know");
    }




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
}
