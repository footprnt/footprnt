/*
 * UserInfoViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * View holder for user information item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 * @version 1.0
 * @since 7-22-19
 */
public class UserInfoViewHolder extends RecyclerView.ViewHolder {

    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;
    TextView mUsername;
    TextView mDescription;

    /**
     * Setter for the profile image on profile page
     */
    public void setIvProfileImage(String url, Context context) {
        Glide.with(context).load(url).into(mIvProfileImage);
    }

    /**
     * Getter for the "Edit Profile" TextView in user information view
     *
     * @return TextView for "Edit Profile" in user information view
     */
    public TextView getTvEditProfile() {
        return mTvEditProfile;
    }

    /**
     * Getter for the CircleImageView for user in user information view
     *
     * @return CircleImageView of user profile image
     */
    public CircleImageView getIvProfileImage() {
        return mIvProfileImage;
    }

    /**
     * Getter for TextView for username
     *
     * @return TextView for username
     */
    public TextView getTvUsername() {
        return mUsername;
    }

    /**
     * Getter for TextView for Description
     *
     * @return TextView for Description
     */
    public TextView getTvDescription() {
        return mDescription;
    }

    /**
     * Constructor for UserInfoViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public UserInfoViewHolder(View v) {
        super(v);
        mIvProfileImage = v.findViewById(R.id.ivProfileImageMain);
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
        mDescription = v.findViewById(R.id.tvDescription);
        mUsername = v.findViewById(R.id.tvUsername);
    }
}
