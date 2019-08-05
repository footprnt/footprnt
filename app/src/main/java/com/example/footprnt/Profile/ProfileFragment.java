/*
 * ProfileFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 */
public class ProfileFragment extends Fragment {

    public final static String TAG = ProfileFragment.class.getName();
    final ParseUser mUser = ParseUser.getCurrentUser();

    // For anonymous user:
    private boolean mIsPrivate;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mLayout = v.findViewById(R.id.rvPosts);
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
        return v;
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
        // TODO: fix so the stat chart updates if user removes unique city, continent, or country (i.e. decrement)
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
        // TODO: fix so UI updates
        // Save post
        if (resultCode == AppConstants.UPDATE_POST_FROM_PROFILE) {
            int position = data.getIntExtra(AppConstants.position, 0);
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
                        if (item.getItemId() == R.id.logout) {
                            // Logout
                            if (AppUtil.haveNetworkConnection(getActivity().getApplicationContext())) {
                                // Destroy instances of DB's on logout
                                StatDatabase.getStatDatabase(getContext()).clearAllTables();
                                PostDatabase.getPostDatabase(getContext()).clearAllTables();
                                UserDatabase.getUserDatabase(getContext()).clearAllTables();
                                ParseUser.logOut();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "No network connection, try again later", Toast.LENGTH_SHORT).show();
                            }
                        } else if (item.getItemId() == R.id.savedPosts) {
                            // Saved Posts
                            Intent it = new Intent(getContext(), SavedPosts.class);
                            startActivity(it);
                        } else if (item.getItemId() == R.id.savedActivities) {
                            // Saved Activities
                            Intent it = new Intent(getContext(), SavedActivities.class);
                            startActivity(it);
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
        postsQuery
                .getTop()
                .withUser()
                .whereEqualTo(AppConstants.user, ParseUser.getCurrentUser());
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
                    AppUtil.logError(getContext(), TAG, "Error querying posts", e, true);
                }
                mObjects.add(mUser);
                mStats.add(mCities);
                mStats.add(mCountries);
                mStats.add(mContinents);
                mObjects.add(mStats);
                mStatRepository.insertStat(new StatWrapper(mStats, mUser));
                mMultiAdapter.notifyDataSetChanged();
                if (mPosts.size() > 0 && mPosts != null) {
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