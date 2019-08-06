/*
 * ListAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Adapters;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Util.DiscoverConstants;
import com.example.footprnt.Map.MapFragment;
import com.example.footprnt.Models.SavedActivity;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.ViewPagerAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adapts business views for RV on the Discover Fragment
 *
 * @author Stanley Nwakamma, Jocelyn Shen, Clarisa Leu
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BusinessViewHolder> {
    private ArrayList<Business> mBusinesses;
    Context mContext;

    public ListAdapter(Context context, ArrayList<Business> businesses) {
        mBusinesses = businesses;
        mContext = context;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        card.setCardBackgroundColor(R.color.black_business);
        card.setMaxCardElevation(0);
        card.setRadius(DiscoverConstants.CARD_ELEVATION);
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

        View mCardView;
        ImageView mIvBusinessImage;
        TextView mTvBusinessName;
        TextView mTvBusinessCategory;
        RatingBar mRating;
        TextView mTvBusinessAddress;
        TextView mTvBusinessPhone;
        ImageView mBtnCall;
        ImageView mBtnLocation;
        ImageView mBookmark;

        private Context mContext;
        private String mJoinAddress;
        private AlertDialog mDialog;
        private Boolean mIsSaved;

        public BusinessViewHolder(final View itemView) {
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
                    Business business = (Business) v.getTag();
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
                    mBookmark = mAlertDialog.findViewById(R.id.ivBookmark);
                    mTvBusinessName = mAlertDialog.findViewById(R.id.tvBusinessName);
                    mTvBusinessCategory = mAlertDialog.findViewById(R.id.tvBusinessCategory);
                    mBtnCall = mAlertDialog.findViewById(R.id.btnCall);
                    mBtnLocation = mAlertDialog.findViewById(R.id.btnLocation);
                    mRating = mAlertDialog.findViewById(R.id.rating);
                    LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
                    mTvBusinessAddress = mAlertDialog.findViewById(R.id.tvBusinessAddress);
                    mTvBusinessPhone = mAlertDialog.findViewById(R.id.tvBusinessPhone);
                    bindBusinessDetail(business);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
        public void bindBusinessDetail(final Business business) {
            mCardView.setTag(business);
            checkIfSaved(business);  // Check if the business is saved in DB and update view
            mBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsSaved) {
                        // Activity not saved yet - save
                        SavedActivity savedActivity = new SavedActivity();
                        savedActivity.setAddress(business.getAddress());
                        savedActivity.setCategories(business.getCategories());
                        savedActivity.setImageUrl(business.getImageUrl());
                        savedActivity.setName(business.getName());
                        savedActivity.setUser(ParseUser.getCurrentUser());
                        savedActivity.setWebsite(business.getWebsite());
                        savedActivity.setPhoneNumber(business.getPhone());
                        savedActivity.setRating(business.getRating());
                        savedActivity.setLocation(new ParseGeoPoint(business.getLatitude(), business.getLongitude()));
                        savedActivity.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                mIsSaved = true;
                                Toast.makeText(mContext, "Saved Activity", Toast.LENGTH_SHORT).show();
                                mBookmark.setImageResource(R.drawable.ic_save_check_filled_blue);
                            }
                        });
                    } else {
                        // Post already saved - unsaved
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.savedActivity);
                        query.whereEqualTo(AppConstants.user, ParseUser.getCurrentUser()).whereEqualTo(AppConstants.website, business.getWebsite()).whereEqualTo(AppConstants.imageUrl, business.getImageUrl());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ParseObject.deleteAllInBackground(objects, new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            mIsSaved = false;
                                            mBookmark.setImageResource(R.drawable.ic_save_check_blue);
                                            Toast.makeText(mContext, "Unsaved Activity", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
            final String businessName = business.getName();
            mTvBusinessName.setText(businessName);
            mTvBusinessCategory.setText(business.getCategories().get(0));
            try {
                mRating.setVisibility(View.VISIBLE);
                mRating.setRating((float) business.getRating());
                LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
            } catch (Exception e) {
                mRating.setVisibility(View.INVISIBLE);
            }
            final String imageUrl = business.getImageUrl();
            if (imageUrl == null || imageUrl.length() == 0) {
                Picasso.with(mContext).load(DiscoverConstants.IMAGE_PLACEHOLDER_PATH);
            } else {
                Picasso.with(mContext).load(imageUrl).into(mIvBusinessImage);
            }
            try {
                mTvBusinessAddress.setVisibility(View.VISIBLE);
                mJoinAddress = String.join(" ", business.getAddress());
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
            final String url = business.getWebsite();
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
            final String businessPhoneNum = business.getPhone();
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

        @SuppressLint("ResourceType")
        public void bindBusiness(Business business) {
            mCardView.setTag(business);
            mTvBusinessName.setText(business.getName());
            mTvBusinessCategory.setText(business.getCategories().get(0));
            try {
                mRating.setVisibility(View.VISIBLE);
                mRating.setRating((float) business.getRating());
                LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor(mContext.getResources().getString(R.color.blue_business)), PorterDuff.Mode.SRC_ATOP);
            } catch (Exception e) {
                mRating.setVisibility(View.INVISIBLE);
            }
            if (business.getImageUrl() == null || business.getImageUrl().length() == 0) {
                Picasso.with(mContext).load(DiscoverConstants.IMAGE_PLACEHOLDER_PATH);
            } else {
                Picasso.with(mContext).load(business.getImageUrl()).into(mIvBusinessImage);
            }
        }

        /**
         * Helper method to update view for bookmark and check if business is saved in DB
         */
        private void checkIfSaved(Business business) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.savedActivity);
            query.whereEqualTo(AppConstants.user, ParseUser.getCurrentUser()).whereEqualTo(AppConstants.name, business.getName()).whereEqualTo(AppConstants.website, business.getWebsite());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            mIsSaved = true;
                            mBookmark.setImageResource(R.drawable.ic_save_check_filled_blue);
                        } else {
                            mIsSaved = false;
                            mBookmark.setImageResource(R.drawable.ic_save_check_blue);
                        }
                    } else {
                        Toast.makeText(mContext, "Error querying saved activities", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}