/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.footprnt.Discover.Adapters.ListAdapter;
import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Services.YelpService;
import com.example.footprnt.Discover.Util.DiscoverConstants;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Fragment for the Discover view
 * Displays all business queried from Yelp API.
 * Businesses displayed: Restaurants, Museums, Hotels, Clubs.
 *
 * @author Stanley Nwakamma, Clarisa Leu
 * @version 1.0
 */
public class DiscoverFragment extends Fragment implements LocationListener {
        public static final String TAG = DiscoverFragment.class.getSimpleName();
    private SwipeRefreshLayout mSwipeContainer;
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
    private ArrayList<String> arrQueries;
    private ArrayList<RecyclerView> arrRecyclerViews;
    private ArrayList<ListAdapter> arrAdapters;
    private ArrayList<ArrayList<Business>> arrBusinesses;
    private Location mCurrLocation;
    private EditText mSearchText;
    FragmentActivity myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        rvMuseums = view.findViewById(R.id.rvMuseums);
        rvHotels = view.findViewById(R.id.rvHotels);
        rvClubs = view.findViewById(R.id.rvClubs);
        mSearchText = view.findViewById(R.id.searchText);
        handleSearch();
        arrQueries = new ArrayList<>();
        arrRecyclerViews = new ArrayList<>();
        arrAdapters = new ArrayList<>();
        arrBusinesses = new ArrayList<>();
        mRestaurants = new ArrayList<>();
        mMuseums = new ArrayList<>();
        mHotels = new ArrayList<>();
        mClubs = new ArrayList<>();
        try {
            prepareArrayLists();
            populateView();
            mSwipeContainer = view.findViewById(R.id.swipeContainer2);
            mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    RefreshBusinesses();
                }
            });
            mSwipeContainer.setColorSchemeResources(R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4,
                    R.color.refresh_progress_5);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No businesses here", Toast.LENGTH_LONG).show();
        }
        return view;
    }
    public void RefreshBusinesses() {
        arrAdapters.clear();
        prepareArrayLists();
        populateView();
        mSwipeContainer.setRefreshing(false);
    }
    public void populateView() {
        for (int i = 0; i < arrQueries.size(); i++) {
            final int finalI = i;
            String address;
            if (mLocation != null) {
                address = AppUtil.getAddress(getContext(), mLocation);
            } else {
                mLocation = null;
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    Location currLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                    if (currLocation != null) {
                        address = AppUtil.getAddress(getContext(), new LatLng(currLocation.getLatitude(), currLocation.getLongitude()));
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
                        if (mCurrLocation != null) {
                            address = AppUtil.getAddress(getContext(), new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude()));
                        } else {
                            address = "1 Hacker Way Menlo Park, CA 94025";
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No location permission", Toast.LENGTH_LONG).show();
                    address = null;
                }
            }
            if (address != null) {
                yelpService.findBusinesses(address, arrQueries.get(i), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) {
                        ArrayList<Business> arrTemp = yelpService.processResults(response);
                        arrBusinesses.remove(finalI);
                        arrBusinesses.add(finalI, arrTemp);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                arrRecyclerViews.get(finalI).setLayoutManager(linearLayoutManager);
                                ListAdapter arrAdapter = new ListAdapter(getContext(), arrBusinesses.get(finalI));
                                arrAdapters.remove(finalI);
                                arrAdapters.add(finalI, arrAdapter);
                                arrRecyclerViews.get(finalI).setAdapter(arrAdapters.get(finalI));
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * To get a query location from Map fragment
     * @param latLng The location passed from Map
     */
    public void setDataFromMapFragment(LatLng latLng) {
        mLocation = latLng;
        try {
            populateView();
        } catch(Exception e){
            Toast.makeText(getContext(), "No businesses here", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Prepares Array list of queries, Recyclerviews, Adapters and Businesses.
     */
    private void prepareArrayLists() {
        arrQueries = new ArrayList<>(Arrays.asList(DiscoverConstants.RESTAURANT, DiscoverConstants.MUSEUM, DiscoverConstants.HOTEL, DiscoverConstants.CLUB));
        arrRecyclerViews = new ArrayList<>(Arrays.asList(rvRestaurants, rvMuseums, rvHotels, rvClubs));;
        arrAdapters = new ArrayList<>(Arrays.asList(mAdapterRestaurants, mAdapterMuseums, mAdapterHotels, mAdapterClubs));
        arrBusinesses = new ArrayList<>(Arrays.asList(mRestaurants, mMuseums, mHotels, mClubs));
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // not implemented
    }

    @Override
    public void onProviderEnabled(String provider) {
        // not implemented
    }

    @Override
    public void onProviderDisabled(String provider) {
        // not implemented
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void handleSearch(){
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchString = mSearchText.getText().toString();

                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> list = new ArrayList<>();
                    try{
                        list = geocoder.getFromLocationName(searchString, 1);
                    } catch (IOException e) {

                    }

                    if (list.size() > 0) {
                        Address address = list.get(0);
                        mLocation = new LatLng(address.getLatitude(), address.getLongitude());
                        populateView();
                    } else {
                        Toast.makeText(getContext(), "Not valid location", Toast.LENGTH_LONG).show();
                    }
                }
                InputMethodManager inputManager = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(myContext.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
    }
}