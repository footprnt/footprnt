/*
 * StatAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footprnt.R;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts saved stats to profile recycler view
 *
 * @author Clarisa Leu
 * @version 1.0
 * @since 7-22-19
 */
public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    ArrayList<String> mStats;  // list of stats
    Context mContext;  // context for rendering
    Unsplash unsplash;

    public StatAdapter(ArrayList<String> stats, Context context) {
        unsplash = new Unsplash(context.getString(R.string.unsplash_access));
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String stat = mStats.get(position);
        unsplash.searchPhotos(stat, new Unsplash.OnSearchCompleteListener() {
            @Override
            public void onComplete(SearchResults results) {
                Log.d("Photos", "Total Results Found " + results.getTotal());
                List<Photo> photos = results.getResults();
                String statPictureUrl = photos.get(3).getUrls().getRegular();
                Glide.with(mContext).load(statPictureUrl).into(holder.mIvImage);
            }

            @Override
            public void onError(String error) {
                Log.d("Unsplash", error);
            }
        });
        holder.mName.setText(stat);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        TextView mName;
        ImageView mIvImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mIvImage = itemView.findViewById(R.id.ivPicture);
            mName = itemView.findViewById(R.id.tvName);
        }
    }
}