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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.footprnt.MainActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Util.Util;
import com.example.footprnt.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for profile page
 * Created by Clarisa Leu 2019
 */
public class ProfileFragment extends Fragment {
    public final static String TAG = "ProfileFragment";  // tag for logging from this activity
    final ParseUser user = ParseUser.getCurrentUser();

    // For user profile info view:
    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;

    // For stats view:
    StatListAdapter mStatAdapter;  // Adapter for stats
    ListView mLvStats;
    HashMap<String, Integer> mCities;
    HashMap<String, Integer> mCountries;
    HashMap<String, Integer> mContinents;
    ArrayList<HashMap<String, Integer>> mStatsList;

    // For post feed:
    ArrayList<Post> mPosts;  // list of current user posts
    RecyclerView mRvPosts;
    PostAdapter mPostAdapter;
    SwipeRefreshLayout mSwipeContainer;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            loadProfImage();
        }
    }

    private void loadProfImage() {
        if (user.getParseFile("profileImg") != null) {
            String url = user.getParseFile("profileImg").getUrl();
            System.out.println(url);
            Glide.with(getContext()).load(url).into(mIvProfileImage);
        } else {
            Glide.with(getContext()).load(R.drawable.ic_user).into(mIvProfileImage);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setUpLogOutButton(v);

        // Populate stat maps and get posts
        mPosts = new ArrayList<>();
        mCities = new HashMap<>();
        mCountries = new HashMap<>();
        mContinents = new HashMap<>();
        mStatsList = new ArrayList<>();

        // Call getPosts() first
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
        mTvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), UserSettings.class);
                startActivityForResult(it, 1);
            }
        });


        // For profile image:
        mIvProfileImage = v.findViewById(R.id.ivProfileImageMain);
        loadProfImage();

        getPosts(
                new Handler(),
                new CalculateStatsCallback() {
                    @Override
                    public void onDone(ArrayList<HashMap<String, Integer>> stats) {
                        mStatsList.add(mCities);
                        mStatsList.add(mCountries);
                        mStatsList.add(mContinents);
                        updateStats();
                    }
                });

        // Refresh listener for post feed and update stats
        mSwipeContainer = v.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts(new Handler(), new CalculateStatsCallback() {
                    @Override
                    public void onDone(ArrayList<HashMap<String, Integer>> stats) {
                        updateStats();
                    }
                });
            }
        });

        // For post feed view:
        mPostAdapter = new PostAdapter(mPosts);
        mRvPosts = v.findViewById(R.id.rvFeed);
        mRvPosts.setLayoutManager(new GridLayoutManager(v.getContext(), 3));
        mRvPosts.setAdapter(mPostAdapter);

        // For stat view
        mLvStats = v.findViewById(R.id.lvStatKey);
        mStatAdapter = new StatListAdapter(mStatsList, v.getContext());
        mLvStats.setAdapter(mStatAdapter);

        return v;
    }

    interface CalculateStatsCallback {
        void onDone(ArrayList<HashMap<String, Integer>> mStatsList);
    }

// TODO: Make interface for stats
//    interface CustomStatInterface {
//        String getName();
//
//        String calculateStat(String data);
//    }


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
                        mPosts.add(post);
                        mPostAdapter.notifyItemInserted(mPosts.size() - 1);
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

                    mSwipeContainer.setRefreshing(false);
                } else {
                    logError("Error querying posts", e, true);
                }

                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onDone(mStatsList);
                            }
                        });
            }
        });
    }

    private void updateStats() {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for (int i = 0; i < mStatsList.size(); i++) {  // Loop through number of stats we're tracking
            HashMap<String, Integer> innerList = mStatsList.get(i);
            ArrayList<String> toAdd = new ArrayList<>();
            toAdd.add(String.format("%s", innerList.size()));
            res.add(toAdd);
        }
        user.put("stats", res);
        user.saveInBackground();
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
