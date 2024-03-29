package com.example.footprnt.Map.Util;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Contains all UI utilities for map fragment
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class UiUtil {

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
    public static void setPostText(Post post, PostAdapter.ViewHolder holder, Context context, Boolean privacy) {
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
        if (privacy != null && privacy) {
            holder.tvUser.setText(MapConstants.ANONYMOUS);
        } else {
            holder.tvUser.setText(post.getUser().getUsername());
        }
        // set date
        String dateText = getPostDateText(post);
        holder.tvTimePosted.setText(dateText);
        // set location
        AppUtil helper = new AppUtil();
        LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        String tvCityState = helper.getAddress(context, point);
        holder.tvLocation.setText(tvCityState);
        // set tags
        String tags = getPostTags(post);
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
    public static void setPostImages(Post post, PostAdapter.ViewHolder holder, Context context, Boolean privacy) {
        if (post.getImage() != null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(context).load(imgUrl).into(holder.ivPicture);
            holder.ivPicture.setVisibility(View.VISIBLE);
        } else {
            holder.ivPicture.setVisibility(View.GONE);
        }
        if ((post.getUser().getParseFile(context.getResources().getString(R.string.profile_image)) != null && !privacy) || (post.getUser().getParseFile(context.getResources().getString(R.string.profile_image)) != null && privacy == null)) {
            String userImgUrl = post.getUser().getParseFile(context.getResources().getString(R.string.profile_image)).getUrl();
            Glide.with(context).load(userImgUrl).apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        } else {
            Glide.with(context).load(MapConstants.PLACEHOLDER_IMAGE).apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        }
    }

    /**
     * Hides toolbar for aesthetics
     *
     * @param activity
     */
    public static void hideToolBar(Activity activity) {
        ConstraintLayout mToolbar = activity.findViewById(R.id.relLayout1);
        View locationButton = ((View) activity.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows toolbar again
     *
     * @param activity
     */
    public static void showToolbar(Activity activity) {
        ConstraintLayout mToolbar = activity.findViewById(R.id.relLayout1);
        View locationButton = ((View) activity.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
    }
}