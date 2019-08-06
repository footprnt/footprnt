/*
 * SavedBusinessesActivity.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.footprnt.Discover.Util.DiscoverConstants;
import com.example.footprnt.Map.MapFragment;
import com.example.footprnt.Models.SavedActivity;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.example.footprnt.ViewPagerAdapter;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Adapts saved businesses to profile recycler view for saved things to do
 * TODO: implement
 *
 * @author Clarisa Leu
 */
public class SavedActivitiesAdapter extends RecyclerView.Adapter<SavedActivitiesAdapter.ViewHolder> {

    static ArrayList<SavedActivity> mSavedActivities;  // list of saved activities
    static Context mContext;  // context for rendering

    public SavedActivitiesAdapter(ArrayList<SavedActivity> savedActivities, Context context) {
        mSavedActivities = savedActivities;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mSavedActivities.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_business, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SavedActivity savedBusiness = mSavedActivities.get(position);
        holder.mCardView.setTag(savedBusiness);
        holder.mTvBusinessName.setText(savedBusiness.getName());
        try {
            ArrayList<String> categories = AppUtil.parseJSONArray(savedBusiness.getCategories());
            holder.mTvBusinessCategory.setText(categories.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.mRating.setVisibility(View.VISIBLE);
            holder.mRating.setRating((float) savedBusiness.getRating());
            LayerDrawable stars = (LayerDrawable) holder.mRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {
            holder.mRating.setVisibility(View.INVISIBLE);
        }
        if (savedBusiness.getImageUrl() == null || savedBusiness.getImageUrl().length() == 0) {
            Picasso.with(mContext).load(DiscoverConstants.IMAGE_PLACEHOLDER_PATH);
        } else {
            Picasso.with(mContext).load(savedBusiness.getImageUrl()).into(holder.mIvBusinessImage);
        }
        // Delete Saved Activity
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int newPosition = holder.getAdapterPosition();
                SavedActivity savedActivity = mSavedActivities.get(newPosition);
                mSavedActivities.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemChanged(newPosition, mSavedActivities.size());
                savedActivity.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(mContext, "Saved Activity Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                if (mSavedActivities.size() == 0) {
                    ((Activity) mContext).finish();
                }
                return true;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mCardView;
        ImageView mIvBusinessImage;
        TextView mTvBusinessName;
        TextView mTvBusinessCategory;
        RatingBar mRating;
        TextView mTvBusinessAddress;
        TextView mTvBusinessPhone;
        ImageView mBtnCall;
        ImageView mBtnLocation;

        private Context mContext;
        private String mJoinAddress;
        private AlertDialog mDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView;
            mContext = itemView.getContext();
            mIvBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            mTvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            mTvBusinessCategory = itemView.findViewById(R.id.tvBusinessCategory);
            mRating = itemView.findViewById(R.id.rating);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    SavedActivity business = (SavedActivity) v.getTag();
                    final View popupView = LayoutInflater.from(mContext).inflate(R.layout.business_details, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setView(popupView);
                    AlertDialog mAlertDialog = alertDialogBuilder.create();
                    WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
                    lp.dimAmount = 0f;
                    mAlertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    mAlertDialog.show();
                    mDialog = mAlertDialog;
                    mIvBusinessImage = mAlertDialog.findViewById(R.id.ivBusinessImage);
                    mTvBusinessName = mAlertDialog.findViewById(R.id.tvBusinessName);
                    mTvBusinessCategory = mAlertDialog.findViewById(R.id.tvBusinessCategory);
                    mBtnCall = mAlertDialog.findViewById(R.id.btnCall);
                    mBtnLocation = mAlertDialog.findViewById(R.id.btnLocation);
                    mRating = mAlertDialog.findViewById(R.id.rating);
                    mAlertDialog.findViewById(R.id.ivBookmark).setVisibility(View.INVISIBLE);
                    LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
                    mTvBusinessAddress = mAlertDialog.findViewById(R.id.tvBusinessAddress);
                    mTvBusinessPhone = mAlertDialog.findViewById(R.id.tvBusinessPhone);
                    bindBusinessDetail(business);
                }
            });
        }

        @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindBusinessDetail(SavedActivity savedBusiness) {
            mCardView.setTag(savedBusiness);
            final String businessName = savedBusiness.getName();
            mTvBusinessName.setText(businessName);
            try {
                ArrayList<String> categories = AppUtil.parseJSONArray(savedBusiness.getCategories());
                mTvBusinessCategory.setText(categories.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mRating.setVisibility(View.VISIBLE);
                mRating.setRating((float) savedBusiness.getRating());
                LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
            } catch (Exception e) {
                mRating.setVisibility(View.INVISIBLE);
            }
            final String imageUrl = savedBusiness.getImageUrl();
            if (imageUrl == null || imageUrl.length() == 0) {
                Picasso.with(mContext).load(DiscoverConstants.IMAGE_PLACEHOLDER_PATH);
            } else {
                Picasso.with(mContext).load(imageUrl).into(mIvBusinessImage);
            }
            try {
                ArrayList<String> address = AppUtil.parseJSONArray(savedBusiness.getAddress());
                mTvBusinessAddress.setVisibility(View.VISIBLE);
                mJoinAddress = String.join(" ", address);
                mTvBusinessAddress.setText(mJoinAddress);
                mTvBusinessAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        ViewPager viewPager = ((Activity) mContext).findViewById(R.id.viewpager);
                        viewPager.setCurrentItem(0);
                        Fragment viewPagerAdapter = ((ViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter())).getItem(0);
                        ((MapFragment) viewPagerAdapter).handleDiscoverInteraction(mJoinAddress, businessName, imageUrl);
                    }
                });
                mBtnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        ViewPager viewPager = ((Activity) mContext).findViewById(R.id.viewpager);
                        viewPager.setCurrentItem(0);
                        Fragment viewPagerAdapter = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(0);
                        ((MapFragment) viewPagerAdapter).handleDiscoverInteraction(mJoinAddress, businessName, imageUrl);
                    }
                });
            } catch (Exception e) {
                mBtnLocation.setVisibility(View.GONE);
                mTvBusinessAddress.setVisibility(View.GONE);
            }
            final String url = savedBusiness.getWebsite();
            if (url != null && url.length() > 0) {
                mTvBusinessName.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        ((Activity) mContext).startActivityForResult(i, AppConstants.VIEW_BUSINESS_PAGE);
                        return true;
                    }
                });
            }
            final String businessPhoneNum = savedBusiness.getPhoneNumber();
            if (businessPhoneNum != null && businessPhoneNum.length() > 0) {
                mTvBusinessPhone.setVisibility(View.VISIBLE);
                mTvBusinessPhone.setText(businessPhoneNum);
                mTvBusinessPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + businessPhoneNum));
                        ((Activity) mContext).startActivityForResult(intent, AppConstants.VIEW_BUSINESS_PAGE);
                    }
                });
                mBtnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + businessPhoneNum));
                        ((Activity) mContext).startActivityForResult(intent, AppConstants.VIEW_BUSINESS_PAGE);
                    }
                });
            } else {
                mTvBusinessPhone.setVisibility(View.GONE);
            }
        }
    }
}