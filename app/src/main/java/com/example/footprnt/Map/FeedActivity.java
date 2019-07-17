package com.example.footprnt.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends Activity {
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    RecyclerView rvPosts;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        posts = new ArrayList<>();
        getPosts();
        postAdapter = new PostAdapter(posts);
        rvPosts = findViewById(R.id.rvPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setAdapter(postAdapter);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4,
                R.color.refresh_progress_5);
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
                    e.printStackTrace();
                }
            }
        });
    }


    public void fetchTimelineAsync(int page) {
        /*
        Handles refreshing
         */
        postAdapter.clear();
        getPosts();
        swipeContainer.setRefreshing(false);
    }
}
