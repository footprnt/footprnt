/*
 * SavedPosts.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.Profile.Adapters.SavedPostsAdapter;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Saved Posts activity
 *
 * @author Clarisa Leu
 */
public class SavedPosts extends AppCompatActivity {

    private final String TAG = SavedPost.class.getSimpleName();
    ImageView mBackButton;
    RecyclerView mRvSavedPosts;
    ArrayList<SavedPost> mSavedPosts;
    SavedPostsAdapter mSavedPostsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        // Set views
        mSavedPosts = new ArrayList<>();
        mBackButton = findViewById(R.id.ivBack);
        mRvSavedPosts = findViewById(R.id.rvSavedPosts);

        // Get saved posts
        getSavedPosts();
        mSavedPostsAdapter = new SavedPostsAdapter(mSavedPosts, this);
        mRvSavedPosts.setLayoutManager(new LinearLayoutManager(this));
        mRvSavedPosts.setAdapter(mSavedPostsAdapter);

    }

    /**
     * Helper method to query saved posts
     */
    private void getSavedPosts() {
        final SavedPost.Query query = new SavedPost.Query();
        query
                .getTop()
                .withUser()
                .whereEqualTo(AppConstants.user, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<SavedPost>() {
            @Override
            public void done(List<SavedPost> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final SavedPost savedPost = objects.get(i);
                        mSavedPosts.add(savedPost);
                    }
                } else {
                    AppUtil.logError(SavedPosts.this, TAG, "Error querying posts", e, true);
                }
            }
        });
    }
}
