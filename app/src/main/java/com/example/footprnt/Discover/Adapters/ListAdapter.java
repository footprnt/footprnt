/*
 * ListAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapts Businesss to the recyclerview
 * @author  Stanley Nwakamma 2019
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BusinessViewHolder> {
    private ArrayList<Business> mBusinesses;
    Context mContext;

    public ListAdapter(Context context, ArrayList<Business> businesses) {
        mBusinesses = businesses;
        mContext = context;
    }


    @NonNull
    @Override
    public ListAdapter.BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        BusinessViewHolder viewHolder = new BusinessViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.BusinessViewHolder holder, int position) {
        holder.bindBusiness(mBusinesses.get(position));
    }


    @Override
    public int getItemCount() {
        return mBusinesses.size();
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBusinessImage;
        TextView tvBusinessName;
        TextView tvBusinessCategory;
        TextView tvBusinessRating;

        private Context mContext;

        public BusinessViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            tvBusinessCategory = itemView.findViewById(R.id.tvBusinessCategory);
            tvBusinessRating = itemView.findViewById(R.id.tvBusinessRating);
        }

        public void bindBusiness(Business business) {
            tvBusinessName.setText(business.getName());
            tvBusinessCategory.setText(business.getCategories().get(0));
            tvBusinessRating.setText(String.format("Rating: %s/5", business.getRating()));
            Picasso.with(mContext).load(business.getImageUrl()).into(ivBusinessImage);
        }
    }
}