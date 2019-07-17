package com.example.footprnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.footprnt.Map.PostAdapter;
import com.example.footprnt.Models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public final static String TAG = "ProfileFragment";  // tag for logging from this activity
    // For stats view:
    ArrayAdapter<String> statAdapter;  // Adapter for stats
    ArrayList<String> stats;
    ListView lvStats;


    // For post feed:
    ArrayList<Post> posts;  // list of current user posts
    RecyclerView rvPosts;    // the RecylerView
    PostAdapter postAdapter;  // post adapter
    SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

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
                popup.show();  //showing popup menu
            }
        });

        // For stat view
        lvStats = v.findViewById(R.id.lvStatKey);
        stats = new ArrayList<>();
        getStats();
        statAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, stats);
        lvStats.setAdapter(statAdapter);

        // For post feed view:
        posts = new ArrayList<>();
        getPosts();
        postAdapter = new PostAdapter(posts);
        rvPosts = v.findViewById(R.id.rvFeed);
        rvPosts.setLayoutManager(new LinearLayoutManager(v.getContext()));
        rvPosts.setAdapter(postAdapter);

        // Refresh listener for post feed
        swipeContainer = v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });



        return v;
    }

    // Get posts
    private void getPosts(){
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
                        Post post = (Post) objects.get(i);
                        posts.add(post);
                        postAdapter.notifyItemInserted(posts.size()-1);
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    logError("Error querying posts", e, true);
                }
            }
        });
    }

    private void getStats(){
        final ParseUser user = ParseUser.getCurrentUser();
        ArrayList<ArrayList<String>> list = (ArrayList<ArrayList<String>>) user.get("stats");
        if(list!=null){
            for(ArrayList<String> stat: list){
                String statKey=null;
                String statNum;
                for(int i = 0 ; i < stat.size(); i++){
                    if(i%2==0) {
                        // Get key
                        statKey = stat.get(i);
                    } else{
                        // Get value
                        statNum = stat.get(i);
                        stats.add(statKey+"  "+statNum);
                    }

                }
            }
        }
    }


    // TODO: implement on click for item to allow user to edit post
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Helper method to handle errors, log them, and alert user
    private void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);  // Always log the error
        // alert the user to avoid silent errors
        if(alertUser){
            // Show a long toast with the error message
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
