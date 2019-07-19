package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView getmIvProfileImage() {
        return mIvProfileImage;
    }

    public void setmIvProfileImage(String url, Context context) {
        Glide.with(context).load(url).into(mIvProfileImage);
    }

    public TextView getmTvEditProfile() {
        return mTvEditProfile;
    }

    public void setmTvEditProfile(TextView mTvEditProfile) {
        this.mTvEditProfile = mTvEditProfile;
    }

    // For user profile info view:
    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;

    public UserInfoViewHolder(View v) {
        super(v);
        mIvProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImageMain);
        mTvEditProfile = (TextView) itemView.findViewById(R.id.tvEditProfile);
        // TODO: set on click listener for edit profile
    }
}
