/*
 * Copyright 2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.DateHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Edit post pop up window for ProfileFragment. Allows user to edit clicked on post from RV.
 * Created by Clarisa Leu 2019
 */
public class EditPost extends Activity {
    ImageView mIvPicture;
    TextView mTvDate;
    EditText mEtDescription;
    EditText mEtTitle;
    EditText mEtLocation;
    Button mBtnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_post);
        DateHelper dateHelper = new DateHelper();

        // Get post from serializable extras
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Post post= (Post)bundle.getSerializable(Post.class.getSimpleName());
        // Set views
        mIvPicture = findViewById(R.id.ivPicture);
        mTvDate = findViewById(R.id.tvPostedAt);
        mEtDescription = findViewById(R.id.etDescription);
        mEtTitle = findViewById(R.id.etTitle);
        mEtLocation = findViewById(R.id.etLocation);
        mBtnDelete = findViewById(R.id.btnDelete);

        String date = dateHelper.getRelativeTimeAgo(post.getCreatedAt().toString());
        mTvDate.setText(date);
        mEtTitle.setText(post.getTitle());
        mEtDescription.setText(post.getDescription());
        String city = post.getCity();
        String country = post.getCountry();
        String continent = post.getContinent();
        mEtLocation.setText(city+", "+country+", "+continent);

        //TODO: Add save/delete button
        if (post.getImage() != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(mIvPicture);
        } else {
            Glide.with(this).load(R.drawable.ic_add_photo).into(mIvPicture);
        }

        // Set view window to be smaller for pop up effect
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width*.8), (int)(height*.7));


        // Delete post
        // TODO: fix so it updates UI
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
                query.whereEqualTo("objectId", post.getObjectId());
                query.getInBackground(post.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(final ParseObject object, ParseException e) {
                        if (e == null) {
                            post.getUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    object.deleteInBackground();
                                    finish();
                                }
                            });
                        }
                    }
                });

            }
        });

    }

}
