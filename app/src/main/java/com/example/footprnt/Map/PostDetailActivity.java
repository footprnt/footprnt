/*
 * PostDetailActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.content.Intent;
import android.net.Uri;
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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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
    ImageView mBookmark;
    Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        mBookmark = findViewById(R.id.ivSave);
        Bundle bundle = getIntent().getExtras();
        mPost = (Post) bundle.getSerializable(Post.class.getSimpleName());
        Boolean hideView = (Boolean) bundle.getSerializable("hideView");
        Boolean privacy = null;
        Object privacySetting = null;
        try {
            privacySetting = mPost.getUser().fetchIfNeeded().get(AppConstants.privacy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        if (!hideView) {
            checkIfSaved();
            mBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsPostSaved) {
                        // Post not saved yet - save
                        SavedPost savedPost = new SavedPost();
                        savedPost.setPost(mPost);
                        savedPost.setUser(ParseUser.getCurrentUser());
                        savedPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                mIsPostSaved = true;
                                Toast.makeText(PostDetailActivity.this, "Saved Post", Toast.LENGTH_SHORT).show();
                                mBookmark.setImageResource(R.drawable.ic_save_check_filled);
                            }
                        });
                    } else {
                        // Post already saved - unsaved
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("SavedPost");
                        query.whereEqualTo(AppConstants.user, ParseUser.getCurrentUser()).whereEqualTo(AppConstants.post, mPost);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ParseObject.deleteAllInBackground(objects, new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            mIsPostSaved = false;
                                            mBookmark.setImageResource(R.drawable.ic_save_check);
                                            Toast.makeText(PostDetailActivity.this, "Unsaved Post", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        } else {
            mBookmark.setVisibility(View.INVISIBLE);
        }
    }

    // Check if post is already saved
    private void checkIfSaved() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SavedPost");
        query.whereEqualTo(AppConstants.user, ParseUser.getCurrentUser()).whereEqualTo(AppConstants.post, mPost);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        mIsPostSaved = true;
                        mBookmark.setImageResource(R.drawable.ic_save_check_filled);
                    } else {
                        mIsPostSaved = false;
                        mBookmark.setImageResource(R.drawable.ic_save_check);
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Error querying saved posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Share a post with someone
     * @param view
     */
    public void shareIntent(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mPost.getTitle() + "\n" + mPost.getDescription());
        if (mPost.getImage() != null && mPost.getImage().getUrl().length() > 0){
            Uri imageUri = Uri.parse(mPost.getImage().getUrl());
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(shareIntent, "send"), 0);
    }
}
