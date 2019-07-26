/*
 * MapFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.example.footprnt.Manifest;
import com.example.footprnt.Map.Util.MapConstants;
import com.example.footprnt.Map.Util.MapUtil;
import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Handles all map activities
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class MapFragment extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    // Map variables
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Util mHelper;
    private boolean mJumpToCurrentLocation = false;
    private JSONObject mContinents;
    private Location mLocation;

    // Display variables
    private CustomInfoWindowAdapter mInfoAdapter;
    ArrayList<Marker> markers;
    private Marker mMarkerShow;
    private MapRipple mMapRipple;
    private ArrayList<MarkerDetails> mMarkerDetails;
    private ImageView mImage;
    private File mPhotoFile;
    private AlertDialog mAlertDialog = null;
    private ParseFile mParseFile;
    private ParseUser mUser;
    private int mMapStyle;
    private Switch mSwitch;

    // Tag variables
    private ArrayList<String> mTags;
    private boolean CULTURE = false;
    private boolean FASHION = false;
    private boolean TRAVEL = false;
    private boolean FOOD = false;
    private boolean NATURE = false;

    // Menu variables
    private ImageView mSettings;
    private PopupMenu mPopup;
    private FragmentActivity myContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        MapsInitializer.initialize(this.getActivity());
        mapFrag.getMapAsync(this);
        mHelper = new Util();
        mUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL();          // set permissions
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        mUser.setACL(acl);
        mUser.setACL(acl);
        mMarkerDetails = new ArrayList<>();
        markers = new ArrayList<>();
        mInfoAdapter = new CustomInfoWindowAdapter(getContext());
        mContinents = MapUtil.getContinents(getActivity());
        mMapStyle = mUser.getInt(MapConstants.map_style);

        // Set up pop up menu
        mSettings = v.findViewById(R.id.ivSettings);
        mPopup = new PopupMenu(getActivity(), mSettings);
        mPopup.getMenuInflater().inflate(R.menu.popup_menu_map, mPopup.getMenu());
        configureMapStyleMenu();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView newPost = getView().findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPostCurrentLocation();
            }
        });
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                if (mJumpToCurrentLocation) {
                    mJumpToCurrentLocation = false;
                    mHelper.centreMapOnLocation(mMap, location, "Your Location");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(mInfoAdapter);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.equals(mMarkerShow)) {
                    mMarkerShow = marker;
                    marker.showInfoWindow();
                } else {
                    marker.hideInfoWindow();
                    mMarkerShow = null;
                }
                return true;
            }
        });
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), mMapStyle));
            if (!success) {
                Log.e("map", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("map", "Can't find style. Error: ", e);
        }

        mJumpToCurrentLocation = true;
        setUpMapIfNeeded();
        loadMarkers();
    }

    private void setUpMapIfNeeded() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (mJumpToCurrentLocation && mLocation != null) {
            mJumpToCurrentLocation = false;
            mHelper.centreMapOnLocation(mMap, mLocation, "Your Location");
        }

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(arg0.getLatitude());
                temp.setLongitude(arg0.getLongitude());
                if (mJumpToCurrentLocation) {
                    mJumpToCurrentLocation = false;
                    mHelper.centreMapOnLocation(mMap, temp, "Your Location");
                }
            }
        });
    }

    /**
     * Loads map markers for all of current user's posts
     */
    public void loadMarkers() {
        mMarkerDetails = new ArrayList<>();
        markers = new ArrayList<>();
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        postQuery.withUser().whereEqualTo("user", mUser);
        postQuery.withUser().whereEqualTo(com.example.footprnt.Util.Constants.user, mUser);
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        MarkerDetails md = objects.get(i);
                        mMarkerDetails.add(md);
                    }
                    for (MarkerDetails markerDetails: mMarkerDetails) {
                        try {
                            Marker m = createMarker(markerDetails);
                            markers.add(m);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void loadAllMarkers(){
        mMarkerDetails = new ArrayList<>();
        markers = new ArrayList<>();
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        postQuery.withUser();
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        MarkerDetails md = objects.get(i);
                        try {
                            Marker m = createMarker(md);
                            markers.add(m);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * Create a Google Map marker at specified point with title and text
     *
     * @param md marker detail
     */
    protected Marker createMarker(MarkerDetails md) throws ParseException {
        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        Post post = (Post) md.getPost();
        double latitude = (post.fetchIfNeeded().getParseGeoPoint("location")).getLatitude();
        double longitude = (post.fetchIfNeeded().getParseGeoPoint("location")).getLongitude();
        String title = (post.fetchIfNeeded().getString("title"));
        ParseFile image = post.fetchIfNeeded().getParseFile("image");
        String imageUrl = "";
        if (image != null){
            imageUrl = image.getUrl();
        }
        Marker m = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(imageUrl)
                .icon(defaultMarker));
        return m;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mJumpToCurrentLocation = true;
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                }
                setUpMapIfNeeded();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(getContext(), mHelper.getAddress(getContext(), latLng), Toast.LENGTH_LONG).show();
        showAlertDialogForPoint(latLng);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMapRipple = new MapRipple(mMap, latLng, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.CYAN)
                .withStrokeColor(Color.BLACK)
                .withDistance(2000)      // 8046.72 for 5 miles
                .withRippleDuration(12000)    //12000ms
                .withTransparency(0.6f);
        mMapRipple.startRippleMapAnimation();      //in onMapReadyCallBack
        Intent i = new Intent(getActivity(), FeedActivity.class);
        i.putExtra("latitude", latLng.latitude);
        i.putExtra("longitude", latLng.longitude);
        startActivity(i);
    }

    /**
     * Shows create post dialog box at the point selected
     *
     * @param point point where post is being created
     */
    private void showAlertDialogForPoint(final LatLng point) {
        View messageView = LayoutInflater.from(getActivity()).inflate(R.layout.message_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(messageView);
        mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
        EditText etDescription = mAlertDialog.findViewById(R.id.etSnippet);
        etDescription.setScroller(new Scroller(getContext()));
        etDescription.setMaxLines(3);
        etDescription.setVerticalScrollBarEnabled(true);
        etDescription.setMovementMethod(new ScrollingMovementMethod());
        ImageView sendPost = mAlertDialog.findViewById(R.id.dropdown);
        ImageView cancelPost = mAlertDialog.findViewById(R.id.cancelPost);
        ImageView ivUpload = mAlertDialog.findViewById(R.id.ivUpload);
        ImageView ivCamera = mAlertDialog.findViewById(R.id.ivCamera);
        mImage = mAlertDialog.findViewById(R.id.image);
        mImage.setVisibility(View.GONE);
        TextView location = mAlertDialog.findViewById(R.id.location);
        location.setText(mHelper.getAddress(getContext(), point));
        final LatLng mLastPoint = point;
        mTags = new ArrayList<>();
        CULTURE = false;
        FASHION = false;
        TRAVEL = false;
        FOOD = false;
        NATURE = false;
        mParseFile = null;
        handleTags();
        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        final Marker temp = mMap.addMarker(new MarkerOptions().position(point).icon(defaultMarker));
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                temp.remove();
            }
        });
        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), com.example.footprnt.Util.Constants.GET_FROM_GALLERY);
            }
        });
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoFile = mHelper.getPhotoFileUri(getActivity(), com.example.footprnt.Util.Constants.photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(getActivity(), com.example.footprnt.Util.Constants.fileProvider, mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, com.example.footprnt.Util.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swipe_two);
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add sound when user sends post
                mp.start();
                final String title = ((EditText) mAlertDialog.findViewById(R.id.etTitle)).getText().toString();
                final String snippet = ((EditText) mAlertDialog.findViewById(R.id.etSnippet)).getText().toString();
                if( TextUtils.isEmpty(title) || TextUtils.isEmpty(snippet)){
                    Toast.makeText(getContext(), R.string.post_incomplete, Toast.LENGTH_SHORT).show();
                } else {
                    final MarkerDetails mOptions = new MarkerDetails();
                    mOptions.setUser(mUser);
                    if (mParseFile != null) {
                        mParseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Post p = createPost(snippet, title, mParseFile, mUser, mLastPoint);
                                mOptions.setPost(p);
                                try {
                                    createMarker(mOptions);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                mOptions.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        loadMarkers();
                                    }
                                });
                            }
                        });
                    } else {
                        Post p = createPost(snippet, title, mParseFile , mUser, mLastPoint);
                        mOptions.setPost(p);
                        try {
                            createMarker(mOptions);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mOptions.saveInBackground();
                    }
                    mAlertDialog.dismiss();
                }
            }
        });
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.cancel();
                temp.remove();
            }
        });
    }

    /**
     * Creates a post at the user's current location
     */
    public void createPostCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = mMap.getMyLocation();
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
//            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
            showAlertDialogForPoint(currLocation);
        }
    }

    /**
     * Creates and uploads post to Parse server
     *
     * @param description content of post
     * @param title       title of post
     * @param imageFile   image uploaded
     * @param user        user who created the post
     * @param point       geopoint where post was created
     */
    private Post createPost(String description, String title, ParseFile imageFile, ParseUser user, LatLng point) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        if (imageFile == null) {
            newPost.remove(com.example.footprnt.Util.Constants.image);
        } else {
            newPost.setImage(imageFile);
        }
        newPost.setUser(user);
        newPost.setTitle(title);
        newPost.setLocation(new ParseGeoPoint(point.latitude, point.longitude));
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String country_code = addresses.get(0).getCountryCode();
                if (city != null) {
                    newPost.setCity(city);
                }
                if (country != null) {
                    newPost.setCountry(country);
                }
                if (country_code != null && mContinents.has(country_code)) {
                    String continent = mContinents.getString(country_code);
                    newPost.setContinent(continent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newPost.setTags(mTags);
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), R.string.post_message, Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
        return newPost;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == com.example.footprnt.Util.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                mImage.setVisibility(View.VISIBLE);
                mImage.setImageBitmap(takenImage);
                File photoFile = mHelper.getPhotoFileUri(getContext(), com.example.footprnt.Util.Constants.photoFileName);
                mParseFile = new ParseFile(photoFile);
            } else {
                mParseFile = null;
                Toast.makeText(getContext(), R.string.camera_message, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap bitmap = null;
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                } catch (Exception e) {
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, com.example.footprnt.Util.Constants.captureImageQuality, stream);
                byte[] image = stream.toByteArray();
                mParseFile = new ParseFile(com.example.footprnt.Util.Constants.imagePath, image);
                final Bitmap finalBitmap = bitmap;
                mImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(finalBitmap).into(mImage);
            } else {
                mParseFile = null;
            }
        }
    }

    /**
     * Handles toggling of tags when in create view dialog
     */
    public void handleTags() {
        final TextView culture = mAlertDialog.findViewById(R.id.culture);
        final TextView food = mAlertDialog.findViewById(R.id.food);
        final TextView fashion = mAlertDialog.findViewById(R.id.fashion);
        final TextView travel = mAlertDialog.findViewById(R.id.travel);
        final TextView nature = mAlertDialog.findViewById(R.id.nature);
        culture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!CULTURE) {
                    culture.setTypeface(null, Typeface.BOLD);
                    mTags.add(MapConstants.culture);
                    CULTURE = true;
                } else {
                    culture.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.culture);
                    CULTURE = false;
                }
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!FOOD) {
                    food.setTypeface(null, Typeface.BOLD);
                    mTags.add(MapConstants.food);
                    FOOD = true;
                } else {
                    food.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.food);
                    FOOD = false;
                }
            }
        });
        fashion.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!FASHION) {
                    fashion.setTypeface(null, Typeface.BOLD);
                    mTags.add(MapConstants.fashion);
                    FASHION = true;
                } else {
                    fashion.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.fashion);
                    FASHION = false;
                }
            }
        });
        travel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!TRAVEL) {
                    travel.setTypeface(null, Typeface.BOLD);
                    mTags.add(MapConstants.travel);
                    TRAVEL = true;
                } else {
                    travel.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.travel);
                    TRAVEL = false;
                }
            }
        });
        nature.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!NATURE) {
                    nature.setTypeface(null, Typeface.BOLD);
                    mTags.add(MapConstants.nature);
                    NATURE = true;
                } else {
                    nature.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.nature);
                    NATURE = false;
                }
            }
        });
    }

    public void handleToggle() {
        mSwitch = getView().findViewById(R.id.switch1);
        mSwitch.setChecked(false);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    mMap.clear();
                    loadAllMarkers();
                } else {
                    mMap.clear();
                    loadMarkers();
                }
            }
        });

        if (mSwitch.isChecked()) {
            mMap.clear();
            loadAllMarkers();
        } else {
            mMap.clear();
            loadMarkers();
        }
    }

    /**
     * Helper function to set up the pop up menu which configures the style for map
     */
    private void configureMapStyleMenu() {
        // Set up initial check boxes in pop up menu
        // TODO: update UI correctly when user opens fragment in beginning and on transition
        for (int i = 0; i < mPopup.getMenu().size(); i++) {
            if (mPopup.getMenu().getItem(i).getItemId() != mMapStyle) {
                mPopup.getMenu().getItem(i).setChecked(false);
            } else {
                mPopup.getMenu().getItem(i).setChecked(true);
            }
        }
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setActionView(new View(getContext()));
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                return false;
                            }

                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {
                                return false;
                            }
                        });
                        switch (item.getItemId()) {
                            case R.id.edit_style_dark_mode:
                                toggleMenuItem(item, MapConstants.style_darkmode);
                                return true;
                            case R.id.edit_style_silver:
                                toggleMenuItem(item, MapConstants.style_silver);
                                return true;
                            case R.id.edit_style_aubergine:
                                toggleMenuItem(item, MapConstants.style_aubergine);
                                return true;
                            case R.id.edit_style_retro:
                                toggleMenuItem(item, MapConstants.style_retro);
                                return true;
                            case R.id.edit_style_basic:
                                toggleMenuItem(item, MapConstants.style_basic);
                                return true;
                        }
                        return false;
                    }
                });
                mPopup.show();
            }
        });
    }

    /**
     * Helper method for onMenuItemSelected. Toggles menu items not selected and updates database
     */
    private void toggleMenuItem(MenuItem menuItem, int id) {
        menuItem.setChecked(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getContext(), id));
        mUser.put(MapConstants.map_style, id);
        mUser.saveInBackground();
        for (int i = 0; i < mPopup.getMenu().size(); i++) {
            if (mPopup.getMenu().getItem(i).getItemId() != menuItem.getItemId()) {
                mPopup.getMenu().getItem(i).setChecked(false);
            }
        }
    }
}