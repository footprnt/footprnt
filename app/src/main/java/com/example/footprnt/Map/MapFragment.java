package com.example.footprnt.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.example.footprnt.Discover.DiscoverFragment;
import com.example.footprnt.HomeActivity;
import com.example.footprnt.Manifest;
import com.example.footprnt.Map.Util.CustomInfoWindowAdapter;
import com.example.footprnt.Map.Util.MapConstants;
import com.example.footprnt.Map.Util.MapUtil;
import com.example.footprnt.Map.Util.ServerUtil;
import com.example.footprnt.Map.Util.UiUtil;
import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.AppUtil;
import com.example.footprnt.ViewPagerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Handles all map activities
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class MapFragment extends Fragment implements GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    // Map variables
    private GoogleMap mMap;
    private SupportMapFragment mMapFrag;
    private AppUtil mHelper;
    private boolean mJumpToCurrentLocation = false;
    private Location mLocation;
    private LatLng mTappedLocation;
    private int mMapStyle;

    // Search variables
    private EditText mSearchText;
    private ImageView mSearch;
    private FragmentActivity myContext;

    // Marker variables
    private CustomInfoWindowAdapter mInfoAdapter;
    ArrayList<Marker> markers;
    private ArrayList<MarkerDetails> mMarkerDetails;
    private Marker mTempMarker;

    // Post variables
    private ParseUser mUser;
    private ImageView mImage;
    private File mPhotoFile;
    private AlertDialog mAlertDialog = null;
    private ParseFile mParseFile;

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
    private FilterMenuLayout mFilterMenuLayout;
    private Switch mSwitch;
    private boolean mMenuItemsAdded;

    // Sound variables
    private MediaPlayer mSwipe;
    private MediaPlayer mBubble;
    private MediaPlayer mBubbleClose;

    // Progress bar variables
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mProgressBar = v.findViewById(R.id.pbLoading);
        initialization();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchText = view.findViewById(R.id.searchText);
        mFilterMenuLayout = view.findViewById(R.id.filter_menu4);
        mFilterMenuLayout.setVisibility(View.GONE);
        mSettings = view.findViewById(R.id.ivSettings);
        mSearch = getActivity().findViewById(R.id.search);
        mPopup = new PopupMenu(getActivity(), mSettings);
        mPopup.getMenuInflater().inflate(R.menu.popup_menu_map, mPopup.getMenu());
        mSwipe = MediaPlayer.create(getContext(), R.raw.swipe_two);
        mBubble = MediaPlayer.create(getContext(), R.raw.bubble);
        mBubbleClose = MediaPlayer.create(getContext(), R.raw.bubble_close);
        configureMapStyleMenu();
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
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
                setupMapUserLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(mInfoAdapter);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        mMap.setOnMapLongClickListener(this);
        mJumpToCurrentLocation = true;
        setupMapStyles();
        setupMapUserLocation();
        loadMarkers();
        handleSwitch();
        handleSearch();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mTappedLocation = latLng;
        ((HomeActivity) getActivity()).hideBottomNav();
        UiUtil.hideToolBar(getActivity());
        configureFilterMenu(latLng);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            if (requestCode == AppConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == getActivity().RESULT_OK) {
                    Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                    mImage.setVisibility(View.VISIBLE);
                    mImage.setImageBitmap(takenImage);
                    File photoFile = mHelper.getPhotoFileUri(getContext(), AppConstants.photoFileName);
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
                    bitmap.compress(Bitmap.CompressFormat.JPEG, AppConstants.captureImageQuality, stream);
                    byte[] image = stream.toByteArray();
                    mParseFile = new ParseFile(AppConstants.imagePath, image);
                    final Bitmap finalBitmap = bitmap;
                    mImage.setVisibility(View.VISIBLE);
                    Glide.with(this).load(finalBitmap).into(mImage);
                } else {
                    mParseFile = null;
                }
            }
        } catch (Exception e){
            Toast.makeText(getContext(), R.string.photo_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initializes map and sets user permissions
     */
    private void initialization() {
        mMapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        MapsInitializer.initialize(this.getActivity());
        mMapFrag.getMapAsync(this);
        mHelper = new AppUtil();
        mUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL(); // set permissions
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        mUser.setACL(acl);
        mUser.setACL(acl);
        mMarkerDetails = new ArrayList<>();
        markers = new ArrayList<>();
        mInfoAdapter = new CustomInfoWindowAdapter(getContext());
        if(mUser.getInt(MapConstants.MAP_STYLE)!=0) {
            mMapStyle = mUser.getInt(MapConstants.MAP_STYLE);
        } else {
            // Default to the Aubergine style
            mMapStyle = MapConstants.STYLE_AUBERGINE;
        }
        mMenuItemsAdded = false;
    }

    /**
     * Sets up map styles
     */
    private void setupMapStyles() {
        View toolbar = ((View) mMapFrag.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("4"));
        RelativeLayout.LayoutParams rlpToolbar = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        rlpToolbar.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlpToolbar.setMargins(100, 0, 0, 250);
        View locationButton = ((View) getActivity().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlpMyLocation = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlpMyLocation.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlpMyLocation.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlpMyLocation.setMargins(0, 200, 180, 0);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), mMapStyle));
    }

    /**
     * Requests permission for location and handles finding user location
     */
    private void setupMapUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (mJumpToCurrentLocation && mLocation != null) {
            mJumpToCurrentLocation = false;
            MapUtil.centreMapOnLocation(mMap, mLocation);
        }

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(arg0.getLatitude());
                temp.setLongitude(arg0.getLongitude());
                if (mJumpToCurrentLocation) {
                    mJumpToCurrentLocation = false;
                    MapUtil.centreMapOnLocation(mMap, temp);
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
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        postQuery.withUser().whereEqualTo(AppConstants.user, mUser);
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        MarkerDetails md = objects.get(i);
                        mMarkerDetails.add(md);
                    }
                    for (MarkerDetails markerDetails : mMarkerDetails) {
                        try {
                            Marker m = ServerUtil.createMarker(mMap, markerDetails);
                            markers.add(m);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    /**
     * Loads map markers for all user's posts
     */
    public void loadAllMarkers() {
        mMarkerDetails = new ArrayList<>();
        markers = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        postQuery.withUser();
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        MarkerDetails md = objects.get(i);
                        try {
                            Marker m = ServerUtil.createMarker(mMap, md);
                            markers.add(m);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Popups feed at tapped location
     */
    private void createFeed() {
        MapRipple mMapRipple = new MapRipple(mMap, mTappedLocation, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.CYAN)
                .withStrokeColor(Color.BLACK)
                .withDistance(2000)      // 8046.72 for 5 miles
                .withRippleDuration(12000)    //12000ms
                .withTransparency(0.6f);
        mMapRipple.startRippleMapAnimation();      //in onMapReadyCallBack
        Intent i = new Intent(getActivity(), FeedActivity.class);
        i.putExtra(MapConstants.LATITUDE, mTappedLocation.latitude);
        i.putExtra(MapConstants.LONGITUDE, mTappedLocation.longitude);
        startActivity(i);
    }

    /**
     * Shows create post dialog box at the point selected
     *
     * @param point point where post is being created
     */
    private void createPostDialog(LatLng point) {
        View messageView = LayoutInflater.from(getActivity()).inflate(R.layout.create_post, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(messageView);
        mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
        mTags = new ArrayList<>();
        handleTags();
        EditText etDescription = mAlertDialog.findViewById(R.id.etSnippet);
        etDescription.setScroller(new Scroller(getContext()));
        etDescription.setMaxLines(3);
        etDescription.setVerticalScrollBarEnabled(true);
        etDescription.setMovementMethod(new ScrollingMovementMethod());
        mImage = mAlertDialog.findViewById(R.id.image);
        mImage.setVisibility(View.GONE);
        TextView location = mAlertDialog.findViewById(R.id.location);
        location.setText(mHelper.getAddress(getContext(), point));
        final Marker temp = mMap.addMarker(new MarkerOptions().position(point).icon(MapConstants.DEFAULT_MARKER));
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                temp.remove();
            }
        });
        handlePhotoButtons();
        handlePostButtons(point, temp);
    }

    /**
     * Handles upload image and take image buttons in alert dialog
     */
    private void handlePhotoButtons() {
        ImageView ivUpload = mAlertDialog.findViewById(R.id.ivUpload);
        ImageView ivCamera = mAlertDialog.findViewById(R.id.ivCamera);
        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), AppConstants.GET_FROM_GALLERY);
            }
        });
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoFile = mHelper.getPhotoFileUri(getActivity(), AppConstants.photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(getActivity(), AppConstants.fileProvider, mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, AppConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
    }

    /**
     * Handles send post and cancel post buttons in alert dialog
     * @param point point where post is being made
     * @param temp temporary marker for UI purposes
     */
    private void handlePostButtons(final LatLng point, final Marker temp) {
        ImageView sendPost = mAlertDialog.findViewById(R.id.cancel);
        ImageView cancelPost = mAlertDialog.findViewById(R.id.cancelPost);
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipe.start();                 // Add sound when user sends post
                mProgressBar.setVisibility(View.VISIBLE);
                final String title = ((EditText) mAlertDialog.findViewById(R.id.etTitle)).getText().toString();
                final String snippet = ((EditText) mAlertDialog.findViewById(R.id.etSnippet)).getText().toString();
                if( TextUtils.isEmpty(title) || TextUtils.isEmpty(snippet)) {
                    Toast.makeText(getContext(), R.string.post_incomplete, Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    final MarkerDetails mOptions = new MarkerDetails();
                    mOptions.setUser(mUser);
                    if (mParseFile != null) {
                        mParseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Post p = ServerUtil.createPost(getActivity(), getContext(), snippet, title, mParseFile, mUser, point, mTags);
                                mOptions.setPost(p);
                                try {
                                    ServerUtil.createMarker(mMap, mOptions);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                mOptions.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        loadMarkers();
                                    }
                                });
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Post p = ServerUtil.createPost(getActivity(), getContext(), snippet, title, mParseFile, mUser, point, mTags);
                        mOptions.setPost(p);
                        try {
                            ServerUtil.createMarker(mMap, mOptions);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
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
     * Handles searching for location
     */
    private void handleSearch() {
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtil.geoLocate(mSearchText, mMap, getContext());
                InputMethodManager inputManager = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(myContext.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MapUtil.geoLocate(mSearchText, mMap, getContext());
                }
                InputMethodManager inputManager = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(myContext.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
    }

    /**
     * Handles toggling of tags when in create view dialog
     */
    public void handleTags() {
        CULTURE = false;
        FASHION = false;
        TRAVEL = false;
        FOOD = false;
        NATURE = false;
        mParseFile = null;
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
                    mTags.add(MapConstants.CULTURE);
                    CULTURE = true;
                } else {
                    culture.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.CULTURE);
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
                    mTags.add(MapConstants.FOOD);
                    FOOD = true;
                } else {
                    food.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.FOOD);
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
                    mTags.add(MapConstants.FASHION);
                    FASHION = true;
                } else {
                    fashion.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.FASHION);
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
                    mTags.add(MapConstants.TRAVEL);
                    TRAVEL = true;
                } else {
                    travel.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.TRAVEL);
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
                    mTags.add(MapConstants.NATURE);
                    NATURE = true;
                } else {
                    nature.setTypeface(null, Typeface.NORMAL);
                    mTags.remove(MapConstants.NATURE);
                    NATURE = false;
                }
            }
        });
    }

    /**
     * Handles toggling of user posts vs all posts
     */
    public void handleSwitch() {
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
     * Configures on map long press filter menu animation and sound
     * @param latLng location to launch create post or view feed
     */
    private void configureFilterMenu(LatLng latLng) {
        final Marker m = mMap.addMarker(new MarkerOptions().position(latLng).icon(MapConstants.MARKER_AZURE));
        mBubble.start();
        mFilterMenuLayout.setVisibility(View.VISIBLE);
        FilterMenu.OnMenuChangeListener menuChangeListener = new FilterMenu.OnMenuChangeListener() {
            @Override
            public void onMenuItemClick(View view, int position) {
                if (MapConstants.MENU_ITEMS[position] == MapConstants.CREATE) {
                    createPostDialog(mTappedLocation);
                }
                else if (MapConstants.MENU_ITEMS[position] == MapConstants.VIEW) {
                    createFeed();
                }
                else if (MapConstants.MENU_ITEMS[position] == MapConstants.DISCOVER) {
                    ViewPager viewPager = getActivity().findViewById(R.id.viewpager);
                    viewPager.setCurrentItem(1);
                    Fragment viewPagerAdapter = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(1);
                    ((DiscoverFragment) viewPagerAdapter).setDataFromMapFragment(mTappedLocation);
                }
                else if (MapConstants.MENU_ITEMS[position] == MapConstants.CURRENT) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Location location = mMap.getMyLocation();
                        LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        createPostDialog(currLocation);
                    }
                }
                else if (MapConstants.MENU_ITEMS[position] == MapConstants.STREET){
                    Intent i = new Intent(getActivity(), StreetViewActivity.class);
                    i.putExtra(MapConstants.LATITUDE, mTappedLocation.latitude);
                    i.putExtra(MapConstants.LONGITUDE, mTappedLocation.longitude);
                    startActivityForResult(i, 20);
                }
            }
            @Override
            public void onMenuCollapse() {
                mFilterMenuLayout.setVisibility(View.INVISIBLE);
                ((HomeActivity)getActivity()).showBottomNav();
                UiUtil.showToolbar(getActivity());
                mBubbleClose.start();
                m.remove();
            }
            @Override
            public void onMenuExpand() {
            }
        };

        if (!mMenuItemsAdded) {
            mMenuItemsAdded = true;
            FilterMenu menu = new FilterMenu.Builder(getContext())
                    .addItem(R.drawable.ic_pencil_white)
                    .addItem(R.drawable.ic_post_current_location)
                    .addItem(R.drawable.ic_rocket_white)
                    .addItem(R.drawable.ic_feed)
                    .addItem(R.drawable.ic_street)
                    .attach(mFilterMenuLayout)
                    .withListener(menuChangeListener)
                    .build();
            menu.toggle(true);
        } else {
            FilterMenu menu = new FilterMenu.Builder(getContext())
                    .attach(mFilterMenuLayout)
                    .withListener(menuChangeListener)
                    .build();
            menu.toggle(true);
        }
    }

    /**
     * Helper function to set up the pop up menu which configures the style for map
     */
    private void configureMapStyleMenu() {
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
                                toggleMenuItem(item, MapConstants.STYLE_DARKMODE);
                                return true;
                            case R.id.edit_style_silver:
                                toggleMenuItem(item, MapConstants.STYLE_SILVER);
                                return true;
                            case R.id.edit_style_aubergine:
                                toggleMenuItem(item, MapConstants.STYLE_AUBERGINE);
                                return true;
                            case R.id.edit_style_retro:
                                toggleMenuItem(item, MapConstants.STYLE_RETRO);
                                return true;
                            case R.id.edit_style_basic:
                                toggleMenuItem(item, MapConstants.STYLE_BASIC);
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
        mUser.put(MapConstants.MAP_STYLE, id);
        mUser.saveInBackground();
        for (int i = 0; i < mPopup.getMenu().size(); i++) {
            if (mPopup.getMenu().getItem(i).getItemId() != menuItem.getItemId()) {
                mPopup.getMenu().getItem(i).setChecked(false);
            }
        }
    }

    /**
     * Zooms in on business location and creates marker at the business location
     * @param address address of business
     * @param businessName name of business
     * @param imageUrl url of business image
     */
    public void handleDiscoverInteraction(String address, String businessName, String imageUrl){
        EditText et = new EditText(getContext());
        et.setText(address);
        Location l = MapUtil.geoLocate(et, mMap, getContext());
        if (imageUrl == null){
            imageUrl = "";
        }
        mTempMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(l.getLatitude(), l.getLongitude()))
                .title(businessName)
                .snippet(imageUrl)
                .icon(MapConstants.MARKER_CYAN));
    }
}