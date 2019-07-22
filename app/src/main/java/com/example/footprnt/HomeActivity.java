package com.example.footprnt;

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

/**
 * Handles displaying three main fragments and navigation bar
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class HomeActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment mFragment1 = new MapFragment();
    final Fragment mFragment2 = new DiscoverFragment();
    final Fragment mFragment3 = new ProfileFragment();

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
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);
    }
}
