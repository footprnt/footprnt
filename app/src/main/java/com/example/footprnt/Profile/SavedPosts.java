/*
 * SavedPosts.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        // Set views
        mBackButton = findViewById(R.id.ivBack);
        mRvSavedPosts = findViewById(R.id.rvSavedPosts);

        // Set adapter
        mSavedPosts = new ArrayList<>();
        mSavedPostsAdapter = new SavedPostsAdapter(mSavedPosts, this);

        // Get saved posts
        // TODO: implement database for saved posts
        if (AppUtil.haveNetworkConnection(getApplicationContext())) {
            getSavedPosts();
            mRvSavedPosts.setLayoutManager(new LinearLayoutManager(this));
            mRvSavedPosts.setAdapter(mSavedPostsAdapter);
        } else {
            // Display no network connection message
            // TODO: fix
            View view = mRvSavedPosts;
            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                int index = parent.indexOfChild(view);
                parent.removeView(view);
                view = mInflater.inflate(R.layout.item_no_network_connection_profile, parent, false);
                parent.addView(view, index);
            }
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                        mSavedPostsAdapter.notifyItemInserted(mSavedPosts.size() - 1);
                    }
                } else {
                    AppUtil.logError(SavedPosts.this, TAG, "Error querying saved posts", e, true);
                }
            }
        });
    }
}
