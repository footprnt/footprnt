/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.footprnt.Discover.Models.Restaurant;
import com.example.footprnt.Discover.Services.YelpService;
import com.example.footprnt.Discover.adapters.RestaurantListAdapter;
import com.example.footprnt.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.parse.Parse.getApplicationContext;


/**
 * Fragment for the Discover view
 *
 * @author Stanley Nwakamma 2019
 * @version 1.0
 */
public class DiscoverFragment extends Fragment {
    public static final String TAG = DiscoverFragment.class.getSimpleName();

    RecyclerView rvRestaurants;
    RestaurantListAdapter mAdapter;
    ArrayList<Restaurant> restaurants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        String baltimore = intent.getStringExtra("baltimore");
        getRestaurants(baltimore);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        restaurants = new ArrayList<>();
        mAdapter = new RestaurantListAdapter(getContext(), restaurants);
        rvRestaurants.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRestaurants.setLayoutManager(linearLayoutManager);
         //etZip= view.findViewById(R.id.etZip);
        //ArrayList<Restaurant> restaurants = new ArrayList<>();
        return view;
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        btnFindRestaurants = view.findViewById(R.id.btnFindRestaurants);
//        btnFindRestaurants.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), RestaurantsActivity.class);
//                i.putExtra("zipcode", etZip.getText().toString());
//                startActivity(i);
//            }
//        });
//    }

    public void getRestaurants(String baltimore){
        final YelpService yelpService = new YelpService();
        YelpService.findRestaurants(baltimore, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //display an error message
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                restaurants = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        rvRestaurants = rvRestaurants.findViewById(R.id.rvRestaurants);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(getContext());
                        rvRestaurants.setLayoutManager(layoutManager);
                        rvRestaurants.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                    }
                });

            }
        });
    }
}