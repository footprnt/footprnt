/*
 * UserStatistics.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Profile.Adapters.StatAdapter;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;

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

        // Network error case
        if (!AppUtil.haveNetworkConnection(getApplicationContext())) {
            Toast.makeText(this, getResources().getString(R.string.network_error_try_again), Toast.LENGTH_SHORT).show();
            finish();
        }

        View messageView = LayoutInflater.from(this).inflate(R.layout.instructions, null);
        TextView text = messageView.findViewById(R.id.textView3);
        text.setText(getResources().getString(R.string.message_user_stats));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog_NoActionBar);
        alertDialogBuilder.setView(messageView);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(900, 600);

        // Set Views
        mBackButton = findViewById(R.id.ivBack);
        mRvCities = findViewById(R.id.rvCities);
        mRvCountries = findViewById(R.id.rvCountries);
        mRvContinents = findViewById(R.id.rvContinents);

        mCities = getIntent().getStringArrayListExtra(AppConstants.city);
        mCountries = getIntent().getStringArrayListExtra(AppConstants.country);
        mContinents = getIntent().getStringArrayListExtra(AppConstants.continent);

        // Nothing to show case
        if (mCities.isEmpty() && mCountries.isEmpty() && mContinents.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.nothing_to_show), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mCityAdapter = new StatAdapter(mCities, this);
            mRvCities.setLayoutManager(new GridLayoutManager(this, 3));
            mRvCities.setAdapter(mCityAdapter);

            mCountryAdapter = new StatAdapter(mCountries, this);
            mRvCountries.setLayoutManager(new GridLayoutManager(this, 3));
            mRvCountries.setAdapter(mCountryAdapter);

            mContinentAdapter = new StatAdapter(mContinents, this);
            mRvContinents.setLayoutManager(new GridLayoutManager(this, 3));
            mRvContinents.setAdapter(mContinentAdapter);
        }
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
