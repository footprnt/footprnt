/*
 * HomeActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Database.StatDatabase;
import com.example.footprnt.Database.UserDatabase;
import com.example.footprnt.Discover.DiscoverFragment;
import com.example.footprnt.Map.MapFragment;
import com.example.footprnt.Profile.ProfileFragment;
import com.example.footprnt.Util.AppConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Handles displaying three main fragments and navigation bar
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";
    final FragmentManager mFragmentManager = getSupportFragmentManager();
    View mShadow;
    Fragment mFragment1;
    Fragment mFragment2;
    Fragment mFragment3;
    ViewPager mViewPager;
    BottomNavigationView mNavView;
    MenuItem mPrevMenuItem;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload the user image on the profile fragment
        if (resultCode == AppConstants.RELOAD_USER_PROFILE_FRAGMENT_REQUEST_CODE) {
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
        // Delete post from profile fragment
        if (resultCode == AppConstants.DELETE_POST_FROM_PROFILE) {
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
        // Update post from profile fragment
        if (resultCode == AppConstants.UPDATE_POST_FROM_PROFILE) {
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PostDatabase.destroyInstance();
        UserDatabase.destroyInstance();
        StatDatabase.destroyInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mViewPager = findViewById(R.id.viewpager);
        mShadow = findViewById(R.id.shadow);
        mNavView = findViewById(R.id.nav_view);

        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.pop_two);
        mNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mp.start();  // Play sound anytime user switches page from bottom nav
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_dashboard:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_notifications:
                        mViewPager.setCurrentItem(2);
                        break;
                    default:
                        mViewPager.setCurrentItem(0);
                        break;
                }
                return true;
            }
        });
        mNavView.setSelectedItemId(R.id.navigation_home);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    mNavView.getMenu().getItem(0).setChecked(false);
                }
                mNavView.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = mNavView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Helper method to hide bottom nav bar
     */
    public void hideBottomNav() {
        mShadow.setVisibility(View.INVISIBLE);
        mNavView.setVisibility(View.INVISIBLE);
    }

    /**
     * Helper method to show bottom nav bar
     */
    public void showBottomNav() {
        mShadow.setVisibility(View.VISIBLE);
        mNavView.setVisibility(View.VISIBLE);
    }

    /**
     * Set up view pager for home activity to switch between fragments on swipe
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mFragmentManager);
        mFragment1 = new MapFragment();
        mFragment2 = new DiscoverFragment();
        mFragment3 = new ProfileFragment();
        viewPagerAdapter.addFragment(mFragment1);
        viewPagerAdapter.addFragment(mFragment2);
        viewPagerAdapter.addFragment(mFragment3);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
