package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    CircleImageView mIvProfileImage;
    TextView mTvEditProfile;
    PieChart mPieChart;
    public final int totalNumCities =4416;
    public final int totalNumCountries = 195;
    public final int totalNumContinents = 7;



    public void setmIvProfileImage(String url, Context context) {
        Glide.with(context).load(url).into(mIvProfileImage);
    }

    public TextView getmTvEditProfile() {
        return mTvEditProfile;
    }

    public CircleImageView getmIvProfileImage() {
        return mIvProfileImage;
    }

    public UserInfoViewHolder(View v) {
        super(v);
        mIvProfileImage = v.findViewById(R.id.ivProfileImageMain);
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
        mTvEditProfile = v.findViewById(R.id.tvEditProfile);
        mPieChart = v.findViewById(R.id.pieChart);
        setUpPieChart();
    }


    private void setUpPieChart(){
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(432, "Cities"));
        pieEntries.add(new PieEntry(totalNumCities-432, "Unvisited Cities"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Number of Cities Visited");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.animateY(1000);
        mPieChart.invalidate();
    }
}
