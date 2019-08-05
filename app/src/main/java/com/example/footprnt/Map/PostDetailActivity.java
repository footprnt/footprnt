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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Displays extended details of a post
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class PostDetailActivity extends AppCompatActivity {

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        post = (Post) bundle.getSerializable(Post.class.getSimpleName());
        Boolean privacy;
        Object privacySetting = post.getUser().get(AppConstants.privacy);
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
        UiUtil.setPostText(post, vh, this, privacy);
        UiUtil.setPostImages(post, vh, this, privacy);

        // Save Post
        final ImageView mBookmark = findViewById(R.id.ivSave);
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedPost savedPost = new SavedPost();
                savedPost.setPost(post);
                savedPost.setUser(ParseUser.getCurrentUser());
                savedPost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(PostDetailActivity.this, "Saved Post", Toast.LENGTH_LONG).show();
                        // TODO: set tint
                    }
                });
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n" + post.getDescription());
        if (post.getImage() != null && post.getImage().getUrl().length() > 0){
            Uri imageUri = Uri.parse(post.getImage().getUrl());
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(shareIntent, "send"), 0);
    }
}
