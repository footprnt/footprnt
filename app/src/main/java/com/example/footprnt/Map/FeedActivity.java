package com.example.footprnt.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.footprnt.Map.Util.Constants;
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

    private boolean CULTURE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mLat = getIntent().getExtras().getDouble("latitude");
        mLong = getIntent().getExtras().getDouble("longitude");
        mPosts = new ArrayList<>();
        getPosts(Constants.POST_RADIUS);
        mPostAdapter = new PostAdapter(mPosts);
        mPostsView = findViewById(R.id.rvPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPostsView.setLayoutManager(layoutManager);
        mPostsView.setAdapter(mPostAdapter);
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
     * Queries posts within certain mile radius
     */
    private void getPosts(int distance) {
        final Post.Query postsQuery = new Post.Query();
        postsQuery
                .getTop()
                .withUser()
                .withinPoint(new ParseGeoPoint(mLat, mLong), distance);
        postsQuery.addDescendingOrder("createdAt");
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Post post = (Post) objects.get(i);
                        mPosts.add(post);
                        mPostAdapter.notifyItemInserted(mPosts.size()-1);
                    }
                    mSwipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handles swipe refresh
     */
    public void fetchTimelineAsync() {
        mPostAdapter.clear();
        getPosts(Constants.POST_RADIUS);
        mSwipeContainer.setRefreshing(false);
    }
}
