/*
 * HomeActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
    final Fragment mFragment1 = new MapFragment();
    final Fragment mFragment2 = new DiscoverFragment();
    final Fragment mFragment3 = new ProfileFragment();


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload the user image on the profile fragment
        if(resultCode == Constants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE){
            mFragment3.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = mFragment1;
                        break;
                    case R.id.navigation_dashboard:
                        fragment = mFragment2;
                        break;
                    case R.id.navigation_notifications:
                    default:
                        fragment = mFragment3;
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);
    }
}
