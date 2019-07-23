/*
 * MapUtil.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map.Util;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Map.PostAdapter;
import com.example.footprnt.Models.Post;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Contains all utilities for map fragment
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class MapUtil {

    /**
     * Calculates relative time
     *
     * @param rawJsonDate input date to convert
     * @return relative time
     */
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    /**
     * Gets a post's date text
     *
     * @param post post to retrieve date from
     * @return post's date text
     */
    public static String getPostDateText(Post post) {
        Date d = post.getCreatedAt();
        String dateText;
        if (d == null) {
            dateText = "0s";
        } else {
            dateText = getRelativeTimeAgo(d.toString());
        }
        return dateText;
    }

    /**
     * Gets a post's tags
     *
     * @param post post to retrieve tags from
     * @return string format of tag (ie. #culture #food)
     */
    public static String getPostTags(Post post) {
        String tagname = "";
        JSONArray arr = post.getTags();
        if (arr != null && arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    tagname += "#" + arr.getString(i) + " ";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return tagname;
        } else {
            return null;
        }
    }

    /**
     * Handles displaying all text into the view holder
     *
     * @param post    post to display
     * @param holder  holder to display text into
     * @param context current context of post
     */
    public static void setPostText(Post post, PostAdapter.ViewHolder holder, Context context) {
        // set description
        String description = post.getDescription();
        if (description.length() > 0) {
            holder.tvDescription.setText(description);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        // set title
        String title = post.getTitle();
        if (title != null && title.length() > 0) {
            holder.tvTitle.setText(title);
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        // set user
        holder.tvUser.setText(post.getUser().getUsername());
        // set date
        String dateText = MapUtil.getPostDateText(post);
        holder.tvTimePosted.setText(dateText);
        // set location
        com.example.footprnt.Util.Util helper = new com.example.footprnt.Util.Util();
        LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        String tvCityState = helper.getAddress(context, point);
        holder.tvLocation.setText(tvCityState);
        // set tags
        String tags = MapUtil.getPostTags(post);
        if (tags != null) {
            holder.tvTags.setText(tags);
        } else {
            holder.tvTags.setVisibility(View.GONE);
        }
    }

    /**
     * Handles displaying all images into the view holder
     *
     * @param post    post to display
     * @param holder  holder to display images into
     * @param context current context of post
     */
    public static void setPostImages(Post post, PostAdapter.ViewHolder holder, Context context) {
        if(post.getImage()!=null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(context).load(imgUrl).into(holder.ivPicture);
        } else {
            holder.ivPicture.setVisibility(View.GONE);
        }
        if (post.getUser().getParseFile("profileImg") != null) {
            String userImgUrl = post.getUser().getParseFile("profileImg").getUrl();
            Glide.with(context).load(userImgUrl).apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        } else {
            Glide.with(context).load("http://via.placeholder.com/300.png").apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        }
    }

    public static JSONObject getContinents(Activity activity){
        JSONObject mContinents = new JSONObject();
        try{
            InputStream is = activity.getAssets().open("continents.json");;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            mContinents = new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mContinents;
    }

}
