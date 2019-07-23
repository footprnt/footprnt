/*
 * MultiViewAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Adapters.ViewHolders.PostViewHolder;
import com.example.footprnt.Profile.Adapters.ViewHolders.StatViewHolder;
import com.example.footprnt.Profile.Adapters.ViewHolders.UserInfoViewHolder;
import com.example.footprnt.Profile.EditPost;
import com.example.footprnt.Profile.UserSettings;
import com.example.footprnt.Profile.Util.Constants;
import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Custom adapter to handle the multiple views on the profile page fragment
 *
 * @author Clarisa Leu-Rodriguez
 */
public class MultiViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<Object> items;

    // Identifier for objects in items and which view to load:
    private final int USER_INFO = 0, POST = 1, STAT = 2;


    /**
     * Constructor for MultiViewAdapter
     *
     * @param context reference for view
     * @param items   the list of homogeneous items in the adapter
     */
    public MultiViewAdapter(Context context, ArrayList<Object> items) {
        this.items = items;
        this.mContext = context;
    }

    /**
     * Getter for number of items in adapter
     *
     * @return size of items
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Getter for the item view type of the item at position for the purpose of view recycling
     *
     * @param position position item is at
     * @return integer corresponding to the type of view type at position, -1 if no such item view
     */
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Post) {
            return POST;
        } else if (items.get(position) instanceof ParseUser) {
            return USER_INFO;
        } else if (items.get(position) instanceof ArrayList) {
            return STAT;
        }
        return -1;
    }


    /**
     * Creates different RecyclerView.ViewHolder objects based on the item view type.
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType  type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case POST:
                View v1 = inflater.inflate(R.layout.item_post_card, viewGroup, false);
                viewHolder = new PostViewHolder(v1);
                break;
            case USER_INFO:
                View v2 = inflater.inflate(R.layout.item_user_information, viewGroup, false);
                viewHolder = new UserInfoViewHolder(v2);
                break;
            case STAT:
                View v3 = inflater.inflate(R.layout.item_user_stats, viewGroup, false);
                viewHolder = new StatViewHolder(v3);
        }
        return viewHolder;
    }

    /**
     * Internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position   Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case POST:
                PostViewHolder vh1 = (PostViewHolder) viewHolder;
                configurePostViewHolder(vh1, position);
                break;
            case USER_INFO:
                UserInfoViewHolder vh2 = (UserInfoViewHolder) viewHolder;
                configureUserInfoViewHolder(vh2, position);
                break;
            case STAT:
                StatViewHolder vh3 = (StatViewHolder) viewHolder;
                configureStatViewHolder(vh3, position);
                break;
        }
    }

    /**
     * Method to configure the post view holder
     *
     * @param vh1      view holder to configure
     * @param position position in adapter the POST item is
     */
    private void configurePostViewHolder(final PostViewHolder vh1, final int position) {
        final Post post = (Post) items.get(position);
        if (post != null) {
            vh1.getRootView().setTag(post);
            String cityName = post.getCity();
            String countryName = post.getCountry();
            String continentName = post.getContinent();
            vh1.getTvTitle().setText(String.format("%s, %s, %s", cityName, countryName, continentName));
            vh1.getTvTitle().setTextColor(Color.WHITE);
            vh1.getTvPalette().setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));

            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    vh1.getIvImage().setImageBitmap(resource);
                    Palette.from(resource).generate();
                    vh1.getTvPalette().setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
                }
            };

            vh1.getIvImage().setTag(target);
            // TODO: Maybe don't crop image as it looks very small
            if (post.getImage() != null) {
                Glide.with(mContext).asBitmap().load(post.getImage().getUrl()).centerCrop().into(target);
            } else {
                // TODO: fix default image loaded where no image present to be prettier
                Glide.with(mContext).asBitmap().load(R.drawable.ic_add_photo).centerCrop().into(target);
            }
            // TODO: fix this to start dialog vs. activity (see parent = AlertDialog)
            vh1.getIvImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, EditPost.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Post.class.getSimpleName(), post);
                    it.putExtras(bundle);
                    // Start activity for result to reconfigure user view after return
                    mContext.startActivity(it);
                }
            });
        }
    }

    /**
     * Method to configure the user information view holder
     *
     * @param vh2      view holder to configure
     * @param position in adapter the USER_INFO item is
     */
    private void configureUserInfoViewHolder(UserInfoViewHolder vh2, final int position) {
        ParseUser user = (ParseUser) items.get(position);
        if (user != null) {
            if (user.getParseFile(com.example.footprnt.Util.Constants.profileImage) != null) {
                vh2.setIvProfileImage(user.getParseFile(com.example.footprnt.Util.Constants.profileImage).getUrl(), mContext);
            } else {
                // User does not have an image, load preset image
                // TODO: change this to be more pretty (i.e. the tint of the image)
                Glide.with(mContext).load(R.drawable.ic_user).into(vh2.getIvProfileImage());
            }
            // TODO: Fix edit profile so it updates previous screen (this)
            vh2.getTvEditProfile().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, UserSettings.class);
                    ((Activity) mContext).startActivityForResult(it, com.example.footprnt.Util.Constants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE);
                }
            });
        }
    }

    /**
     * Method to configure the user statistics view holder
     *
     * @param vh3      view holder to configure
     * @param position position in adapter the STATS item is
     */
    private void configureStatViewHolder(final StatViewHolder vh3, final int position) {
        final ArrayList<HashMap<String, Integer>> stats = (ArrayList<HashMap<String, Integer>>) items.get(position);
        final HashMap<String, Integer> cities = stats.get(0);
        final HashMap<String, Integer> countries = stats.get(1);
        final HashMap<String, Integer> continents = stats.get(2);

        if (cities != null) {
            setUpPieChart(vh3.getPieChartCity(), cities.size(), Constants.totalNumCities, "Visited Cities");
        }

        if (countries != null) {
            setUpPieChart(vh3.getPieChartCountry(), countries.size(), Constants.totalNumContinents, "Visited Countries");
        }

        if (continents != null) {
            setUpPieChart(vh3.getPieChartContinent(), continents.size(), Constants.totalNumCountries, "Visited Continents");
        }

    }

    /**
     * Helper method to set up the user statistic pie chart
     *
     * @param pieChart the chart to configure
     * @param visited  number of places user has visited
     * @param total    total for pie chart (i.e. total number of places been)
     * @param title    the title of the pie chart
     */
    private void setUpPieChart(PieChart pieChart, int visited, int total, String title) {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(visited, title));
        pieEntries.add(new PieEntry(total - visited));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "User Stats");
        pieDataSet.setColors(R.color.colorPrimary, R.color.colorPrimaryDark);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
