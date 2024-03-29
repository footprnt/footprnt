/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private Event mAdventure;
    private ProgressBar mProgressBarAdventure;
    private ImageView mEventImage;
    private TextView mEventTitle;
    private TextView mEventDescription;
    private TextView mEventTime;
    private TextView mEventUrl;
    private TextView mSearchingNear;
    private CardView mNoNetwork;
    private ImageView mLocationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Set Views & Initialize
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        mTvRestaurants = view.findViewById(R.id.restaurants);
        rvMuseums = view.findViewById(R.id.rvMuseums);
        mSearchingNear = view.findViewById(R.id.textView13);
        mNoNetwork = view.findViewById(R.id.cvNoNetwork);
        mNoNetwork.setVisibility(View.INVISIBLE);
        mTvMuseums = view.findViewById(R.id.museums);
        rvHotels = view.findViewById(R.id.rvHotels);
        mLocationView = view.findViewById(R.id.btnCall);
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
        mProgressBarAdventure = view.findViewById(R.id.pbLoading2);
        mArrQueries = new ArrayList<>();
        mArrRecyclerViews = new ArrayList<>();
        mArrAdapters = new ArrayList<>();
        mArrBusinesses = new ArrayList<>();
        mRestaurants = new ArrayList<>();
        mMuseums = new ArrayList<>();
        mHotels = new ArrayList<>();
        mClubs = new ArrayList<>();
        mEventImage = view.findViewById(R.id.eventImage);
        mEventTitle = view.findViewById(R.id.eventTitle);
        mEventDescription = view.findViewById(R.id.eventDescrption);
        mEventTime = view.findViewById(R.id.eventStart);
        mEventUrl = view.findViewById(R.id.eventUrl);

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
            mSwipeContainer.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3,
                    R.color.refresh_progress_4, R.color.refresh_progress_5);
        } catch (Exception e) {
            Toast.makeText(getContext(), DiscoverConstants.NO_BUSINESS_MESSAGE, Toast.LENGTH_LONG).show();
        }
        return view;
    }

    /**
     * Helper method to refresh RV
     */
    public void RefreshBusinesses() {
        if (AppUtil.haveNetworkConnection(getContext())) {
            mNoNetwork.setVisibility(View.INVISIBLE);
            mAddress.setVisibility(View.VISIBLE);
            mSearchingNear.setVisibility(View.VISIBLE);
            mLocationView.setVisibility(View.VISIBLE);
            mArrAdapters.clear();
            prepareArrayLists();
            populateView();
            getAdventureOfTheDay();
            mSwipeContainer.setRefreshing(false);
        } else {
            mNoNetwork.setVisibility(View.VISIBLE);
            mAddress.setVisibility(View.INVISIBLE);
            mSearchingNear.setVisibility(View.INVISIBLE);
            mLocationView.setVisibility(View.INVISIBLE);
            mSwipeContainer.setRefreshing(false);
        }
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
                mBusinessAddress = null;
            }
        }
    }

    private int numBusinesses = 0;

    /**
     * Helper method to populate business views
     */
    public void populateView() {
        numBusinesses = 0;
        mTvRestaurants.setVisibility(View.VISIBLE);
        mTvMuseums.setVisibility(View.VISIBLE);
        mTvClubs.setVisibility(View.VISIBLE);
        mTvHotels.setVisibility(View.VISIBLE);
        rvRestaurants.setVisibility(View.VISIBLE);
        rvMuseums.setVisibility(View.VISIBLE);
        rvClubs.setVisibility(View.VISIBLE);
        rvHotels.setVisibility(View.VISIBLE);
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
                        numBusinesses += arrTemp.size();
                        mArrBusinesses.remove(finalI);
                        mArrBusinesses.add(finalI, arrTemp);
                        if (numBusinesses == 0 && finalI == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mNothingNearYou.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    mTvRestaurants.setVisibility(View.INVISIBLE);
                                    mTvMuseums.setVisibility(View.INVISIBLE);
                                    mTvClubs.setVisibility(View.INVISIBLE);
                                    mTvHotels.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                mArrRecyclerViews.get(finalI).setLayoutManager(linearLayoutManager);
                                ListAdapter arrAdapter = new ListAdapter(getContext(), mArrBusinesses.get(finalI));
                                mArrAdapters.remove(finalI);
                                mArrAdapters.add(finalI, arrAdapter);
                                mArrRecyclerViews.get(finalI).setAdapter(mArrAdapters.get(finalI));

                                if (finalI == 0) {
                                    if (mArrBusinesses.get(finalI).size() == 0) {
                                        mTvRestaurants.setVisibility(View.GONE);
                                        rvRestaurants.setVisibility(View.GONE);
                                    } else {
                                        mTvRestaurants.setVisibility(View.VISIBLE);
                                        rvRestaurants.setVisibility(View.VISIBLE);
                                    }
                                } else if (finalI == 1) {
                                    if (mArrBusinesses.get(finalI).size() == 0) {
                                        mTvMuseums.setVisibility(View.GONE);
                                        rvMuseums.setVisibility(View.GONE);
                                    } else {
                                        mTvMuseums.setVisibility(View.VISIBLE);
                                        rvMuseums.setVisibility(View.VISIBLE);
                                    }
                                } else if (finalI == 2) {
                                    if (mArrBusinesses.get(finalI).size() == 0) {
                                        mTvHotels.setVisibility(View.GONE);
                                        rvHotels.setVisibility(View.GONE);
                                    } else {
                                        mTvHotels.setVisibility(View.VISIBLE);
                                        rvHotels.setVisibility(View.VISIBLE);
                                    }
                                } else if (finalI == 3) {
                                    if (mArrBusinesses.get(finalI).size() == 0) {
                                        mTvClubs.setVisibility(View.GONE);
                                        rvClubs.setVisibility(View.GONE);
                                    } else {
                                        mTvClubs.setVisibility(View.VISIBLE);
                                        rvClubs.setVisibility(View.VISIBLE);
                                    }

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
                    } catch (IOException ignored) {

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
        mProgressBarAdventure.setVisibility(View.VISIBLE);
        mEventImage.setVisibility(View.INVISIBLE);
        mEventTitle.setVisibility(View.INVISIBLE);
        mEventDescription.setVisibility(View.INVISIBLE);
        mEventTime.setVisibility(View.INVISIBLE);
        mEventUrl.setVisibility(View.INVISIBLE);
        if (mNoEvent != null) {
            mNoEvent.setVisibility(View.INVISIBLE);
        }

        yelpService.findEvents(mBusinessAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                final ArrayList<Event> arrTemp = yelpService.processEvents(response);


                final CardView adventure = getActivity().findViewById(R.id.cvAdventure);
                final ImageButton completed = adventure.findViewById(R.id.check);
                final ImageButton cancel = adventure.findViewById(R.id.cancel);
                completed.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                final TextView eventHeader = adventure.findViewById(R.id.tvAdventure);
                if (!arrTemp.isEmpty()) {
                    ArrayList<String> userCompletedAdventures = ((ArrayList<String>) ParseUser.getCurrentUser().get(AppConstants.completed_adventure));
                    ArrayList<String> userUncompletedAdventures = ((ArrayList<String>) ParseUser.getCurrentUser().get(AppConstants.uncompleted_adventure));
                    if (userCompletedAdventures == null) {
                        userCompletedAdventures = new ArrayList<>();
                    }
                    if (userUncompletedAdventures == null) {
                        userUncompletedAdventures = new ArrayList<>();
                    }
                    Event prevAdventure = mAdventure;
                    for (Event event : arrTemp) {
                        if (userCompletedAdventures.size() == 0 && userUncompletedAdventures.size() == 0) {
                            mAdventure = event;
                            break;
                        } else if (!userCompletedAdventures.contains(event.getEventId()) && !userUncompletedAdventures.contains(event.getEventId())) {
                            mAdventure = event;
                            break;
                        }
                    }
                    if (mAdventure != null && prevAdventure != mAdventure) {
                        mFragmentContext.runOnUiThread(new Runnable() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void run() {
                                String imageUrl = mAdventure.getImageUrl();
                                if (imageUrl != null && imageUrl.length() > 0) {
                                    mEventImage.setVisibility(View.VISIBLE);
                                    try {
                                        Glide.with(mFragmentContext).load(imageUrl).apply(RequestOptions.circleCropTransform()).into(mEventImage);
                                    } catch (Exception e) {
                                        mEventImage.setVisibility(View.GONE);
                                    }
                                } else {
                                    mEventImage.setVisibility(View.GONE);
                                }
                                String title = mAdventure.getName();
                                if (title != null && title.length() > 0) {
                                    mEventTitle.setVisibility(View.VISIBLE);
                                    mEventTitle.setText(title);
                                } else {
                                    mEventTitle.setVisibility(View.GONE);
                                }
                                String description = mAdventure.getDescription();
                                if (description != null && description.length() > 0) {
                                    mEventDescription.setVisibility(View.VISIBLE);
                                    mEventDescription.setText(description);
                                } else {
                                    mEventDescription.setVisibility(View.GONE);
                                }
                                String time = mAdventure.getTimeStart();
                                if (time != null && time.length() > 0) {
                                    mEventTime.setVisibility(View.VISIBLE);
                                    String dateDisplay = time.substring(0, 10);
                                    mEventTime.setText(dateDisplay);
                                } else {
                                    mEventTime.setVisibility(View.GONE);
                                }
                                final String yelpUrl = mAdventure.getEventUrl();
                                if (yelpUrl != null && yelpUrl.length() > 0) {
                                    mEventUrl.setVisibility(View.VISIBLE);
                                    mEventUrl.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(yelpUrl));
                                            ((Activity) getContext()).startActivityForResult(i, AppConstants.VIEW_BUSINESS_PAGE);
                                            return true;
                                        }
                                    });
                                } else {
                                    mEventUrl.setVisibility(View.GONE);
                                }
                                mNoEvent.setVisibility(View.INVISIBLE);
                                mProgressBarAdventure.setVisibility(View.INVISIBLE);
                                adventure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(adventure, "scaleX", 1f, 0f);
                                        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(adventure, "scaleX", 0f, 1f);
                                        oa1.setInterpolator(new DecelerateInterpolator());
                                        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                                        oa1.setDuration(100);
                                        oa2.setDuration(100);
                                        oa1.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                completed.setVisibility(View.VISIBLE);
                                                cancel.setVisibility(View.VISIBLE);
                                                completed.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ArrayList<String> userCompletedAdventures = ((ArrayList<String>) ParseUser.getCurrentUser().get("completed_adventure"));
                                                        if (userCompletedAdventures != null) {
                                                            userCompletedAdventures.add(mAdventure.getEventId());
                                                        } else {
                                                            userCompletedAdventures = new ArrayList<>();
                                                            userCompletedAdventures.add(mAdventure.getEventId());
                                                        }
                                                        ParseUser.getCurrentUser().put("completed_adventure", userCompletedAdventures);
                                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                completed.setVisibility(View.INVISIBLE);
                                                                cancel.setVisibility(View.INVISIBLE);
                                                                getAdventureOfTheDay();
                                                            }
                                                        });
                                                    }
                                                });
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ArrayList<String> userUncompletedAdventure = ((ArrayList<String>) ParseUser.getCurrentUser().get("uncompleted_adventure"));
                                                        if (userUncompletedAdventure != null) {
                                                            userUncompletedAdventure.add(mAdventure.getEventId());
                                                        } else {
                                                            userUncompletedAdventure = new ArrayList<>();
                                                            userUncompletedAdventure.add(mAdventure.getEventId());
                                                        }
                                                        ParseUser.getCurrentUser().put("uncompleted_adventure", userUncompletedAdventure);
                                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                completed.setVisibility(View.INVISIBLE);
                                                                cancel.setVisibility(View.INVISIBLE);
                                                                getAdventureOfTheDay();
                                                            }
                                                        });
                                                    }
                                                });
                                                mEventImage.setVisibility(View.INVISIBLE);
                                                mEventTitle.setVisibility(View.INVISIBLE);
                                                mEventDescription.setVisibility(View.INVISIBLE);
                                                mEventTime.setVisibility(View.INVISIBLE);
                                                mEventUrl.setVisibility(View.INVISIBLE);
                                                eventHeader.setVisibility(View.INVISIBLE);
                                                oa2.start();
                                            }
                                        });
                                        oa1.start();
                                    }
                                });
                            }
                        });
                    } else {
                        mFragmentContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mEventImage.setVisibility(View.INVISIBLE);
                                mEventTitle.setVisibility(View.INVISIBLE);
                                mEventDescription.setVisibility(View.INVISIBLE);
                                mEventTime.setVisibility(View.INVISIBLE);
                                mEventUrl.setVisibility(View.INVISIBLE);
                                mNoEvent.setVisibility(View.VISIBLE);
                                mProgressBarAdventure.setVisibility(View.INVISIBLE);
                                adventure.setClickable(false);
                            }
                        });

                    }
                } else {
                    mFragmentContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEventImage.setVisibility(View.INVISIBLE);
                            mEventTitle.setVisibility(View.INVISIBLE);
                            mEventDescription.setVisibility(View.INVISIBLE);
                            mEventTime.setVisibility(View.INVISIBLE);
                            mEventUrl.setVisibility(View.INVISIBLE);
                            mNoEvent.setVisibility(View.VISIBLE);
                            mProgressBarAdventure.setVisibility(View.INVISIBLE);
                            adventure.setClickable(false);

                        }
                    });
                }
            }
        });
    }
}