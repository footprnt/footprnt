package com.example.footprnt.Map.Util;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        AppUtil helper = new AppUtil();
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
            holder.ivPicture.setVisibility(View.VISIBLE);
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

    /**
     * Parses JSONarray of continents
     *
     * @param activity
     * @return
     */
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

    /**
     * On search result, locates place on map
     *
     * @param searchText
     * @param map
     * @param context
     */
    public static void geoLocate(EditText searchText, GoogleMap map, Context context){
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e){

        }

        if (list.size() > 0){
            Address address = list.get(0);
            Location l = new Location(LocationManager.GPS_PROVIDER);
            l.setLatitude(address.getLatitude());
            l.setLongitude(address.getLongitude());
            centreMapOnLocation(map, l);
        }
    }

    /**
     * Centres camera on given location
     *
     * @param map
     * @param location
     */
    public static void centreMapOnLocation(GoogleMap map, Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    /**
     * Creates post and sends to Parse server
     *
     * @param activity
     * @param context
     * @param description
     * @param title
     * @param imageFile
     * @param user
     * @param point
     * @param tags
     * @return
     */
    public static Post createPost(Activity activity, final Context context, String description, String title, ParseFile imageFile, ParseUser user, LatLng point, ArrayList<String> tags) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        if (imageFile == null) {
            newPost.remove(AppConstants.image);
        } else {
            newPost.setImage(imageFile);
        }
        newPost.setUser(user);
        newPost.setTitle(title);
        newPost.setLocation(new ParseGeoPoint(point.latitude, point.longitude));
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String country_code = addresses.get(0).getCountryCode();
                if (city != null) {
                    newPost.setCity(city);
                }
                if (country != null) {
                    newPost.setCountry(country);
                }
                JSONObject continents = getContinents(activity);
                if (country_code != null && continents.has(country_code)) {
                    String continent = continents.getString(country_code);
                    newPost.setContinent(continent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newPost.setTags(tags);
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Toast.makeText(context, R.string.post_message, Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
        return newPost;
    }

    /**
     * Create a Google Map marker at specified point with given marker details
     *
     * @param md marker detail
     */
    public static Marker createMarker(GoogleMap mMap, MarkerDetails md) throws com.parse.ParseException {
        if(md.getPost()!=null) {
            Post post = (Post) md.getPost();
            double latitude = (post.fetchIfNeeded().getParseGeoPoint("location")).getLatitude();
            double longitude = (post.fetchIfNeeded().getParseGeoPoint("location")).getLongitude();
            String title = (post.fetchIfNeeded().getString("title"));
            ParseFile image = post.fetchIfNeeded().getParseFile("image");
            String imageUrl = "";
            if (image != null) {
                imageUrl = image.getUrl();
            }
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title)
                    .snippet(imageUrl)
                    .icon(MapConstants.DEFAULT_MARKER));
            return m;
        }
        return null;
    }

    /**
     * Hides toolbar for aesthetics
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
     * @param activity
     */
    public static void showToolbar(Activity activity) {
        ConstraintLayout mToolbar = activity.findViewById(R.id.relLayout1);
        View locationButton = ((View) activity.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
    }
}
