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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Models.SavedActivity;
import com.example.footprnt.Profile.Adapters.SavedActivitiesAdapter;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display the saved activities/events from the discover page
 *
 * @author Clarisa Leu
 * @version 1.0
 * @since 7-22-19
 */
public class SavedActivities extends AppCompatActivity {

    private final String TAG = "SavedActivities";
    ImageView mBackButton;
    RecyclerView mRvSavedActivities;
    ArrayList<SavedActivity> mSavedActivities;
    SavedActivitiesAdapter mSavedActivitiesAdapter;
    CardView mNoNetwork;
    CardView mNoSavedActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_activities);
        // Set views
        mBackButton = findViewById(R.id.ivBack);
        mRvSavedActivities = findViewById(R.id.rvSavedActivities);
        mNoNetwork = findViewById(R.id.cvAdventure);
        mNoNetwork.setVisibility(View.INVISIBLE);
        mNoSavedActivities = findViewById(R.id.cvRoot2);
        mNoSavedActivities.setVisibility(View.INVISIBLE);

        // Set adapter
        mSavedActivities = new ArrayList<>();
        mSavedActivitiesAdapter = new SavedActivitiesAdapter(mSavedActivities, this);

        // Get saved activities
        if (AppUtil.haveNetworkConnection(getApplicationContext())) {
            getSavedActivities();
            mRvSavedActivities.setLayoutManager(new GridLayoutManager(this, 2));
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

    /**
     * Helper method to get query saved activities from user from DB
     */
    private void getSavedActivities() {
        final SavedActivity.Query query = new SavedActivity.Query();
        query
                .getTop()
                .withUser()
                .whereEqualTo(AppConstants.user, ParseUser.getCurrentUser());
        query.addDescendingOrder(AppConstants.createdAt);
        query.findInBackground(new FindCallback<SavedActivity>() {
            @Override
            public void done(List<SavedActivity> objects, ParseException e) {
                if (e == null) {
                    if (objects.isEmpty()) {
                        mNoSavedActivities.setVisibility(View.VISIBLE);
                        mRvSavedActivities.setVisibility(View.INVISIBLE);
                    } else {
                        for(SavedActivity savedActivity : objects){
                            mSavedActivities.add(savedActivity);
                            mSavedActivitiesAdapter.notifyItemInserted(mSavedActivities.size() - 1);
                        }
                    }
                } else {
                    AppUtil.logError(SavedActivities.this, TAG, getResources().getString(R.string.error_query_saved_activity), e, true);
                }
            }
        });
    }
}
