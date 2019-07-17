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
import com.example.footprnt.Profile.Util.LocationUtil;
import com.example.footprnt.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    public final static String TAG = "ProfileFragment";  // tag for logging from this activity
    // For user profile info view:
    CircleImageView ivProfileImage;
    TextView tvEditProfile;

    // For stats view:
    StatListAdapter statAdapter;  // Adapter for stats
    ListView lvStats;
    HashMap<String, Integer> cities;
    HashMap<String, Integer> countries;
    HashMap<String, Integer> continents;
    ArrayList<HashMap<String, Integer>> statsList;

    // For post feed:
    ArrayList<Post> posts;  // list of current user posts
    RecyclerView rvPosts;
    PostAdapter postAdapter;
    SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setUpLogOutButton(v);

        // For profile image:
        ivProfileImage = v.findViewById(R.id.ivProfileImageMain);
        if (ParseUser.getCurrentUser().getParseFile("profileImg") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("profileImg").getUrl()).into(ivProfileImage);
        } else {
            Glide.with(getContext()).load(R.drawable.ic_user).into(ivProfileImage);
        }

        tvEditProfile = v.findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), UserSettings.class);
                startActivity(it);
            }
        });


        // Populate stat maps and get posts
        posts = new ArrayList<>();
        cities = new HashMap<>();
        countries = new HashMap<>();
        continents = new HashMap<>();
        statsList = new ArrayList<>();


        // Call getPosts() first
        getPosts(
                new Handler(),
                new CalculateStatsCallback() {
                    @Override
                    public void onDone(ArrayList<HashMap<String, Integer>> stats) {
                        statsList.add(cities);
                        statsList.add(countries);
                        statsList.add(continents);
                        updateStats();
                    }
                });

        // Refresh listener for post feed and update stats
        swipeContainer = v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        postAdapter = new PostAdapter(posts);
        rvPosts = v.findViewById(R.id.rvFeed);
        rvPosts.setLayoutManager(new GridLayoutManager(v.getContext(), 3));
        rvPosts.setAdapter(postAdapter);

        // For stat view
        lvStats = v.findViewById(R.id.lvStatKey);
        statAdapter = new StatListAdapter(statsList, v.getContext());
        lvStats.setAdapter(statAdapter);

        return v;
    }

    interface CalculateStatsCallback {
        void onDone(ArrayList<HashMap<String, Integer>> statsList);
    }

//
//    interface CustomStatInterface {
//        String getName();
//
//        String calculateStat(String data);
//    }


    private void setUpLogOutButton(final View v){
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
                .withUser();
        postsQuery.addDescendingOrder("createdAt");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = objects.get(i);
                        // Only add current user's posts
                        if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            posts.add(post);
                            postAdapter.notifyItemInserted(posts.size() - 1);
                            // Get post stats and update user stats
                            LocationUtil helper = new LocationUtil();
                            ArrayList<String> postStats = helper.getAddress(getContext(), post.getLocation());
                            // Fill HashMaps
                            // Cities
                            if (!cities.containsKey(postStats.get(0))) {
                                // User first visit
                                cities.put(postStats.get(0), 1);
                            } else {
                                // User already visited, increment count
                                cities.put(postStats.get(0), cities.get(postStats.get(0)) + 1);
                            }
                            // Countries
                            if (!countries.containsKey(postStats.get(1))) {
                                countries.put(postStats.get(1), 1);
                            } else {
                                countries.put(postStats.get(1), countries.get(postStats.get(1)) + 1);
                            }
                            // Continents
                            if (!continents.containsKey(postStats.get(2))) {
                                continents.put(postStats.get(2), 1);
                            } else {
                                continents.put(postStats.get(2), continents.get(postStats.get(2)) + 1);
                            }
                        }
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    logError("Error querying posts", e, true);
                }

                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onDone(statsList);
                            }
                        });
            }
        });
    }

    private void updateStats() {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for (int i = 0; i < statsList.size(); i++) {  // Loop through number of stats we're tracking
            HashMap<String, Integer> innerList = statsList.get(i);
            ArrayList<String> toAdd = new ArrayList<>();
            toAdd.add(String.format("%s", innerList.size()));
            res.add(toAdd);
        }
        final ParseUser user = ParseUser.getCurrentUser();
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
