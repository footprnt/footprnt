/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.footprnt.Discover.Adapters.ListAdapter;
import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Services.YelpService;
import com.example.footprnt.Discover.Util.Constants;
import com.example.footprnt.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Fragment for the Discover view
 *
 * @author Stanley Nwakamma 2019
 * @version 1.0
 */
public class DiscoverFragment extends Fragment {
    public static final String TAG = DiscoverFragment.class.getSimpleName();

    RecyclerView rvRestaurants;
    RecyclerView rvMuseums;
    ListAdapter mAdapterResteraunts;
    ListAdapter mAdapterMuseums;
    ArrayList<Business> mResteraunts;
    ArrayList<Business> mMuseums;
    final YelpService yelpService = new YelpService();


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = getActivity().getIntent();
//        String location = intent.getStringExtra("location");
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        rvMuseums = view.findViewById(R.id.rvMuseums);

        // Fill up lists with query
        mResteraunts = new ArrayList<>();
        mMuseums = new ArrayList<>();
        // TODO: fill with current location
        yelpService.findBusinesses("Scottsdale", Constants.RESTAURANT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResteraunts = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        rvRestaurants.setLayoutManager(linearLayoutManager);
                        mAdapterResteraunts = new ListAdapter(getContext(), mResteraunts);
                        rvRestaurants.setAdapter(mAdapterResteraunts);
                    }
                });
            }
        });

        yelpService.findBusinesses("Scottsdale", Constants.MUSEUM, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMuseums = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        rvMuseums.setLayoutManager(linearLayoutManager);
                        mAdapterMuseums = new ListAdapter(getContext(), mMuseums);
                        rvMuseums.setAdapter(mAdapterMuseums);
                    }
                });
            }
        });




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

//    public void getRestaurants(String location) {
//        final YelpService yelpService = new YelpService();
//        YelpService.findBusinesses(location, Constants.RESTAURANT, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                //display an error message
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                businesses = yelpService.processResults(response);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter = new ListAdapter(getApplicationContext(), businesses);
//                        rvRestaurants = rvRestaurants.findViewById(R.id.rvRestaurants);
//                        RecyclerView.LayoutManager layoutManager =
//                                new LinearLayoutManager(getContext());
//                        rvRestaurants.setLayoutManager(layoutManager);
//                        rvRestaurants.setAdapter(mAdapter);
//                        mAdapter.notifyDataSetChanged();
//                        run();
//
//                    }
//                });
//
//            }
//        });
//    }
}