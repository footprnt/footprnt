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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.footprnt.Discover.DiscoverFragment;
import com.example.footprnt.Map.MapFragment;
import com.example.footprnt.Profile.ProfileFragment;
import com.example.footprnt.Util.Constants;

/**
 * Handles displaying three main fragments and navigation bar
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class HomeActivity extends AppCompatActivity {

    final FragmentManager mFragmentManager = getSupportFragmentManager();
    BottomNavigationView navView;
    View mShadow;
    Fragment mFragment1;
    Fragment mFragment2;
    Fragment mFragment3;
    ViewPager viewPager;
    MenuItem mPrevMenuItem;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload the user image on the profile fragment
        if (resultCode == Constants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE) {
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode == 302){
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode == 301){
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mShadow = findViewById(R.id.shadow);

        navView = findViewById(R.id.nav_view);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.pop_two);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mp.start();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_dashboard:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;

                }
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    navView.getMenu().getItem(0).setChecked(false);
                }
                navView.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = navView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
    }

    public void hideBottomNav(){
        mShadow.setVisibility(View.INVISIBLE);
        navView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
    }

    public void showBottomNav(){
        mShadow.setVisibility(View.VISIBLE);
        navView.setVisibility(View.VISIBLE);

    }

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
