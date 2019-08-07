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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Profile.Adapters.StatAdapter;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;

import java.util.ArrayList;

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
    ArrayList<String> mCities;  // Associated cities user has traveled
    ArrayList<String> mCountries;  // Associated countries user has traveled
    ArrayList<String> mContinents;  // Associated continents user has traveled
    StatAdapter mCityAdapter;
    StatAdapter mCountryAdapter;
    StatAdapter mContinentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        // Set Views
        mBackButton = findViewById(R.id.ivBack);
        mRvCities = findViewById(R.id.rvCities);
        mRvCountries = findViewById(R.id.rvCountries);
        mRvContinents = findViewById(R.id.rvContinents);

        mCities = getIntent().getStringArrayListExtra(AppConstants.city);
        mCountries = getIntent().getStringArrayListExtra(AppConstants.country);
        mContinents = getIntent().getStringArrayListExtra(AppConstants.continent);

        mCityAdapter = new StatAdapter(mCities, this);
        mRvCities.setLayoutManager(new LinearLayoutManager(this));
        mRvCities.setAdapter(mCityAdapter);

        mCountryAdapter = new StatAdapter(mCountries, this);
        mRvCountries.setLayoutManager(new LinearLayoutManager(this));
        mRvCountries.setAdapter(mCountryAdapter);

        mContinentAdapter = new StatAdapter(mContinents, this);
        mRvContinents.setLayoutManager(new LinearLayoutManager(this));
        mRvContinents.setAdapter(mContinentAdapter);
    }
}
