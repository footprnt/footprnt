package com.example.footprnt.Map.Util;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServerUtil {

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
                JSONObject continents = MapUtil.getContinents(activity);
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
                    Toast.makeText(context, context.getResources().getString(R.string.post_message), Toast.LENGTH_SHORT).show();
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
        if (md.getPost() != null) {
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
}