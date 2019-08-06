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
import android.widget.Toast;

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
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
        mPosts = posts;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_post_card, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mProgressBar.setVisibility(View.VISIBLE);
        final SavedPost savedPost = mPosts.get(position);
        final Post post = (Post) savedPost.getPost();
        holder.mRootView.setTag(post);
        StringBuilder sb = new StringBuilder();
        String cityName = "";
        String countryName = "";
        String continentName = "";
        String title = "";
        String description = "";
        ParseFile image = null;
        try {
            cityName = post.fetchIfNeeded().getString(AppConstants.city);
            countryName = post.fetchIfNeeded().getString(AppConstants.country);
            continentName = post.fetchIfNeeded().getString(AppConstants.continent);
            title = post.fetchIfNeeded().getString(AppConstants.title);
            description = post.fetchIfNeeded().getString(AppConstants.description);
            image = post.fetchIfNeeded().getParseFile(AppConstants.image);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cityName.length() > 0) {
            sb.append(cityName).append(", ");
        }
        if (countryName.length() > 0) {
            sb.append(countryName).append(", ");
        }
        if (continentName.length() > 0) {
            sb.append(continentName);
        }
        holder.mTvTitle.setText(sb);
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                holder.mIvImage.setImageBitmap(resource);
                Palette.from(resource).generate();
            }
        };
        holder.mIvImage.setTag(target);
        if (image != null) {
            holder.mTvText.setVisibility(View.INVISIBLE);
            holder.mIvImage.setVisibility(View.VISIBLE);
            holder.mTitle.setVisibility(View.INVISIBLE);
            Glide.with(mContext).asBitmap().load(image.getUrl()).centerCrop().into(target);
        } else {
            holder.mIvImage.setVisibility(View.INVISIBLE);
            holder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            holder.mTvText.setText(description);
            holder.mTitle.setText(title);
            holder.mTitle.setVisibility(View.VISIBLE);
            holder.mTvText.setVisibility(View.VISIBLE);
        }
        holder.mProgressBar.setVisibility(View.INVISIBLE);
        // Saved Post Details
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    mPosts.get(position).fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            SavedPost savedPost = (SavedPost) object;
                            Post post = (Post) savedPost.getPost();
                            Intent intent = new Intent(mContext, PostDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Post.class.getSimpleName(), post);
                            bundle.putSerializable(AppConstants.hideView, true);
                            intent.putExtras(bundle);
                            ((Activity) mContext).startActivityForResult(intent, AppConstants.SAVED_POST_DETAILS_FROM_PROFILE);
                        }
                    });
                }
            }
        });
        // Delete Saved Post
        holder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int newPosition = holder.getAdapterPosition();
                SavedPost savedPost1 = mPosts.get(newPosition);
                mPosts.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemChanged(newPosition, mPosts.size());
                savedPost1.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(mContext, R.string.delete_saved_post, Toast.LENGTH_SHORT).show();
                    }
                });
                if (mPosts.size() == 0) {
                    ((Activity) mContext).finish();
                }
                return true;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
    }
}