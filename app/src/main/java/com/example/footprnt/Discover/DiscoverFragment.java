/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Discover.Adapters.ListAdapter;
import com.example.footprnt.Discover.Models.Business;
import com.example.footprnt.Discover.Models.Event;
import com.example.footprnt.Discover.Services.YelpService;
import com.example.footprnt.Discover.Util.DiscoverConstants;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
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
 * @author Stanley Nwakamma, Clarisa Leu, Jocelyn Shen
 * @version 1.0
 */
public class DiscoverFragment extends Fragment implements LocationListener {

    public static final String TAG = DiscoverFragment.class.getSimpleName();  // For logcat
    FragmentActivity mFragmentContext;

    // Querying Yelp:
    final YelpService yelpService = new YelpService();
    private LatLng mLocation;
    ArrayList<Business> mRestaurants;
    ArrayList<Business> mMuseums;
    ArrayList<Business> mHotels;
    ArrayList<Business> mClubs;

    // Views:
    private SwipeRefreshLayout mSwipeContainer;
    RecyclerView rvRestaurants;
    RecyclerView rvMuseums;
    RecyclerView rvHotels;
    RecyclerView rvClubs;
    ListAdapter mAdapterRestaurants;
    ListAdapter mAdapterMuseums;
    ListAdapter mAdapterHotels;
    ListAdapter mAdapterClubs;
    private TextView mAddress;
    private TextView mNoEvent;
    String mBusinessAddress;
    private TextView mNothingNearYou;
    private TextView mTvRestaurants;
    private TextView mTvMuseums;
    private TextView mTvClubs;
    private TextView mTvHotels;
    private Location mCurrLocation;
    private EditText mSearchText;
    private ArrayList<String> mArrQueries;
    private ArrayList<RecyclerView> mArrRecyclerViews;
    private ArrayList<ListAdapter> mArrAdapters;
    private ArrayList<ArrayList<Business>> mArrBusinesses;
    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        // Set Views & Initialize
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        mTvRestaurants = view.findViewById(R.id.restaurants);
        rvMuseums = view.findViewById(R.id.rvMuseums);
        mTvMuseums = view.findViewById(R.id.museums);
        rvHotels = view.findViewById(R.id.rvHotels);
        mTvHotels = view.findViewById(R.id.hotels);
        rvClubs = view.findViewById(R.id.rvClubs);
        mTvClubs = view.findViewById(R.id.clubs);
        mSearchText = view.findViewById(R.id.searchText);
        mAddress = view.findViewById(R.id.address);
        mNoEvent = view.findViewById(R.id.noEvent);
        mNoEvent.setVisibility(View.INVISIBLE);
        mNothingNearYou = view.findViewById(R.id.nothingNearYou);
        mNothingNearYou.setVisibility(View.INVISIBLE);
        mProgressBar = view.findViewById(R.id.pbLoading);
        mArrQueries = new ArrayList<>();
        mArrRecyclerViews = new ArrayList<>();
        mArrAdapters = new ArrayList<>();
        mArrBusinesses = new ArrayList<>();
        mRestaurants = new ArrayList<>();
        mMuseums = new ArrayList<>();
        mHotels = new ArrayList<>();
        mClubs = new ArrayList<>();

        // Handle user searching:
        handleSearch();
        try {
            getAddress();
            getAdventureOfTheDay();
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
            Toast.makeText(getContext(), DiscoverConstants.NO_BUSINESS_MESSAGE, Toast.LENGTH_LONG).show();
        }
        return view;
    }

    /**
     * Helper method to refresh RV
     */
    public void RefreshBusinesses() {
        mArrAdapters.clear();
        prepareArrayLists();
        populateView();
        mSwipeContainer.setRefreshing(false);
    }

    /**
     * Helper method to get address
     */
    public void getAddress() {
        if (mLocation != null) {
            mBusinessAddress = AppUtil.getAddress(getContext(), mLocation);
        } else {
            mLocation = null;
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location currLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (currLocation != null) {
                    mBusinessAddress = AppUtil.getAddress(getContext(), new LatLng(currLocation.getLatitude(), currLocation.getLongitude()));
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
                    if (mCurrLocation != null) {
                        mBusinessAddress = AppUtil.getAddress(getContext(), new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude()));
                    } else {
                        // Default location in case location search doesn't work on first try
                        mBusinessAddress = DiscoverConstants.DEFAULT_LOCATION;
                    }
                }
            } else {
                Toast.makeText(getContext(), "No location permission", Toast.LENGTH_LONG).show();
                mBusinessAddress = null;
            }
        }
    }

    /**
     * Helper method to populate business views
     */
    public void populateView() {
        mNothingNearYou.setVisibility(View.INVISIBLE);
        for (int i = 0; i < mArrQueries.size(); i++) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            final int finalI = i;
            getAddress();
            if (mBusinessAddress != null) {
                mAddress.setText(mBusinessAddress);
                yelpService.findBusinesses(mBusinessAddress, mArrQueries.get(i), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        final ArrayList<Business> arrTemp = yelpService.processResults(response, mProgressBar);
                        mArrBusinesses.remove(finalI);
                        mArrBusinesses.add(finalI, arrTemp);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                mArrRecyclerViews.get(finalI).setLayoutManager(linearLayoutManager);
                                ListAdapter arrAdapter = new ListAdapter(getContext(), mArrBusinesses.get(finalI));
                                mArrAdapters.remove(finalI);
                                mArrAdapters.add(finalI, arrAdapter);
                                mArrRecyclerViews.get(finalI).setAdapter(mArrAdapters.get(finalI));
                                if (arrTemp.size() == 0) {
                                    mTvRestaurants.setVisibility(View.GONE);
                                    mTvMuseums.setVisibility(View.GONE);
                                    mTvClubs.setVisibility(View.GONE);
                                    mTvHotels.setVisibility(View.GONE);
                                    mNothingNearYou.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * To get a query location from Map fragment
     *
     * @param latLng The location passed from Map
     */
    public void setDataFromMapFragment(LatLng latLng) {
        mLocation = latLng;
        try {
            populateView();
            getAdventureOfTheDay();
        } catch (Exception e) {
            Toast.makeText(getContext(), DiscoverConstants.NO_BUSINESS_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Prepares Array list of queries, Recyclerviews, Adapters and Businesses.
     */
    private void prepareArrayLists() {
        mArrQueries = new ArrayList<>(Arrays.asList(DiscoverConstants.RESTAURANT, DiscoverConstants.MUSEUM, DiscoverConstants.HOTEL, DiscoverConstants.CLUB));
        mArrRecyclerViews = new ArrayList<>(Arrays.asList(rvRestaurants, rvMuseums, rvHotels, rvClubs));
        ;
        mArrAdapters = new ArrayList<>(Arrays.asList(mAdapterRestaurants, mAdapterMuseums, mAdapterHotels, mAdapterClubs));
        mArrBusinesses = new ArrayList<>(Arrays.asList(mRestaurants, mMuseums, mHotels, mClubs));
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
        mFragmentContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void handleSearch() {
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchString = mSearchText.getText().toString();

                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> list = new ArrayList<>();
                    try {
                        list = geocoder.getFromLocationName(searchString, 1);
                    } catch (IOException e) {

                    }

                    if (list.size() > 0) {
                        Address address = list.get(0);
                        mLocation = new LatLng(address.getLatitude(), address.getLongitude());
                        populateView();
                        getAdventureOfTheDay();
                    } else {
                        Toast.makeText(getContext(), DiscoverConstants.NOT_VALID_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                }
                InputMethodManager inputManager = (InputMethodManager) mFragmentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mFragmentContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
    }

    public void getAdventureOfTheDay() {
        yelpService.findEvents(mBusinessAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                final ArrayList<Event> arrTemp = yelpService.processEvents(response);
                final ImageView eventImage = getActivity().findViewById(R.id.eventImage);
                final TextView eventTitle = getActivity().findViewById(R.id.eventTitle);
                final TextView eventDescription = getActivity().findViewById(R.id.eventDescrption);
                final TextView eventTime = getActivity().findViewById(R.id.eventStart);
                final TextView eventUrl = getActivity().findViewById(R.id.eventUrl);
                if (arrTemp.size() > 0) {
                    final Event e = arrTemp.get(0);
                    mFragmentContext.runOnUiThread(new Runnable() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void run() {
                            String imageUrl = e.getImageUrl();
                            if (imageUrl != null && imageUrl.length() > 0) {
                                eventImage.setVisibility(View.VISIBLE);
                                try {
                                    Glide.with(mFragmentContext).load(imageUrl).apply(RequestOptions.circleCropTransform()).into(eventImage);
                                } catch (Exception e) {
                                    eventImage.setVisibility(View.GONE);
                                }
                            } else {
                                eventImage.setVisibility(View.GONE);
                            }
                            String title = e.getName();
                            if (title != null && title.length() > 0) {
                                eventTitle.setVisibility(View.VISIBLE);
                                eventTitle.setText(title);
                            } else {
                                eventTitle.setVisibility(View.GONE);
                            }
                            String description = e.getDescription();
                            if (description != null && description.length() > 0) {
                                eventDescription.setVisibility(View.VISIBLE);
                                eventDescription.setText(description);
                            } else {
                                eventDescription.setVisibility(View.GONE);
                            }
                            String time = e.getTimeStart();
                            if (time != null && time.length() > 0) {
                                eventTime.setVisibility(View.VISIBLE);
                                String dateDisplay = time.substring(0, 10);
                                eventTime.setText(dateDisplay);
                            } else {
                                eventTime.setVisibility(View.GONE);
                            }
                            final String yelpUrl = e.getEventUrl();
                            if (yelpUrl != null && yelpUrl.length() > 0) {
                                eventUrl.setVisibility(View.VISIBLE);
                                eventUrl.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(yelpUrl));
                                        ((Activity) getContext()).startActivityForResult(i, AppConstants.VIEW_BUSINESS_PAGE);
                                        return true;
                                    }
                                });
                            } else {
                                eventUrl.setVisibility(View.GONE);
                            }
                            mNoEvent.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    mFragmentContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventImage.setVisibility(View.INVISIBLE);
                            eventTitle.setVisibility(View.INVISIBLE);
                            eventDescription.setVisibility(View.INVISIBLE);
                            eventTime.setVisibility(View.INVISIBLE);
                            eventUrl.setVisibility(View.INVISIBLE);
                            mNoEvent.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }
}