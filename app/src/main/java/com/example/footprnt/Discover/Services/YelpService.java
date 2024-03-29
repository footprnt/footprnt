/*
 * YelpService.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Services;

import android.view.View;
import android.widget.ProgressBar;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Models.Event;
import com.example.footprnt.Discover.Util.DiscoverConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * YelpService is a helper class for making API calls to Yelp and parsing JSON responses from Yelp
 *
 * @author Stanley Nwakamma, Clarisa Leu-Rodriguez
 */
public class YelpService {
    /**
     * Finds <query> in <location>
     * @param location location to query in
     * @param query    type of query to make
     * @param callback response from yelp
     */
    public static void findBusinesses(String location, String query, Callback callback) {
        String url = DiscoverConstants.YELP_BASE_URL + location + "&term=" + query;
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", DiscoverConstants.YELP_TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void findEvents(String location, Callback callback) {
        String url = DiscoverConstants.YELP_EVENT_BASE_URL + location + "&sort_on=time_start";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", DiscoverConstants.YELP_TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<Event> processEvents(Response response) {
        ArrayList<Event> events = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            JSONObject yelpJSON = new JSONObject(jsonData);
            JSONArray eventsJSON = yelpJSON.getJSONArray("events");
            for (int i = 0; i < eventsJSON.length(); i++) {
                JSONObject eventJSONObject = eventsJSON.getJSONObject(i);
                String name = eventJSONObject.getString("name");
                String description = eventJSONObject.getString("description");
                String imageUrl = eventJSONObject.getString("image_url");
                JSONObject location = eventJSONObject.getJSONObject("location");
                String eventId = eventJSONObject.getString("id");
                String timeStart = eventJSONObject.getString("time_start");
                String timeEnd = eventJSONObject.getString("time_end");
                String eventUrl = eventJSONObject.getString("event_site_url");
                Event event = new Event(name, description, imageUrl, location, eventId, timeStart, timeEnd, eventUrl);
                events.add(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Helper function to parse through raw JSON response from Yelp
     *
     * @param response raw response from Yelp API call
     * @return List of restaurants from Yelp in given location
     */
    public ArrayList<Business> processResults(Response response, ProgressBar pbLoader) {
        ArrayList<Business> businesses = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            JSONObject yelpJSON = new JSONObject(jsonData);
            JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
            for (int i = 0; i < businessesJSON.length(); i++) {
                JSONObject businessJSON = businessesJSON.getJSONObject(i);
                String name = businessJSON.getString("name");
                String phone = businessJSON.optString("display_phone", "Phone not available");
                String website = businessJSON.getString("url");
                double rating = businessJSON.getDouble("rating");

                String imageUrl = businessJSON.getString("image_url");

                double latitude = businessJSON.getJSONObject("coordinates").getDouble("latitude");

                double longitude = businessJSON.getJSONObject("coordinates").getDouble("longitude");

                ArrayList<String> address = new ArrayList<>();
                JSONArray addressJSON = businessJSON.getJSONObject("location")
                        .getJSONArray("display_address");
                for (int y = 0; y < addressJSON.length(); y++) {
                    address.add(addressJSON.get(y).toString());
                }

                ArrayList<String> categories = new ArrayList<>();
                JSONArray categoriesJSON = businessJSON.getJSONArray("categories");

                for (int y = 0; y < categoriesJSON.length(); y++) {
                    categories.add(categoriesJSON.getJSONObject(y).getString("title"));
                }
                Business business = new Business(name, phone, website, rating,
                        imageUrl, address, latitude, longitude, categories);
                businesses.add(business);
            }
            pbLoader.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return businesses;
    }
}