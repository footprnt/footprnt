/*
 * SavedBusinessesActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Adapts saved businesses to profile recycler view for saved things to do
 * TODO: implement
 * @author Clarisa Leu
 */
public class SavedActivitiesAdapter extends RecyclerView.Adapter<SavedActivitiesAdapter.ViewHolder> {

    static ArrayList<Business> mBusinesses;  // list of saved businesses
    static Context mContext;  // context for rendering

    public SavedActivitiesAdapter(ArrayList<Business> businesses, Context context) {
        mBusinesses = businesses;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mBusinesses.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_business, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Business savedBusiness = mBusinesses.get(position);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // TODO: implement business details view on click

            }
        }
    }
}