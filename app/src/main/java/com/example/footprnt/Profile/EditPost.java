/*
 * EditPost.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.Util;
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
    Button mBtnDelete;
    Button mBtnSave;
    Post post;
    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_post);

        // Get post from serializable extras
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable(Post.class.getSimpleName());
        final int position = (int) bundle.getSerializable("position");

        // Set views
        mIvPicture = findViewById(R.id.ivPicture);
        mTvDate = findViewById(R.id.tvPostedAt);
        mEtDescription = findViewById(R.id.etDescription);
        mEtTitle = findViewById(R.id.etTitle);
        mEtLocation = findViewById(R.id.etLocation);
        mBtnDelete = findViewById(R.id.btnDelete);
        mBtnSave = findViewById(R.id.btnSave);

        setViews();

        // Set view window to be smaller for pop up effect
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));


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
                                    Intent it = new Intent();
                                    it.putExtra("position", position);
                                    setResult(302, it);
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
                post.setTitle(mEtTitle.getText().toString());
                post.setDescription(mEtDescription.getText().toString());
                post.getUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent it = new Intent();
                        it.putExtra("position", position);
                        setResult(301, it);
                        finish();
                    }
                });

            }
        });

    }


    private void setViews(){
        String date = util.getRelativeTimeAgo(post.getCreatedAt().toString());
        mTvDate.setText(date);
        mEtTitle.setText(post.getTitle());
        mEtDescription.setText(post.getDescription());

        StringBuilder sb = new StringBuilder();
        if(post.getCity()!=null){
            sb.append(post.getCity()+",");
        }
        if(post.getCountry()!=null){
            sb.append(post.getCountry()+",");
        }

        if(post.getContinent()!=null){
            sb.append(post.getContinent());
        }

        mEtLocation.setText(sb);

        if (post.getImage() != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(mIvPicture);
        }
    }
}
