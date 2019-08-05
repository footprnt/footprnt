/*
 * SavedPostDetails.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
 * Saved Post Details Activity
 *
 * @author Clarisa Leu
 */
public class SavedPostDetails extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        final Post post = (Post) bundle.getSerializable(Post.class.getSimpleName());
        Boolean privacy;
        assert post != null;
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
                        Toast.makeText(SavedPostDetails.this, "Unsaved Post", Toast.LENGTH_SHORT).show();
                        // TODO: set tint, update UI
                        setResult(AppConstants.SAVED_POST_DETAILS_FROM_PROFILE);
                        finish();
                    }
                });
            }
        });


    }
}
