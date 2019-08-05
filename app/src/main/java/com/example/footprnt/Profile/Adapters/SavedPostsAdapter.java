/*
 * SavedPostsAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Map.PostDetailActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Adapts saved posts to profile recycler view
 *
 * @author Clarisa Leu
 */
public class SavedPostsAdapter extends RecyclerView.Adapter<SavedPostsAdapter.ViewHolder> {

    static ArrayList<SavedPost> mPosts;  // list of posts
    static Context mContext;  // context for rendering

    public SavedPostsAdapter(ArrayList<SavedPost> posts, Context context) {
        this.mPosts = posts;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_post_card, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mProgressBar.setVisibility(View.VISIBLE);
        final SavedPost savedPost = mPosts.get(position);
        savedPost.getPost().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                final Post post = (Post) object;
                holder.mRootView.setTag(post);
                StringBuilder sb = new StringBuilder();
                String cityName = post.getString(AppConstants.city);
                if (cityName != null) {
                    sb.append(cityName).append(", ");
                }
                String countryName = post.getString(AppConstants.country);
                if (countryName != null) {
                    sb.append(countryName).append(", ");
                }
                String continentName = post.getString(AppConstants.continent);
                if (continentName != null) {
                    sb.append(continentName);
                }
                holder.mTitle.setText(post.getTitle());
                holder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mIvImage.setImageBitmap(resource);
                        Palette.from(resource).generate();
                    }
                };
                holder.mIvImage.setTag(target);
                if (post.getImage() != null) {
                    holder.mTvText.setVisibility(View.INVISIBLE);
                    holder.mIvImage.setVisibility(View.VISIBLE);
                    holder.mTitle.setVisibility(View.INVISIBLE);
                    Glide.with(mContext).asBitmap().load(post.getImage().getUrl()).centerCrop().into(target);
                } else {
                    holder.mIvImage.setVisibility(View.INVISIBLE);
                    holder.mTvText.setText(post.getDescription());
                    holder.mTitle.setText(post.getTitle());
                    holder.mTitle.setVisibility(View.VISIBLE);
                    holder.mTvText.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.mProgressBar.setVisibility(View.INVISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mRootView;
        ImageView mIvImage;
        TextView mTvTitle;
        View mVPalette;
        TextView mTvText;
        TextView mTitle;
        ProgressBar mProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mIvImage = itemView.findViewById(R.id.ivImage);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mVPalette = itemView.findViewById(R.id.vPalette);
            mTitle = itemView.findViewById(R.id.eventTitle);
            mTvText = itemView.findViewById(R.id.tvText);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mPosts.get(position).fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        Post post = (Post) object;
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Post.class.getSimpleName(), post);
                        intent.putExtras(bundle);
                        ((Activity) mContext).startActivityForResult(intent, AppConstants.SAVED_POST_DETAILS_FROM_PROFILE);
                    }
                });

            }
        }
    }
}