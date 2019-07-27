/*
 * FeedActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.footprnt.Map.Util.MapConstants;
import com.example.footprnt.Map.Util.PostAdapter;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all post feed activities
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class FeedActivity extends Activity {

    private ArrayList<Post> mPosts;
    private PostAdapter mPostAdapter;
    private RecyclerView mPostsView;
    private SwipeRefreshLayout mSwipeContainer;
    private double mLat;
    private double mLong;
    private View mMenu;
    private TextView mNoPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initialization();
        handleMenuAction();
        handleSwipeRefresh();
    }

    /**
     * Initializes all variables
     */
    private void initialization(){
        mLat = getIntent().getExtras().getDouble("latitude");
        mLong = getIntent().getExtras().getDouble("longitude");
        mPosts = new ArrayList<>();
        final Post.Query postsQuery = new Post.Query();
        postsQuery
                .getTop()
                .withUser()
                .withinPoint(new ParseGeoPoint(mLat, mLong), MapConstants.POST_RADIUS);
        mPostAdapter = new PostAdapter(mPosts);
        mPostsView = findViewById(R.id.rvPosts);
        mNoPosts = findViewById(R.id.noPosts);
        mNoPosts.setVisibility(View.INVISIBLE);
        getPosts(postsQuery);
        mMenu = findViewById(R.id.dropdown);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPostsView.setLayoutManager(layoutManager);
        mPostsView.setAdapter(mPostAdapter);
    }

    /**
     * Queries posts within certain mile radius
     */
    private void getPosts(Post.Query postsQuery) {
        mPostAdapter.clear();
        postsQuery.addDescendingOrder("createdAt");
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Post post = (Post) objects.get(i);
                        mPosts.add(post);
                        mPostAdapter.notifyItemInserted(mPosts.size() - 1);
                    }
                    if (mPosts.size() == 0){
                        mNoPosts.setVisibility(View.VISIBLE);
                    } else {
                        mNoPosts.setVisibility(View.INVISIBLE);
                    }
                    mSwipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handles swipe refresh action
     */
    public void handleSwipeRefresh(){
        mSwipeContainer = findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
        mSwipeContainer.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4,
                R.color.refresh_progress_5);
    }

    /**
     * Queries posts on swipe refresh
     */
    public void fetchTimelineAsync() {
        mPostAdapter.clear();
        final Post.Query postsQuery = new Post.Query();
        postsQuery
                .getTop()
                .withUser()
                .withinPoint(new ParseGeoPoint(mLat, mLong), MapConstants.POST_RADIUS);
        getPosts(postsQuery);
        mSwipeContainer.setRefreshing(false);
    }

    /**
     * Handles applying tags and filter menu action
     */
    public void handleMenuAction(){
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(FeedActivity.this, mMenu);
                popup.getMenuInflater().inflate(R.menu.popup_tags, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        final Post.Query postsQuery = new Post.Query();
                        postsQuery
                                .getTop()
                                .withUser()
                                .withinPoint(new ParseGeoPoint(mLat, mLong), MapConstants.POST_RADIUS);
                        if (item.getItemId() == R.id.all){
                            List<String> tags_to_check = new ArrayList();
                            getPosts(postsQuery);
                        }
                        if (item.getItemId() == R.id.culture){
                            List<String> tags_to_check = new ArrayList();
                            tags_to_check.add(MapConstants.culture);
                            postsQuery.whereContainsAll(Post.KEY_TAGS, tags_to_check);
                            getPosts(postsQuery);
                        }
                        if (item.getItemId() == R.id.food){
                            List<String> tags_to_check = new ArrayList();
                            tags_to_check.add(MapConstants.food);
                            postsQuery.whereContainsAll(Post.KEY_TAGS, tags_to_check);
                            getPosts(postsQuery);
                        }
                        if (item.getItemId() == R.id.travel){
                            List<String> tags_to_check = new ArrayList();
                            tags_to_check.add(MapConstants.travel);
                            postsQuery.whereContainsAll(Post.KEY_TAGS, tags_to_check);
                            getPosts(postsQuery);
                        }
                        if (item.getItemId() == R.id.fashion){
                            List<String> tags_to_check = new ArrayList();
                            tags_to_check.add(MapConstants.fashion);
                            postsQuery.whereContainsAll(Post.KEY_TAGS, tags_to_check);
                            getPosts(postsQuery);
                        }
                        if (item.getItemId() == R.id.nature){
                            List<String> tags_to_check = new ArrayList();
                            tags_to_check.add(MapConstants.nature);
                            postsQuery.whereContainsAll(Post.KEY_TAGS, tags_to_check);
                            getPosts(postsQuery);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}
