/*
 * Copyright 2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Displays posts to profile page
 * Created by Clarisa Leu 2019
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String TAG = "PostAdapter";
    ArrayList<Post> mPosts;    // list of posts
    Context mContext;          // context for rendering

    public PostAdapter(ArrayList<Post> posts) {
        this.mPosts = posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_post_grid, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        if (post.getImage() != null) {
            Glide.with(mContext).load(post.getImage().getUrl()).centerCrop().placeholder(R.drawable.ic_add_photo).error(R.drawable.ic_add_photo).into(holder.ivPicture);
        } else {
            Glide.with(mContext).load(R.drawable.ic_add_photo).placeholder(R.drawable.ic_add_photo).error(R.drawable.ic_add_photo).into(holder.ivPicture);
        }

        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog settingsDialog = new Dialog(mContext);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                settingsDialog.setContentView(inflater.inflate(R.layout.item_post_grid, null));
                settingsDialog.show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
        }
    }
}