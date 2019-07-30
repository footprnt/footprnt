/*
 * ProfileFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.footprnt.Database.PostDatabase;
import com.example.footprnt.Database.Repository.PostRepository;
import com.example.footprnt.LoginActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Models.PostWrapper;
import com.example.footprnt.Profile.Adapters.MultiViewAdapter;
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
 * Created by Clarisa Leu 2019
 */
public class ProfileFragment extends Fragment {

    public final static String TAG = ProfileFragment.class.getName();  // tag for logging from this activity
    final ParseUser user = ParseUser.getCurrentUser();

    // For database:
    PostRepository mPostRepository;

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
    ArrayList<HashMap<String, Integer>> mStats;  // Stats to be passed to adapter




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mLayout = v.findViewById(R.id.rvPosts);

        // For database:
        mPostWrappers = new ArrayList<>();
        mPostRepository = new PostRepository(getActivity().getApplicationContext());

        setUpToolbar(v);

        // Populate stat maps and get posts
        mObjects = new ArrayList<>();
        mCities = new HashMap<>();
        mCountries = new HashMap<>();
        mContinents = new HashMap<>();
        mPosts = new ArrayList<>();
        mStats = new ArrayList<>();

        // Get posts from DB or Network
        // If the data base size is null - try to get posts on first try
        PostDatabase postDatabase = mPostRepository.getPostDatabase();
        if (postDatabase.daoAccess().fetchAllPosts().getValue()==null) {
            getPosts();
        } else {
            // Otherwise - load the posts from the database
            getPostsDB();
        }


        // For post feed view:
        mMultiAdapter = new MultiViewAdapter(getContext(), mObjects);
        mLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        mLayout.setAdapter(mMultiAdapter);

        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        PostDatabase.destroyInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppConstants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE) {
            mMultiAdapter.notifyItemChanged(0);
        }

        // Delete post
        if (resultCode == AppConstants.DELETE_POST_FROM_PROFILE) {
            int position = data.getIntExtra(AppConstants.position, 0);
            mObjects.remove(position);
            mMultiAdapter.notifyItemChanged(position);
        }
        // TODO: fix so UI updates
        // Save post
        if (resultCode == AppConstants.UPDATE_POST_FROM_PROFILE) {
            int position = data.getIntExtra(AppConstants.position, 0);
            mMultiAdapter.notifyItemChanged(position);
        }
    }

    private void setUpToolbar(final View v) {
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
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    /**
     * Method to load the posts from the data base vs network
     */
    private void getPostsDB() {
        mPostWrappers.clear();
        mPostRepository.getPosts().observe(getActivity(), new Observer<List<PostWrapper>>() {
            @Override
            public void onChanged(@Nullable List<PostWrapper> postWrappers) {
                for (PostWrapper post : postWrappers) {
                    mPostWrappers.add(post);
                }
                if(mPostWrappers.size()>0 && mPostWrappers!=null) {
                    mObjects.addAll(mPostWrappers);
                    mMultiAdapter.notifyDataSetChanged();
                }
//                } else {
//                    // Handle case if user has no posts yet
//                    mObjects.add("No posts!");
//                    mMultiAdapter.notifyDataSetChanged();
//                }
            }
        });
       // List<PostWrapper> posts = mPostRepository.getPosts().getValue();
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
                        // Wrap post and add to repo & DB if not already done
                        final PostWrapper postWrapper = new PostWrapper(post);
                        LiveData<PostWrapper> postWrap = mPostRepository.getPost(postWrapper.getObjectId());
                        PostWrapper postWrapperCompare = postWrap.getValue();
                        if (postWrapperCompare == null) {
                            mPostRepository.insertPost(new PostWrapper(post));
                        }

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

                mObjects.add(user);
                mStats.add(mCities);
                mStats.add(mCountries);
                mStats.add(mContinents);
                mObjects.add(mStats);
                mMultiAdapter.notifyDataSetChanged();

                if (mPosts.size() > 0 && mPosts != null) {
                    mObjects.addAll(mPosts);
                    mMultiAdapter.notifyDataSetChanged();
                } else {
                    // Handle case if user has no posts yet
                    mObjects.add("No posts!");
                    mMultiAdapter.notifyDataSetChanged();
                }

            }
        });
    }
}
