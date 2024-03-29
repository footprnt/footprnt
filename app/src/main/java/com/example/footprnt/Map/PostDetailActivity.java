/*
 * PostDetailActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.content.Intent;
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
        mBookmark = findViewById(R.id.ivSaveBtnPost);
        Bundle bundle = getIntent().getExtras();
        mPost = (Post) bundle.getSerializable(AppConstants.Post);
        Boolean hideView = (Boolean) bundle.getSerializable(AppConstants.hideView);
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
                                Toast.makeText(PostDetailActivity.this, getResources().getString(R.string.saved_post), Toast.LENGTH_SHORT).show();
                                mBookmark.setImageResource(R.drawable.ic_save_check_filled);
                            }
                        });
                    } else {
                        // Post already saved - unsaved
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.savedPost);
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
                                            Toast.makeText(PostDetailActivity.this, getResources().getString(R.string.unsaved_post), Toast.LENGTH_SHORT).show();
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.savedPost);
        query.whereEqualTo(AppConstants.user, ParseUser.getCurrentUser()).whereEqualTo(AppConstants.post, mPost);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (!objects.isEmpty()) {
                        mIsPostSaved = true;
                        mBookmark.setImageResource(R.drawable.ic_save_check_filled);
                    } else {
                        mIsPostSaved = false;
                        mBookmark.setImageResource(R.drawable.ic_save_check);
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, getResources().getString(R.string.error_query_saved_posts), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Share a post with someone
     *
     * @param view
     */
    public void shareIntent(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        // TODO: maybe change the sharing format?
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s\n%s", mPost.getTitle(), mPost.getDescription()));
//        if (mPost.getImage() != null && mPost.getImage().getUrl().length() > 0) {
//            try {
//                URL url = new URL(mPost.getImage().getUrl());
//                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                OutputStream out = null;
//                File file = AppUtil.getPhotoFileUri(this, AppConstants.photoFileNameShare);
//                try {
//                    out = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, AppConstants.captureImageQuality, out);
//                    out.flush();
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Uri bmpUri = FileProvider.getUriForFile(this, AppConstants.fileProvider, file);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        shareIntent.setType(AppConstants.photoType);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(shareIntent, AppConstants.send), 0);
    }
}
