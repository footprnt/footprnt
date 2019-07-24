/*
 * RestaurantListAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprnt.Discover.Models.Restaurant;
import com.example.footprnt.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private ArrayList<Restaurant> mRestaurants;

    public RestaurantListAdapter(Context context, ArrayList<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }


    @NonNull
    @Override
    public RestaurantListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantListAdapter.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mRestaurants.get(position));
    }


    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivRestaurantImage)
        ImageView ivRestaurantImage;
        @BindView(R.id.tvRestaurantName)
        TextView tvRestaurantName;
        @BindView(R.id.tvRestaurantCategory)
        TextView tvRestaurantCategory;
        @BindView(R.id.tvRestaurantRating)
        TextView tvRestaurantRating;

        private Context mContext;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            ivRestaurantImage = itemView.findViewById(R.id.ivRestaurantImage);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvRestaurantCategory = itemView.findViewById(R.id.tvRestaurantCategory);
            tvRestaurantRating = itemView.findViewById(R.id.tvRestaurantRating);
        }

        public void bindRestaurant(Restaurant restaurant) {
            tvRestaurantName.setText(restaurant.getName());
            tvRestaurantCategory.setText(restaurant.getCategories().get(0));
            tvRestaurantRating.setText("Rating: " + restaurant.getRating() + "/5");
            Picasso.with(mContext).load(restaurant.getImageUrl()).into(ivRestaurantImage);
        }
    }
}