/*
 * ListAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapts Businesss to the recyclerview
 *
 * @author Stanley Nwakamma 2019
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
    public BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        card.setCardBackgroundColor(Color.parseColor("#050505"));
        card.setMaxCardElevation(0);
        card.setRadius(30);
        BusinessViewHolder viewHolder = new BusinessViewHolder(card);
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
        public View cardView;
        ImageView ivBusinessImage;
        TextView tvBusinessName;
        TextView tvBusinessCategory;
        TextView tvBusinessRating;
        TextView tvBusinessAddress;
        TextView tvBusinessUrl;
        TextView tvBusinessPhone;
        Button btnCall;

        private Context mContext;

        public BusinessViewHolder(final View itemView) {
            super(itemView);
            cardView = itemView;
            mContext = itemView.getContext();
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            tvBusinessCategory = itemView.findViewById(R.id.tvBusinessCategory);
            tvBusinessRating = itemView.findViewById(R.id.tvBusinessRating);
            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Business business = (Business) v.getTag();
                    final View popupView = LayoutInflater.from(mContext).inflate(R.layout.business_details, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setView(popupView);
                    AlertDialog mAlertDialog = alertDialogBuilder.create();
                    WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
                    lp.dimAmount = 0f;
                    mAlertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    mAlertDialog.show();
                    ivBusinessImage = mAlertDialog.findViewById(R.id.ivBusinessImage);
                    tvBusinessName = mAlertDialog.findViewById(R.id.tvBusinessName);
                    tvBusinessCategory = mAlertDialog.findViewById(R.id.tvBusinessCategory);
                    tvBusinessRating = mAlertDialog.findViewById(R.id.tvBusinessRating);
                    tvBusinessAddress = mAlertDialog.findViewById(R.id.tvBusinessAddress);
                    tvBusinessUrl = mAlertDialog.findViewById(R.id.tvBusinessUrl);
                    tvBusinessPhone = mAlertDialog.findViewById(R.id.tvBusinessPhone);
//                    btnCall = popupView.findViewById(R.id.btnCall);
                    bindBusinessDetail(business);
                }
            });
        }

        public void bindBusinessDetail(Business business) {
            cardView.setTag(business);
            tvBusinessName.setText(business.getName());
            tvBusinessCategory.setText(business.getCategories().get(0));
            tvBusinessRating.setText(String.format("Rating: %s/5", business.getRating()));
            tvBusinessAddress.setText(business.getAddress().get(0));
            tvBusinessUrl.setText(business.getWebsite());
            tvBusinessPhone.setText(business.getPhone());
            if (business.getImageUrl() == null || business.getImageUrl().length() == 0){
                Picasso.with(mContext).load("https://pyzikscott.files.wordpress.com/2016/03/yelp-placeholder.png?w=584");
            } else {
                Picasso.with(mContext).load(business.getImageUrl()).into(ivBusinessImage);
            }
        }

        public  void bindBusiness (Business business) {
            cardView.setTag(business);
            tvBusinessName.setText(business.getName());
            tvBusinessCategory.setText(business.getCategories().get(0));
            tvBusinessRating.setText(String.format("Rating: %s/5", business.getRating()));
            if (business.getImageUrl() == null || business.getImageUrl().length() == 0){
                Picasso.with(mContext).load("https://pyzikscott.files.wordpress.com/2016/03/yelp-placeholder.png?w=584");
            } else {
                Picasso.with(mContext).load(business.getImageUrl()).into(ivBusinessImage);
            }
        }
    }
}