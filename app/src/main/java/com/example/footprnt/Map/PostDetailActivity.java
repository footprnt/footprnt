/*
 * PostDetailActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.footprnt.Map.Util.MapUtil;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;

/**
 * Displays extended details of a post
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        final Post post = (Post) bundle.getSerializable(Post.class.getSimpleName());
        ImageView mIvBackArrow = findViewById(R.id.ivBack2);
        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PostAdapter.ViewHolder vh = new PostAdapter.ViewHolder(findViewById(R.id.constraintlayout));
        MapUtil.setPostText(post, vh, this);
        MapUtil.setPostImages(post, vh, this);
    }
}
