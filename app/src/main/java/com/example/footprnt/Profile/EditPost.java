/*
 * EditPost.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Util.ProfileConstants;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Edit post pop up window for ProfileFragment. Allows user to edit clicked on post from RV.
 *
 * @author Clarisa Leu-Rodriguez
 */
public class EditPost extends AppCompatActivity {

    ImageView mIvPicture;
    TextView mTvDate;
    EditText mEtDescription;
    EditText mEtTitle;
    EditText mEtLocation;
    FloatingActionButton mBtnDelete;
    FloatingActionButton mBtnSave;
    Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_post);

        // Get post from serializable extras
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mPost = (Post) bundle.getSerializable(Post.class.getSimpleName());
        final int position = (int) bundle.getSerializable(AppConstants.position);

        // Set views
        mIvPicture = findViewById(R.id.ivPicture);
        mTvDate = findViewById(R.id.tvPostedAt);
        mEtDescription = findViewById(R.id.etDescription);
        mEtTitle = findViewById(R.id.etTitle);
        mEtLocation = findViewById(R.id.etLocation);
        mBtnDelete = findViewById(R.id.cancel);
        mBtnSave = findViewById(R.id.save);

        setViews();

        // Set view window to be smaller for pop up effect
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width * ProfileConstants.widthRatio), (int) (height * ProfileConstants.heightRatio));

        // Delete post
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.post);
                query.whereEqualTo(AppConstants.objectId, mPost.getObjectId());
                query.getInBackground(mPost.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(final ParseObject object, ParseException e) {
                        if (e == null) {
                            mPost.getUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    object.deleteInBackground();
                                    Intent it = new Intent();
                                    it.putExtra(AppConstants.position, position);
                                    setResult(AppConstants.DELETE_POST_FROM_PROFILE, it);
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPost.setTitle(mEtTitle.getText().toString());
                mPost.setDescription(mEtDescription.getText().toString());
                mPost.getUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent it = new Intent();
                        it.putExtra(AppConstants.position, position);
                        setResult(AppConstants.UPDATE_POST_FROM_PROFILE, it);
                        finish();
                    }
                });
            }
        });
    }

    /**
     * Helper method to set the views on the edit post page
     */
    private void setViews() {
        String date = AppUtil.getRelativeTimeAgo(mPost.getCreatedAt().toString());
        mTvDate.setText(date);
        mEtTitle.setText(mPost.getTitle());
        mEtDescription.setText(mPost.getDescription());
        // Check for null values for either city, country, or continent
        StringBuilder sb = new StringBuilder();
        if (mPost.getCity() != null) {
            sb.append(mPost.getCity()).append(",");
        }
        if (mPost.getCountry() != null) {
            sb.append(mPost.getCountry()).append(",");
        }
        if (mPost.getContinent() != null) {
            sb.append(mPost.getContinent());
        }
        mEtLocation.setText(sb);
        if (mPost.getImage() != null) {
            mIvPicture.setVisibility(View.VISIBLE);
            Glide.with(this).load(mPost.getImage().getUrl()).into(mIvPicture);
        } else {
            mIvPicture.setVisibility(View.GONE);
        }
    }
}