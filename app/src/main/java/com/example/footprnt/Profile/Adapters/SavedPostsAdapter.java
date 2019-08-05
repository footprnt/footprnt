/*
 * SavedPostsAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Adapts saved posts to profile recycler view
 *
 * @author Clarisa Leu
 */
public class SavedPostsAdapter extends RecyclerView.Adapter<SavedPostsAdapter.ViewHolder> {

    static ArrayList<SavedPost> mPosts;    // list of posts
    static Context mContext;          // context for rendering

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
        final SavedPost savedPost = mPosts.get(position);
        final Post post = (Post) savedPost.getPost();
        if (post != null) {
            holder.mRootView.setTag(post);
            StringBuilder sb = new StringBuilder();
            String cityName = post.getCity();
            if (cityName != null) {
                sb.append(cityName).append(", ");
            }
            String countryName = post.getCountry();
            if (countryName != null) {
                sb.append(countryName).append(", ");
            }
            String continentName = post.getContinent();
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
            holder.mRootView.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: implement detailed view of saved post
                }
            });
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        ImageView mIvImage;
        TextView mTvTitle;
        View mVPalette;
        TextView mTvText;
        TextView mTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mIvImage = itemView.findViewById(R.id.ivImage);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mVPalette = itemView.findViewById(R.id.vPalette);
            mTitle = itemView.findViewById(R.id.eventTitle);
            mTvText = itemView.findViewById(R.id.tvText);
        }
    }
}