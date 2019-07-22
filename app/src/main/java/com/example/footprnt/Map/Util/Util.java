package com.example.footprnt.Map.Util;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Map.PostAdapter;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Util.LocationHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String getRelativeTimeAgo(String rawJsonDate) {
        /*
        Calculates relative time
         */
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

    public static String getPostDateText(Post post) {
        Date d = post.getCreatedAt();
        String dateText;
        if (d==null) {
            dateText = "0s";
        } else {
            dateText = getRelativeTimeAgo(d.toString());
        }
        return dateText;
    }

    public static String getPostTags(Post post) {
        String tagname = "";
        JSONArray arr = post.getTags();
        if (arr != null && arr.length() >0 ) {
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
        String dateText = Util.getPostDateText(post);
        holder.tvTimePosted.setText(dateText);
        // set location
        LocationHelper helper = new LocationHelper();
        LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        String tvCityState = helper.getAddress(context, point);
        holder.tvLocation.setText(tvCityState);
        // set tags
        String tags = Util.getPostTags(post);
        if(tags != null) {
            holder.tvTags.setText(tags);
        } else {
            holder.tvTags.setVisibility(View.GONE);
        }
    }

    public static void setPostImages(Post post, PostAdapter.ViewHolder holder, Context context) {
        // set images
        if(post.getImage()!=null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(context).load(imgUrl).into(holder.ivPicture);
        } else {
            holder.ivPicture.setVisibility(View.GONE);
        }
        if(post.getUser().getParseFile("profileImg")!=null) {
            String userImgUrl = post.getUser().getParseFile("profileImg").getUrl();
            Glide.with(context).load(userImgUrl).apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        } else {
            Glide.with(context).load("http://via.placeholder.com/300.png").apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        }
    }

}
