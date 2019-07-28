/*
 * YelpService.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Services;

import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * YelpService for making API calls to Yelp
 *
 * @author Stanley Nwakamma, 2019
 */
public class YelpService {

    /**
     * Finds <query> in <location>
     *
     * @param location location to query in
     * @param query    type of query to make
     * @param callback response from yelp
     */

    public static void findBusinesses(String location, String query, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL + location + "&term=" + query).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", Constants.YELP_TOKEN)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * Helper function to parse through raw JSON response from Yelp
     *
     * @param response raw response from Yelp API call
     * @return List of businesses from Yelp in given location
     */
    public ArrayList<Business> processResults(Response response) {

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return businesses;
    }
}