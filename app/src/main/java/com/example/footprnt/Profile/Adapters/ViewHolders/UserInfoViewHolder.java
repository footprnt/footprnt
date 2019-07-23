/*
 * PostViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * View holder for user information item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 */
public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;
    PieChart mPieChart;

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
     * Constructor for UserInfoViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public UserInfoViewHolder(View v) {
        super(v);
        mIvProfileImage = v.findViewById(R.id.ivProfileImageMain);
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
    }
}
