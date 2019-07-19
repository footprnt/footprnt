/*
 * Copyright 2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.footprnt.MainActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Adapters.MultiViewAdapter;
import com.example.footprnt.Profile.Util.Util;
import com.example.footprnt.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment for profile page
 * Created by Clarisa Leu 2019
 */
public class ProfileFragment extends Fragment{
    public final static String TAG = "ProfileFragment";  // tag for logging from this activity
    final ParseUser user = ParseUser.getCurrentUser();
    public final int totalNumCities =4416;
    public final int totalNumCountries = 195;
    public final int totalNumContinents = 7;

    // For stats view:
    HashMap<String, Integer> mCities;
    HashMap<String, Integer> mCountries;
    HashMap<String, Integer> mContinents;

    // For post feed:
    ArrayList<Object> mObjects;
    RecyclerView mLayout;
    MultiViewAdapter mMultiAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setUpLogOutButton(v);

        // Populate stat maps and get posts
        mObjects = new ArrayList<>();
        mCities = new HashMap<>();
        mCountries = new HashMap<>();
        mContinents = new HashMap<>();


        getPosts(
                new Handler(),
                new CalculateStatsCallback() {
                    @Override
                    public void onDone(ArrayList<HashMap<String, Integer>> stats) {
                    }
                });

        mObjects.add(user);

        // For post feed view:
        mMultiAdapter = new MultiViewAdapter(getActivity(),mObjects);
        mLayout = v.findViewById(R.id.rvPosts);
        mLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        mLayout.setAdapter(mMultiAdapter);


        return v;
    }

    interface CalculateStatsCallback {
        void onDone(ArrayList<HashMap<String, Integer>> mStatsList);
    }


    private void setUpLogOutButton(final View v) {
        // Log out button
        final ImageView settings = v.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), settings);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ParseUser.logOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    // Get posts
    private void getPosts(final Handler handler, final CalculateStatsCallback callback) {
        final Post.Query postsQuery = new Post.Query();
        postsQuery
                .getTop()
                .withUser()
                .whereEqualTo("user", ParseUser.getCurrentUser());
        postsQuery.addDescendingOrder("createdAt");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = objects.get(i);
                        // Only add current user's posts
                        mObjects.add(post);
                        mMultiAdapter.notifyItemInserted(mObjects.size() - 1);
                        // Get post stats and update user stats
                        Util helper = new Util();
                        ArrayList<String> postStats = helper.getAddress(getContext(), post.getLocation());
                        // Fill HashMaps
                        // Cities
                        // TODO: check stats with post and update dictionary
                        if (!mCities.containsKey(postStats.get(0))) {
                            // User first visit
                            mCities.put(postStats.get(0), 1);
                        } else {
                            // User already visited, increment count
                            mCities.put(postStats.get(0), mCities.get(postStats.get(0)) + 1);
                        }
                        // Countries
                        if (!mCountries.containsKey(postStats.get(1))) {
                            mCountries.put(postStats.get(1), 1);
                        } else {
                            mCountries.put(postStats.get(1), mCountries.get(postStats.get(1)) + 1);
                        }
                        // Continents
                        if (!mContinents.containsKey(postStats.get(2))) {
                            mContinents.put(postStats.get(2), 1);
                        } else {
                            mContinents.put(postStats.get(2), mContinents.get(postStats.get(2)) + 1);
                        }
                    }

              //      mSwipeContainer.setRefreshing(false);
                } else {
                    logError("Error querying posts", e, true);
                }

                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
            }
        });
    }

    // TODO: implement on click for item to allow user to edit post
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Helper method to handle errors, log them, and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        Log.e(TAG, message, error);
        if (alertUser) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}
