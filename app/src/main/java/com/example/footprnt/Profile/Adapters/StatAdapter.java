/*
 * StatAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Adapts saved stats to profile recycler view
 *
 * @author Clarisa Leu
 * @version 1.0
 * @since 7-22-19
 */
public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    static ArrayList<String> mStats;  // list of stats
    static Context mContext;  // context for rendering

    public StatAdapter(ArrayList<String> stats, Context context) {
        mStats = stats;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mStats.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_stat, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
       // TODO: set views
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