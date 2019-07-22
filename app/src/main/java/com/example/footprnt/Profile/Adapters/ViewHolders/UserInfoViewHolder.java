package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;
    PieChart mPieChart;

    public void setmIvProfileImage(String url, Context context) {
        Glide.with(context).load(url).into(mIvProfileImage);
    }

    public TextView getmTvEditProfile() {
        return mTvEditProfile;
    }

    public CircleImageView getmIvProfileImage() {
        return mIvProfileImage;
    }

    public PieChart getmPieChart() {
        return mPieChart;
    }

    public UserInfoViewHolder(View v) {
        super(v);
        mIvProfileImage = v.findViewById(R.id.ivProfileImageMain);
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
        mPieChart = v.findViewById(R.id.pieChartCity);
    }
}
