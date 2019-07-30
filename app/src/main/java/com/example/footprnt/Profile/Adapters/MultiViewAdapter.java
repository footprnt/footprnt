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
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Database.Models.PostWrapper;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Adapters.ViewHolders.NoPostsViewHolder;
import com.example.footprnt.Profile.Adapters.ViewHolders.PostViewHolder;
import com.example.footprnt.Profile.Adapters.ViewHolders.StatViewHolder;
import com.example.footprnt.Profile.Adapters.ViewHolders.UserInfoViewHolder;
import com.example.footprnt.Profile.EditPost;
import com.example.footprnt.Profile.UserSettings;
import com.example.footprnt.Profile.Util.ProfileConstants;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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
    ArrayList<Object> mItems;

    // Identifier for objects in items and which view to load:
    private final int USER_INFO = 0, POST = 1, STAT = 2, NO_POSTS = 3, POST_WRAPPER = 4, STAT_DB = 5;

    /**
     * Constructor for MultiViewAdapter
     *
     * @param context reference for view
     * @param items   the list of homogeneous items in the adapter
     */
    public MultiViewAdapter(Context context, ArrayList<Object> items) {
        this.mItems = items;
        this.mContext = context;
    }

    /**
     * Getter for number of items in adapter
     *
     * @return size of items
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Getter for the item view type of the item at position for the purpose of view recycling
     *
     * @param position position item is at
     * @return integer corresponding to the type of view type at position, -1 if no such item view
     */
    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Post) {
            return POST;
        } else if (mItems.get(position) instanceof ParseUser) {
            return USER_INFO;
        } else if (mItems.get(position) instanceof ArrayList) {
            return STAT;
        } else if (mItems.get(position) instanceof PostWrapper) {
            return POST_WRAPPER;
        } else if (mItems.get(position) instanceof String) {
            return NO_POSTS;
        }
        return -1;
    }

    LayoutInflater inflater;

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
        inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case POST:
                View v1 = inflater.inflate(R.layout.item_post_card, viewGroup, false);
                v1.findViewById(R.id.tvText).setVisibility(View.INVISIBLE);
                v1.findViewById(R.id.title).setVisibility(View.INVISIBLE);
                viewHolder = new PostViewHolder(v1);
                break;
            case USER_INFO:
                View v2 = inflater.inflate(R.layout.item_user_information, viewGroup, false);
                viewHolder = new UserInfoViewHolder(v2);
                break;
            case STAT:
                View v3 = inflater.inflate(R.layout.item_user_stats, viewGroup, false);
                viewHolder = new StatViewHolder(v3);
                break;
            case NO_POSTS:
                View v4 = inflater.inflate(R.layout.item_no_posts, viewGroup, false);
                viewHolder = new NoPostsViewHolder(v4);
                break;
            case POST_WRAPPER:
                View v5 = inflater.inflate(R.layout.item_post_card, viewGroup, false);
                viewHolder = new PostViewHolder(v5);
                break;
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
            case NO_POSTS:
                NoPostsViewHolder vh4 = (NoPostsViewHolder) viewHolder;
                configureNoPostsViewHolder(vh4, position);
                break;
            case POST_WRAPPER:
                PostViewHolder vh5 = (PostViewHolder) viewHolder;
                configurePostWrapperViewHolder(vh5, position);
                break;
        }
    }

    private void configureStatDBViewHolder(final StatViewHolder vh6, final int position) {
        // TODO: implement later
    }

    private void configurePostWrapperViewHolder(final PostViewHolder vh5, final int position) {
        if (position < mItems.size()) {
            PostWrapper postWrapper = (PostWrapper) mItems.get(position);
            if (postWrapper != null) {
                vh5.getRootView().setTag(postWrapper);
                StringBuilder sb = new StringBuilder();
                String cityName = postWrapper.getCity();
                if (cityName != null) {
                    sb.append(cityName).append(", ");
                }
                String countryName = postWrapper.getCountry();
                if (countryName != null) {
                    sb.append(countryName).append(", ");
                }
                String continentName = postWrapper.getContinent();
                if (continentName != null) {
                    sb.append(continentName);
                }
                vh5.getTvTitle().setText(sb);
                vh5.getTvTitle().setTextColor(ContextCompat.getColor(mContext, R.color.grey));

                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        vh5.getIvImage().setImageBitmap(resource);
                        Palette.from(resource).generate();
                    }
                };

                vh5.getRootView().setClickable(false);

                vh5.getIvImage().setTag(target);
                if (postWrapper.imageUrl.length() > 0) {
                    vh5.getTvDescription().setVisibility(View.INVISIBLE);
                    vh5.getIvImage().setVisibility(View.VISIBLE);
                    vh5.getPostTitle().setVisibility(View.INVISIBLE);
                    Glide.with(mContext).asBitmap().load(postWrapper.imageUrl).centerCrop().into(target);
                } else {
                    vh5.getIvImage().setVisibility(View.INVISIBLE);
                    vh5.getTvDescription().setText(postWrapper.getDescription());
                    vh5.getPostTitle().setText(postWrapper.getTitle());
                    vh5.getPostTitle().setVisibility(View.VISIBLE);
                    vh5.getTvDescription().setVisibility(View.VISIBLE);
                }
                if (postWrapper.getImageUrl().length() > 0) {
                    Glide.with(mContext).asBitmap().load(postWrapper.getImageUrl()).centerCrop().into(target);
                }
            }
        }
    }


    /**
     * Method to configure the post view holder
     *
     * @param vh1      view holder to configure
     * @param position position in adapter the POST item is
     */
    private void configurePostViewHolder(final PostViewHolder vh1, final int position) {
        if (position < mItems.size()) {
            Post post = (Post) mItems.get(position);
            if (post != null) {
                vh1.getRootView().setTag(post);
                StringBuilder sb = new StringBuilder();
                String cityName = post.getCity();
                if (cityName != null) {
                    sb.append(cityName).append(", ");
                }
                String countryName = post.getCountry();
                if (countryName != null) {
                    sb.append(countryName).append(", ");
                }
                String continentName = post.getContinent();
                if (continentName != null) {
                    sb.append(continentName);
                }
                vh1.getTvTitle().setText(sb);
                vh1.getTvTitle().setTextColor(ContextCompat.getColor(mContext, R.color.grey));

                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        vh1.getIvImage().setImageBitmap(resource);
                        Palette.from(resource).generate();
                    }
                };

                vh1.getIvImage().setTag(target);
                if (post.getImage() != null) {
                    vh1.getTvDescription().setVisibility(View.INVISIBLE);
                    vh1.getIvImage().setVisibility(View.VISIBLE);
                    vh1.getPostTitle().setVisibility(View.INVISIBLE);
                    Glide.with(mContext).asBitmap().load(post.getImage().getUrl()).centerCrop().into(target);
                } else {
                    vh1.getIvImage().setVisibility(View.INVISIBLE);
                    vh1.getTvDescription().setText(post.getDescription());
                    vh1.getPostTitle().setText(post.getTitle());
                    vh1.getPostTitle().setVisibility(View.VISIBLE);
                    vh1.getTvDescription().setVisibility(View.VISIBLE);
                }

                vh1.getRootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Post p = (Post) mItems.get(position);
                        Intent it = new Intent(mContext, EditPost.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Post.class.getSimpleName(), p);
                        bundle.putSerializable(AppConstants.position, position);
                        it.putExtras(bundle);
                        ((Activity) mContext).startActivityForResult(it, AppConstants.DELETE_POST_FROM_PROFILE);
                    }
                });


            }
        }
    }

    /**
     * Method to configure the user information view holder
     *
     * @param vh2      view holder to configure
     * @param position in adapter the USER_INFO item is
     */
    private void configureUserInfoViewHolder(final UserInfoViewHolder vh2, final int position) {
        if (position < mItems.size()) {
            final ParseUser user = (ParseUser) mItems.get(position);
            if (user != null) {
                if (user.getParseFile(AppConstants.profileImage) != null) {
                    vh2.setIvProfileImage(user.getParseFile(AppConstants.profileImage).getUrl(), mContext);
                }

                user.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (object.getString(AppConstants.description) != null) {
                            vh2.getTvDescription().setText(object.getString(AppConstants.description));
                        }
                    }
                });
                vh2.getTvUsername().setText("@" + user.getUsername());

                vh2.getTvEditProfile().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, UserSettings.class);
                        ((Activity) mContext).startActivityForResult(it, AppConstants.RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE);
                    }
                });
            }
        }
    }

    /**
     * Method to configure the user statistics view holder
     *
     * @param vh3      view holder to configure
     * @param position position in adapter the STATS item is
     */
    private void configureStatViewHolder(final StatViewHolder vh3, final int position) {
        if (position < mItems.size()) {
            final ArrayList<HashMap<String, Integer>> stats = (ArrayList<HashMap<String, Integer>>) mItems.get(position);
            final HashMap<String, Integer> cities = stats.get(0);
            final HashMap<String, Integer> countries = stats.get(1);
            final HashMap<String, Integer> continents = stats.get(2);

            if (cities != null && cities.size() < ProfileConstants.totalNumCities) {
                setUpPieChart(vh3.getPieChartCity(), cities.size(), ProfileConstants.totalNumCities, "Visited Cities");
            } else {

                View view = vh3.getRootView().findViewById(R.id.pieChartCity);
                if (view != null) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    int index = parent.indexOfChild(view);
                    parent.removeView(view);
                    view = inflater.inflate(R.layout.item_visited_all_cities, parent, false);
                    parent.addView(view, index);
                }
            }


            if (countries != null && countries.size() < ProfileConstants.totalNumCountries) {
                setUpPieChart(vh3.getPieChartCountry(), countries.size(), ProfileConstants.totalNumCountries, "Visited Countries");
            } else {
                View view = vh3.getRootView().findViewById(R.id.pieChartCountry);
                if (view != null) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    int index = parent.indexOfChild(view);
                    parent.removeView(view);
                    view = inflater.inflate(R.layout.item_visited_all_countries, parent, false);
                    parent.addView(view, index);
                }
            }

            if (continents != null && continents.size() < ProfileConstants.totalNumContinents) {
                setUpPieChart(vh3.getPieChartContinent(), continents.size(), ProfileConstants.totalNumContinents, "Visited Continents");
            } else {
                View view = vh3.getRootView().findViewById(R.id.pieChartContinent);
                if (view != null) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    int index = parent.indexOfChild(view);
                    parent.removeView(view);
                    view = inflater.inflate(R.layout.item_visited_all_continents, parent, false);
                    parent.addView(view, index);
                }
            }

            final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.pop);
            vh3.getNextButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                    vh3.getViewFlipper().setInAnimation(mContext, R.anim.flipin);
                    vh3.getViewFlipper().setOutAnimation(mContext, R.anim.flipout);
                    vh3.getViewFlipper().showNext();
                }
            });

            vh3.getPreviousButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                    vh3.getViewFlipper().setInAnimation(mContext, R.anim.flipin_reverse);
                    vh3.getViewFlipper().setOutAnimation(mContext, R.anim.flipout_reverse);
                    vh3.getViewFlipper().showPrevious();
                }
            });
        }
    }

    /**
     * Method to configure the no posts view holder
     *
     * @param vh4      view holder to configure
     * @param position position in adapter the no posts item is
     */
    private void configureNoPostsViewHolder(final NoPostsViewHolder vh4, final int position) {
        vh4.getRootView().setTag(mItems.get(position));
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
        pieEntries.add(new PieEntry(total - visited, "Unvisited"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        int[] CUSTOM_COLORS = {
                Color.rgb(125, 187, 201), Color.rgb(64, 89, 128), Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
        };
        pieDataSet.setColors(CUSTOM_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(18);
        int color = ContextCompat.getColor(mContext, R.color.white);
        pieData.setValueTextColor(color);
        Paint p1 = pieChart.getPaint(Chart.PAINT_HOLE);
        int color_primary = ContextCompat.getColor(mContext, R.color.colorPrimary);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(color_primary);
        pieChart.setData(pieData);
        Description d = new Description();
        d.setText("");
        pieChart.setDescription(d);
        pieChart.setDrawCenterText(true);
        pieChart.animateY(1000);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }
}
