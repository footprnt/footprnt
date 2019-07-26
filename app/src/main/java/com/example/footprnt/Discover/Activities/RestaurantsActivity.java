package com.example.footprnt.Discover.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.footprnt.Discover.Models.Restaurant;
import com.example.footprnt.Discover.Services.YelpService;
import com.example.footprnt.Discover.Adapters.RestaurantListAdapter;
import com.example.footprnt.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RestaurantsActivity extends AppCompatActivity {

    //@BindView(R.id.rvRestaurants)
    RecyclerView rvRestaurants;

    RestaurantListAdapter mAdapter;
    ArrayList<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        String zipcode;
        zipcode = getIntent().getStringExtra("zipcode");

        getRestaurants(zipcode);
    }

    private void getRestaurants(String location) {
        final YelpService yelpService = new YelpService();
        YelpService.findRestaurants(location, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) {
                restaurants = yelpService.processResults(response);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        rvRestaurants = findViewById(R. id. rvRestaurants);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(RestaurantsActivity.this);
                        rvRestaurants.setLayoutManager(layoutManager);
                        rvRestaurants.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                });
            }

        });
    }
}
