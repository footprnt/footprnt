/*
 * YelpService.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Services;

import com.example.footprnt.Discover.Util.Constants;
import com.example.footprnt.Discover.Models.Restaurant;

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
 * @author Stanley Nwakamma
 */
public class YelpService {

    /**
     * Find resteraunts in current location
     *
     * @param location current location (city)
     * @param callback response from yelp
     */
    public static void findRestaurants(String location, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.YELP_LOCATION_QUERY_PARAMETER, location);
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
     * @return List of restaurants from Yelp in given location
     */
    public ArrayList<Restaurant> processResults(Response response) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            JSONObject yelpJSON = new JSONObject(jsonData);
            JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
            for (int i = 0; i < businessesJSON.length(); i++) {
                JSONObject restaurantJSON = businessesJSON.getJSONObject(i);
                String name = restaurantJSON.getString("name");
                String phone = restaurantJSON.optString("display_phone", "Phone not available");
                String website = restaurantJSON.getString("url");
                double rating = restaurantJSON.getDouble("rating");

                String imageUrl = restaurantJSON.getString("image_url");

                double latitude = restaurantJSON.getJSONObject("coordinates").getDouble("latitude");

                double longitude = restaurantJSON.getJSONObject("coordinates").getDouble("longitude");

                ArrayList<String> address = new ArrayList<>();
                JSONArray addressJSON = restaurantJSON.getJSONObject("location")
                        .getJSONArray("display_address");
                for (int y = 0; y < addressJSON.length(); y++) {
                    address.add(addressJSON.get(y).toString());
                }

                ArrayList<String> categories = new ArrayList<>();
                JSONArray categoriesJSON = restaurantJSON.getJSONArray("categories");

                for (int y = 0; y < categoriesJSON.length(); y++) {
                    categories.add(categoriesJSON.getJSONObject(y).getString("title"));
                }
                Restaurant restaurant = new Restaurant(name, phone, website, rating,
                        imageUrl, address, latitude, longitude, categories);
                restaurants.add(restaurant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return restaurants;
    }
}