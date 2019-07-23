/*
 * MapFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.example.footprnt.Manifest;
import com.example.footprnt.Map.Util.Constants;
import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.Util;
import com.google.android.gms.maps.GoogleMap;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Handles all map activities
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class MapFragment extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    // Map variables
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private LatLng mLastPoint;
    private Util mHelper;
    private boolean mJumpToCurrentLocation = false;
    private JSONObject mContinents;

    // Display variables
    private MapRipple mMapRipple;
    private ArrayList<MarkerDetails> mMarkers;
    private ImageView mImage;
    private File mPhotoFile;
    private AlertDialog mAlertDialog = null;
    private ParseFile mParseFile;
    private ParseUser mUser;

    // Tag variables
    private ArrayList<String> mTags;
    private boolean CULTURE = false;
    private boolean FASHION = false;
    private boolean TRAVEL = false;
    private boolean FOOD = false;
    private boolean NATURE = false;

    int mapStyle;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        configureMapStyle(v);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mHelper = new Util();
        mUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL();          // set permissions
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        mUser.setACL(acl);
        mMarkers = new ArrayList<>();
        try {
            InputStream is = getActivity().getAssets().open("continents.json");
            ;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            mContinents = new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void configureMapStyle(View v){
        final ImageView settings = v.findViewById(R.id.ivSettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), settings);
                popup.getMenuInflater().inflate(R.menu.popup_menu_map, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit_style_darkmode:
                                mapStyle = R.raw.style_json_darkmode;
                                mMap.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                getContext(), mapStyle));
                                return true;
                            case R.id.edit_style_silver:
                                mapStyle = R.raw.style_json_silver;
                                mMap.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                getContext(), mapStyle));
                                return true;
                            default:
                                mapStyle = R.raw.style_json_basic;
                                mMap.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                getContext(), mapStyle));
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView newPost = getView().findViewById(R.id.newPost);
        ImageView findCurrentLoc = getView().findViewById(R.id.findCurrentLoc);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPostCurrentLocation();
            }
        });
        findCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mHelper.centreMapOnLocation(mMap, lastKnownLocation, "Your location");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), mapStyle));
            if (!success) {
                Log.e("map", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("map", "Can't find style. Error: ", e);
        }
        Intent intent = getActivity().getIntent();
        if (intent.getIntExtra("Place Number", 0) == 0) {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
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

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    mHelper.centreMapOnLocation(mMap, lastKnownLocation, "Your Location");
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            loadMarkers();
        }
    }

    /**
     * Loads map markers for all of current user's posts
     */
    public void loadMarkers() {
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        mMarkers = new ArrayList<>();
        postQuery.withUser().whereEqualTo(com.example.footprnt.Util.Constants.user, mUser);
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        mMarkers.add(objects.get(i));
                    }
                    for (MarkerDetails m : mMarkers) {
                        createMarker(m.getLocation().getLatitude(), m.getLocation().getLongitude(), m.getTitle(), m.getDescription());
                    }
                } else {
                    mUser.put("markers", new ArrayList<Marker>());
                    mUser.saveInBackground();
                }
            }
        });
    }

    /**
     * Create a Google Map marker at specified point with title and text
     *
     * @param latitude  latitude of point where placing marker
     * @param longitude longitude of point where placing marker
     * @param title     title of post
     * @param snippet   description of post
     */
    protected void createMarker(double latitude, double longitude, String title, String snippet) {
        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .icon(defaultMarker));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mJumpToCurrentLocation = true;
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMapRipple = new MapRipple(mMap, latLng, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.CYAN)
                .withStrokeColor(Color.BLACK)
                .withDistance(2000)      // 2000 metres radius
                .withRippleDuration(4000)    //12000ms
                .withTransparency(0.6f);
        mMapRipple.startRippleMapAnimation();      //in onMapReadyCallBack
        Toast.makeText(getContext(), mHelper.getAddress(getContext(), latLng), Toast.LENGTH_LONG).show();
        showAlertDialogForPoint(latLng);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMapRipple = new MapRipple(mMap, latLng, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.CYAN)
                .withStrokeColor(Color.BLACK)
                .withDistance(8046.72)      // 2000 metres radius
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
        ImageView sendPost = mAlertDialog.findViewById(R.id.sendPost);
        ImageView cancelPost = mAlertDialog.findViewById(R.id.cancelPost);
        ImageView ivUpload = mAlertDialog.findViewById(R.id.ivUpload);
        ImageView ivCamera = mAlertDialog.findViewById(R.id.ivCamera);
        mImage = mAlertDialog.findViewById(R.id.image);
        mImage.setVisibility(View.GONE);
        TextView location = mAlertDialog.findViewById(R.id.location);
        location.setText(mHelper.getAddress(getContext(), point));
        mLastPoint = point;
        mTags = new ArrayList<>();
        CULTURE = false;
        FASHION = false;
        TRAVEL = false;
        FOOD = false;
        NATURE = false;
        handleTags();
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
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = ((EditText) mAlertDialog.findViewById(R.id.etTitle)).getText().toString();
                final String snippet = ((EditText) mAlertDialog.findViewById(R.id.etSnippet)).getText().toString();
                createMarker(mLastPoint.latitude, mLastPoint.longitude, title, snippet);
                MarkerDetails mOptions = new MarkerDetails();
                mOptions.setLocation(new ParseGeoPoint(mLastPoint.latitude, mLastPoint.longitude));
                mOptions.setDescription(snippet);
                mOptions.setTitle(title);
                mOptions.setUser(mUser);
                mMarkers.add(mOptions);
                mUser.put("markers", mMarkers);
                mUser.saveInBackground();
                if (mParseFile != null) {
                    mParseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            createPost(snippet, title, mParseFile, mUser, mLastPoint);
                        }
                    });
                } else {
                    createPost(snippet, title, mParseFile, mUser, mLastPoint);
                }
                mAlertDialog.dismiss();
            }
        });
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.cancel();
            }
        });
    }

    /**
     * Creates a post at the user's current location
     */
    public void createPostCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng currLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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
    private void createPost(String description, String title, ParseFile imageFile, ParseUser user, LatLng point) {
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
                    Toast.makeText(getContext(), R.string.post_message, Toast.LENGTH_SHORT);
                } else {
                    e.printStackTrace();
                }
            }
        });
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
                    mTags.add(Constants.culture);
                    CULTURE = true;
                } else {
                    culture.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(Constants.culture);
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
                    mTags.add(Constants.food);
                    FOOD = true;
                } else {
                    food.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(Constants.food);
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
                    mTags.add(Constants.fashion);
                    FASHION = true;
                } else {
                    fashion.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(Constants.fashion);
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
                    mTags.add(Constants.travel);
                    TRAVEL = true;
                } else {
                    travel.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(Constants.travel);
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
                    mTags.add(Constants.nature);
                    NATURE = true;
                } else {
                    nature.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(Constants.nature);
                    NATURE = false;
                }
            }
        });
    }
}