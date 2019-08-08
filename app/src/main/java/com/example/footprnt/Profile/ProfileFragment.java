/*
 * ProfileFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.footprnt.Database.Models.PostWrapper;
import com.example.footprnt.Database.Models.StatWrapper;
import com.example.footprnt.Database.Models.UserWrapper;
import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Database.Repository.PostRepository;
import com.example.footprnt.Database.Repository.StatRepository;
import com.example.footprnt.Database.Repository.UserRepository;
import com.example.footprnt.Database.StatDatabase;
import com.example.footprnt.Database.UserDatabase;
import com.example.footprnt.LoginActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Adapters.MultiViewAdapter;
import com.example.footprnt.Profile.Util.ProfileConstants;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment for profile page
 *
 * @author Clarisa Leu, Jocelyn Shen
 * @version 1.0
 * @since 7-22-19
 */
public class ProfileFragment extends Fragment {

    public final static String TAG = "ProfileFragment";
    final ParseUser mUser = ParseUser.getCurrentUser();

    // For database:
    PostRepository mPostRepository;
    UserRepository mUserRepository;
    StatRepository mStatRepository;
    UserWrapper mUserWrapper;
    StatWrapper mStatWrapper;
    List<PostWrapper> mPostWrapperDB;

    // For post feed:
    ArrayList<Object> mObjects;
    ArrayList<Post> mPosts;
    ArrayList<PostWrapper> mPostWrappers;
    RecyclerView mLayout;
    MultiViewAdapter mMultiAdapter;

    // For stats view:
    HashMap<String, Integer> mCities;  // Contains the cities and number of times visited by user
    HashMap<String, Integer> mCountries;  // Contains the countries and number of times visited by user
    HashMap<String, Integer> mContinents;  // Contains the continents and number of times visited by user
    ArrayList<HashMap<String, Integer>> mStats;  // StatWrapper to be passed to adapter

    // For progress bar & swipe to refresh:
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mLayout = v.findViewById(R.id.rvPosts);
        mProgressBar = v.findViewById(R.id.pbLoading);
        mSwipeContainer = v.findViewById(R.id.swipeContainer);
        setUpToolbar(v);
        setUpDB();

        // Populate stat maps and get posts
        mObjects = new ArrayList<>();
        mPostWrappers = new ArrayList<>();
        mCities = new HashMap<>();
        mCountries = new HashMap<>();
        mContinents = new HashMap<>();
        mPosts = new ArrayList<>();
        mStats = new ArrayList<>();

        // Get posts from DB or Network
        if (AppUtil.haveNetworkConnection(getActivity())) {
            StatDatabase.getStatDatabase(getContext()).clearAllTables();
            PostDatabase.getPostDatabase(getContext()).clearAllTables();
            UserDatabase.getUserDatabase(getContext()).clearAllTables();
            getPosts();
        } else {
            // Add User
            if (mUserWrapper != null) {
                mObjects.add(mUserWrapper);
            }
            // Add Stats
            if (mStatWrapper != null) {
                mObjects.add(mStatWrapper);
            }
            // Add posts of no posts
            if (mPostRepository.getPosts().size() == 0) {
                mObjects.add(ProfileConstants.noPosts);
            } else {
                for (PostWrapper p : mPostRepository.getPosts()) {
                    mPostWrappers.add(p);
                }
                mObjects.addAll(mPostWrappers);
            }
        }
        // For post feed view:
        mMultiAdapter = new MultiViewAdapter(getContext(), mObjects);
        mLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        mLayout.setAdapter(mMultiAdapter);


        // For Refresh:
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshViews();
            }
        });

        return v;
    }

    /**
     * Helper method to refresh views
     */
    private void refreshViews(){
        mObjects.clear();
        getPosts();
        mSwipeContainer.setRefreshing(false);
    }


    /**
     * Helper method to set up database
     */
    private void setUpDB() {
        mPostRepository = new PostRepository(getActivity().getApplicationContext());
        mPostWrapperDB = mPostRepository.getPosts();
        mUserRepository = new UserRepository(getActivity().getApplicationContext());
        mUserRepository.insertUser(new UserWrapper(ParseUser.getCurrentUser()));
        mUserWrapper = mUserRepository.getUser();
        mStatRepository = new StatRepository(getActivity().getApplicationContext());
        mStatWrapper = mStatRepository.getStats();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppConstants.RELOAD_USER_PROFILE_FRAGMENT_REQUEST_CODE) {
            mMultiAdapter.notifyItemChanged(0);
        }
        // Delete post
        if (resultCode == AppConstants.DELETE_POST_FROM_PROFILE) {
            int position = data.getIntExtra(AppConstants.position, 0);
            // Case where user deletes only post:
            if (mObjects.size() == 3) {
                mObjects.remove(position);
                mMultiAdapter.notifyItemRemoved(position);
                mObjects.add(ProfileConstants.noPosts);
                mMultiAdapter.notifyItemInserted(position);
                mMultiAdapter.notifyItemChanged(position);
            } else {
                mObjects.remove(position);
                mMultiAdapter.notifyItemRemoved(position);
                mMultiAdapter.notifyItemChanged(position);
            }
        }
        // Save post
        if (resultCode == AppConstants.UPDATE_POST_FROM_PROFILE) {
            int position = data.getIntExtra(AppConstants.position, 0);
            Post post = (Post) data.getSerializableExtra(AppConstants.Post);
            mObjects.set(position, post);
            mMultiAdapter.notifyItemChanged(position);
        }
    }

    /**
     * Helper method to set up the toolbar. Toolbar functionality includes settings
     * log out, and viewing the users saved posts/saved activities
     *
     * @param v this view
     */
    private void setUpToolbar(final View v) {
        final ImageView settings = v.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), settings);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = null;
                        if (item.getItemId() == R.id.logout) {
                            // Logout
                            if (AppUtil.haveNetworkConnection(getActivity().getApplicationContext())) {
                                // Destroy instances of DB's on logout
                                StatDatabase.getStatDatabase(getContext()).clearAllTables();
                                PostDatabase.getPostDatabase(getContext()).clearAllTables();
                                UserDatabase.getUserDatabase(getContext()).clearAllTables();
                                ParseUser.logOut();
                                intent = new Intent(getActivity(), LoginActivity.class);
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.network_error_try_again), Toast.LENGTH_SHORT).show();
                            }
                        } else if (item.getItemId() == R.id.savedPosts) {
                            // Saved Posts
                            intent = new Intent(getContext(), SavedPosts.class);
                        } else if (item.getItemId() == R.id.savedActivities) {
                            // Saved Activities
                            intent = new Intent(getContext(), SavedActivities.class);
                        } else if (item.getItemId() == R.id.userStatistics) {
                            // User Statistics - pass in the key set of the users traveled places
                            intent = new Intent(getContext(), UserStatistics.class);
                            ArrayList<String> cityKeys = new ArrayList<>(mCities.keySet());
                            ArrayList<String> countryKeys = new ArrayList<>(mCountries.keySet());
                            ArrayList<String> continentKeys = new ArrayList<>(mContinents.keySet());
                            intent.putStringArrayListExtra(AppConstants.city, cityKeys);
                            intent.putStringArrayListExtra(AppConstants.country, countryKeys);
                            intent.putStringArrayListExtra(AppConstants.continent, continentKeys);
                        }
                        if (intent != null) {
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    /**
     * Helper method to get posts from parse as well as populate stat maps
     */
    private void getPosts() {
        final Post.Query postsQuery = new Post.Query();
        // Only add current user's posts
        postsQuery.getTop().withUser().whereEqualTo(AppConstants.user, ParseUser.getCurrentUser());
        postsQuery.addDescendingOrder(AppConstants.createdAt);
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = objects.get(i);
                        mPostRepository.insertPost(new PostWrapper(post));
                        // Get post stats and update user stats
                        mPosts.add(post);
                        // Fill HashMaps and handle null values
                        // Cities
                        if (post.getCity() != null) {
                            String city = post.getCity();
                            if (!mCities.containsKey(city)) {
                                // User first visit
                                mCities.put(city, 1);
                            } else {
                                // User already visited, increment count
                                mCities.put(city, mCities.get(city) + 1);
                            }
                        }
                        // Countries
                        if (post.getCountry() != null) {
                            String country = post.getCountry();
                            if (!mCountries.containsKey(country)) {
                                mCountries.put(country, 1);
                            } else {
                                mCountries.put(country, mCountries.get(country) + 1);
                            }
                        }
                        // Continents
                        if (post.getContinent() != null) {
                            String continent = post.getContinent();
                            if (!mContinents.containsKey(continent)) {
                                mContinents.put(continent, 1);
                            } else {
                                mContinents.put(continent, mContinents.get(continent) + 1);
                            }
                        }
                    }
                } else {
                    AppUtil.logError(getContext(), TAG, getResources().getString(R.string.error_query_posts), e, true);
                }
                mObjects.add(mUser);
                mStats.add(mCities);
                mStats.add(mCountries);
                mStats.add(mContinents);
                mObjects.add(mStats);
                mStatRepository.insertStat(new StatWrapper(mStats, mUser));
                mMultiAdapter.notifyDataSetChanged();
                if (mPosts.size() > 0) {
                    mObjects.addAll(mPosts);
                    mMultiAdapter.notifyDataSetChanged();
                } else {
                    // Handle case if user has no posts yet
                    mObjects.add(ProfileConstants.noPosts);
                    mMultiAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}