/*
 * SavedActivities.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Profile.Adapters.SavedActivitiesAdapter;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppUtil;

import java.util.ArrayList;

/**
 * Activity to display the saved activities/events from the discover page
 *
 * @author Clarisa Leu
 */
public class SavedActivities extends AppCompatActivity {

    private final String TAG = SavedActivities.class.getSimpleName();
    ImageView mBackButton;
    RecyclerView mRvSavedActivities;
    ArrayList<Business> mSavedBussinesses;
    SavedActivitiesAdapter mSavedActivitiesAdapter;
    CardView mNoNetwork;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_activities);
        // Set views
        mBackButton = findViewById(R.id.ivBack);
        mRvSavedActivities = findViewById(R.id.rvSavedActivities);
        mNoNetwork = findViewById(R.id.cvAdventure);
        mNoNetwork.setVisibility(View.INVISIBLE);

        // Set adapter
        mSavedBussinesses = new ArrayList<>();
        mSavedActivitiesAdapter = new SavedActivitiesAdapter(mSavedBussinesses, this);

        // Get saved businesses
        // TODO: implement database for saved activities ?
        if(AppUtil.haveNetworkConnection(getApplicationContext())){
            getSavedActivities();
            mRvSavedActivities.setLayoutManager(new LinearLayoutManager(this));
            mRvSavedActivities.setAdapter(mSavedActivitiesAdapter);
        } else {
            // Display no network connection message
            mRvSavedActivities.setVisibility(View.INVISIBLE);
            mNoNetwork.setVisibility(View.VISIBLE);

        }
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // TODO: implement
    private void getSavedActivities(){

    }
}
