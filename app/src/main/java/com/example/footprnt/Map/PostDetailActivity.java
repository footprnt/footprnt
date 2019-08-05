/*
 * PostDetailActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprnt.Map.Util.PostAdapter;
import com.example.footprnt.Map.Util.UiUtil;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Displays extended details of a post
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class PostDetailActivity extends AppCompatActivity {

    Boolean mIsPostSaved;
    Post mPost;
    ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        mPost = (Post) bundle.getSerializable(Post.class.getSimpleName());
        mUser = mPost.getUser();
        Boolean privacy;
        Object privacySetting = mPost.getUser().get(AppConstants.privacy);
        if (privacySetting == null) {
            privacy = false;
        } else {
            privacy = (Boolean) privacySetting;
        }
        ImageView mIvBackArrow = findViewById(R.id.ivBack2);
        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PostAdapter.ViewHolder vh = new PostAdapter.ViewHolder(findViewById(R.id.constraintlayout));
        UiUtil.setPostText(mPost, vh, this, privacy);
        UiUtil.setPostImages(mPost, vh, this, privacy);

        // Save Post
        checkIfSaved();
        final ImageView mBookmark = findViewById(R.id.ivSave);
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsPostSaved) {
                    // Post not saved yet - save
                    SavedPost savedPost = new SavedPost();
                    savedPost.setPost(mPost);
                    savedPost.setUser(ParseUser.getCurrentUser());
                    savedPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(PostDetailActivity.this, "Saved Post", Toast.LENGTH_SHORT).show();
                            mBookmark.setImageResource(R.drawable.ic_save_check_filled);
                        }
                    });
                    mIsPostSaved = true;
                } else {
                    // Post already saved - unsaved
                    deleteSavedPost();
                    mIsPostSaved = false;
                    mBookmark.setImageResource(R.drawable.ic_save_check);
                    Toast.makeText(PostDetailActivity.this, "Unsaved Post", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Check if post is already saved
    private void checkIfSaved() {
        ParseQuery<SavedPost> query = ParseQuery.getQuery(SavedPost.class);
        query.whereEqualTo(AppConstants.user, mUser.getObjectId());
        query.whereEqualTo(AppConstants.post, mPost.getObjectId());
        query.findInBackground(new FindCallback<SavedPost>() {
            @Override
            public void done(List<SavedPost> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        mIsPostSaved = true;
                    } else {
                        mIsPostSaved = false;
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Error querying saved posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteSavedPost(){
        ParseQuery<SavedPost> query = ParseQuery.getQuery(SavedPost.class);
        query.whereEqualTo(AppConstants.user, mUser.getObjectId());
        query.whereEqualTo(AppConstants.post, mPost.getObjectId());
        query.findInBackground(new FindCallback<SavedPost>() {
            @Override
            public void done(List<SavedPost> objects, ParseException e) {
                for(SavedPost savedPost:objects){
                    savedPost.deleteInBackground();
                    savedPost.saveInBackground();
                }
            }
        });
    }
}
