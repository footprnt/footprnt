package com.example.footprnt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

// TODO: Add Javadoc
public class HomeActivity extends AppCompatActivity { // TODO: Add newline between class and first line of code
    final FragmentManager fragmentManager = getSupportFragmentManager();

    // TODO: All private class variables named like mVariableName
    final Fragment fragment1 = new MapFragment();
    final Fragment fragment2 = new DiscoverFragment();
    final Fragment fragment3 = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view); // TODO: "final"
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment; // TODO: "final"
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = fragment1;
                        break;
                    case R.id.navigation_dashboard:
                        fragment = fragment2;
                        break;
                    case R.id.navigation_notifications:
                    default:
                        fragment = fragment3;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);
    }
}
