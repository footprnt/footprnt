/*
 * PostViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.R;

/**
 * View holder for post item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 * @version 1.0
 * @since 7-22-19
 */
public class NoPostsViewHolder extends RecyclerView.ViewHolder {

    View mRootView;
    View mVPalette;

    /**
     * Getter for the root view of the post view holder
     *
     * @return the View of the root view of the post
     */
    public View getRootView() {
        return mRootView;
    }

    /**
     * Getter for the View of the Palette for the post in the post view holder
     *
     * @return the View of the Palette of the post
     */
    public View getTvPalette() {
        return mVPalette;
    }

    /**
     * Constructor for PostViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public NoPostsViewHolder(final View v) {
        super(v);
        mRootView = itemView;
        mVPalette = v.findViewById(R.id.vPalette);
    }

}
