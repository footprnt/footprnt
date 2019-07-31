/*
 * PostViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.EditPost;
import com.example.footprnt.R;

/**
 * View holder for post item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 */
public class PostViewHolder extends RecyclerView.ViewHolder {

    View mRootView;
    ImageView mIvImage;
    TextView mTvTitle;
    View mVPalette;
    TextView mTvText;
    TextView title;

    /**
     * Getter for the root view of the post view holder
     *
     * @return the View of the root view of the post
     */
    public View getRootView() {
        return mRootView;
    }

    /**
     * Getter for the ImageView of the post in the post view holder
     *
     * @return the ImageView of the post
     */
    public ImageView getIvImage() {
        return mIvImage;
    }

    /**
     * Getter for the TextView of the title of the post in the post view holdeer
     *
     * @return the TextView of the title of the post
     */
    public TextView getTvTitle() {
        return mTvTitle;
    }

    /**
     * Getter for the View of the Palette for the post in the post view holder
     *
     * @return the View of the Palette of the post
     */
    public View getTvPalette() {
        return mVPalette;
    }

    public TextView getTvDescription(){ return mTvText;}

    public TextView getPostTitle(){ return title;}

    /**
     * Constructor for PostViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public PostViewHolder(final View v) {
        super(v);
        mRootView = itemView;
        mIvImage = itemView.findViewById(R.id.ivImage);
        mTvTitle = itemView.findViewById(R.id.tvTitle);
        mVPalette = itemView.findViewById(R.id.vPalette);
        title = itemView.findViewById(R.id.title);
        mTvText = itemView.findViewById(R.id.tvText);

        // Set on click listener to launch detailed post view
        // TODO: fix this
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Post contact = (Post) v.getTag();
                if (contact != null) {
                    Intent i = new Intent(v.getContext(), EditPost.class);
                }
            }
        });
        mTvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Post contact = (Post) v.getTag();
                if (contact != null) {
                    Intent i = new Intent(v.getContext(), EditPost.class);
                }
            }
        });
    }

}
