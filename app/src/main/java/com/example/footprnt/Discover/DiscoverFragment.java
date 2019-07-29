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
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Fragment for the Discover view
 * Displays all business queried from Yelp API.
 * Businesses displayed: Restaurants, Museums, Hotels, Clubs.
 * @author Stanley Nwakamma, Clarisa Leu
 * @version 1.0
 */
public class DiscoverFragment extends Fragment {
    public static final String TAG = DiscoverFragment.class.getSimpleName();

    RecyclerView rvRestaurants;
    RecyclerView rvMuseums;
    RecyclerView rvHotels;
    RecyclerView rvClubs;
    ListAdapter mAdapterRestaurants;
    ListAdapter mAdapterMuseums;
    ListAdapter mAdapterHotels;
    ListAdapter mAdapterClubs;
    ArrayList<Business> mRestaurants;
    ArrayList<Business> mMuseums;
    ArrayList<Business> mHotels;
    ArrayList<Business> mClubs;
    final YelpService yelpService = new YelpService();
    private LatLng mLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        rvMuseums = view.findViewById(R.id.rvMuseums);
        rvHotels = view.findViewById(R.id.rvHotels);
        rvClubs = view.findViewById(R.id.rvClubs);

        // Fill up lists with query
        mRestaurants = new ArrayList<>();
        mMuseums = new ArrayList<>();
        mHotels = new ArrayList<>();
        mClubs = new ArrayList<>();
        // TODO: fill with current location
        yelpService.findBusinesses("Sunnyvale", Constants.RESTAURANT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mRestaurants = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        rvRestaurants.setLayoutManager(linearLayoutManager);
                        mAdapterRestaurants = new ListAdapter(getContext(), mRestaurants);
                        rvRestaurants.setAdapter(mAdapterRestaurants);
                    }
                });
            }
        });

        yelpService.findBusinesses("Sunnyvale", Constants.MUSEUM, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
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

        yelpService.findBusinesses("Sunnyvale", Constants.HOTEL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mHotels = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        rvHotels.setLayoutManager(linearLayoutManager);
                        mAdapterHotels = new ListAdapter(getContext(), mHotels);
                        rvHotels.setAdapter(mAdapterHotels);
                    }
                });
            }
        });

        yelpService.findBusinesses("Sunnyvale", Constants.CLUB, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mClubs = yelpService.processResults(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        rvClubs.setLayoutManager(linearLayoutManager);
                        mAdapterClubs = new ListAdapter(getContext(), mClubs);
                        rvClubs.setAdapter(mAdapterClubs);
                    }
                });
            }
        });


        return view;

    }

    public void setDataFromMapFragment(LatLng latLng){
        mLocation = latLng;
    }
}