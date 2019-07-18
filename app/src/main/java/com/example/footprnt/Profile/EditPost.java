/*
 * Copyright 2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Util.Util;
import com.example.footprnt.R;

import java.util.ArrayList;

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
    Util util= new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_post);

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

        String date = util.getRelativeTimeAgo(post.getCreatedAt().toString());
        mTvDate.setText(date);
        mEtTitle.setText(post.getTitle());
        mEtDescription.setText(post.getDescription());
        ArrayList<String> location = util.getAddress(this, post.getLocation());
        mEtLocation.setText(location.get(0)+", "+location.get(1));
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
    }

}
