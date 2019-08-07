/*
 * UserStatistics.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.R;

/**
 * User Statistics Page
 *
 * @author Clarisa Leu-Rodriguez
 * @version 1.0
 * @since 7-22-19
 */
public class UserStatistics extends AppCompatActivity {

    private final String TAG = "UserStatistics";
    ImageView mBackButton;
    RecyclerView mRvCities;
    RecyclerView mRvCountries;
    RecyclerView mRvContinents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        // Set Views
        mBackButton = findViewById(R.id.ivBack);
        mRvCities = findViewById(R.id.rvCities);
        mRvCountries = findViewById(R.id.rvCountries);
        mRvContinents = findViewById(R.id.rvContinents);

    }
}
