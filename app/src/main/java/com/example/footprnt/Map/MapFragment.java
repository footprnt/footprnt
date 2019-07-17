package com.example.footprnt.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.example.footprnt.Manifest;
import com.example.footprnt.Models.MarkerDetails;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.LocationHelper;
import com.example.footprnt.Util.PhotoHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private GoogleMap map;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng lastPoint;
    LocationHelper locationHelper;
    boolean mJumpToCurrentLocation = false;
    MapRipple mapRipple;
    ArrayList<MarkerDetails> markers;
    public static final int GET_FROM_GALLERY = 3;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    ImageView imageView;
    File photoFile;
    ImageView sendPost;
    ImageView cancelPost;
    AlertDialog alertDialog=null;
    ParseFile parseFile;
    private ParseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        locationHelper = new LocationHelper();
        user = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL();
        acl.setReadAccess(user,true);
        acl.setWriteAccess(user,true);
        user.setACL(acl);
        markers = new ArrayList<>();
        return v;
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    locationHelper.centreMapOnLocation(map, lastKnownLocation, "Your location");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);
        Intent intent = getActivity().getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){
            // Zoom into users location
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (mJumpToCurrentLocation) {
                        mJumpToCurrentLocation = false;
                        locationHelper.centreMapOnLocation(map, location, "Your Location");
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

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null ) {
                    locationHelper.centreMapOnLocation(map, lastKnownLocation, "Your Location");
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            loadMarkers();
        }
    }

    public void loadMarkers(){
        final MarkerDetails.Query postQuery = new MarkerDetails.Query();
        postQuery.withUser().whereEqualTo("user", user);
        postQuery.findInBackground(new FindCallback<MarkerDetails>() {
            @Override
            public void done(List<MarkerDetails> objects, ParseException e) {
                if (e == null){
                    for (int i = 0; i < objects.size(); i++){
                        System.out.println(objects.get(i).getLocation());
                        System.out.println(objects.get(i).getTitle());
//                        createMarker(objects.get(i).getLocation().getLatitude(), objects.get(i).getLocation().getLatitude(), objects.get(i).getTitle(), objects.get(i).getDescription());
                    }
                } else {
                    markers = new ArrayList<>();
                    user.put("markers", new ArrayList<Marker>());
                    user.saveInBackground();
                }
            }
        });
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        return map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(defaultMarker));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mJumpToCurrentLocation = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mapRipple = new MapRipple(map, latLng, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.BLUE)
                .withStrokeColor(Color.BLACK)
                .withDistance(2000)      // 2000 metres radius
                .withRippleDuration(6000)    //12000ms
                .withTransparency(0.8f);
        mapRipple.startRippleMapAnimation();      //in onMapReadyCallBack
        Toast.makeText(getActivity(), locationHelper.getAddress(getContext(), latLng), Toast.LENGTH_LONG).show();
        showAlertDialogForPoint(latLng);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapRipple = new MapRipple(map, latLng, getContext())
                .withNumberOfRipples(3)
                .withFillColor(Color.BLUE)
                .withStrokeColor(Color.BLACK)
                .withDistance(2000)      // 2000 metres radius
                .withRippleDuration(6000)    //12000ms
                .withTransparency(0.9f);
        mapRipple.startRippleMapAnimation();      //in onMapReadyCallBack
        Intent i = new Intent(getActivity(), FeedActivity.class);
        startActivity(i);
    }

    private void showAlertDialogForPoint(final LatLng point) {
        View messageView = LayoutInflater.from(getActivity()).
                inflate(R.layout.message_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(messageView);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        sendPost = alertDialog.findViewById(R.id.sendPost);
        cancelPost = alertDialog.findViewById(R.id.cancelPost);
        ImageView ivUpload = alertDialog.findViewById(R.id.ivUpload);
        ImageView ivCamera = alertDialog.findViewById(R.id.ivCamera);
        imageView = alertDialog.findViewById(R.id.image);
        imageView.setVisibility(View.GONE);
        TextView location = alertDialog.findViewById(R.id.location);
        location.setText(locationHelper.getAddress(getContext(),point));
        lastPoint = point;

        ivUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                PhotoHelper photoHelper = new PhotoHelper();
                photoFile = photoHelper.getPhotoFileUri(getActivity(), photoFileName);

                Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.example.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
                        getText().toString();
                final String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
                        getText().toString();
                createMarker(lastPoint.latitude, lastPoint.longitude, title, snippet);
                MarkerDetails mOptions = new MarkerDetails();
                mOptions.setLocation(new ParseGeoPoint(lastPoint.latitude, lastPoint.longitude));
                mOptions.setDescription(snippet);
                mOptions.setTitle(title);
                mOptions.setUser(user);
                markers.add(mOptions);
                user.put("markers", markers);
                user.saveInBackground();
                if (parseFile != null){
                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            createPost(snippet, title, parseFile, user, lastPoint);
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    createPost(snippet, title, parseFile , user, lastPoint);
                    alertDialog.dismiss();
                }
            }
        });
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    public void createPostCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng currLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            showAlertDialogForPoint(currLocation);
        }
    }

    private void createPost(String description, String title, ParseFile imageFile, ParseUser user, LatLng point){
        final Post newPost = new Post();
        newPost.setDescription(description);
        if (imageFile == null){
            newPost.remove("image");
        } else {
            newPost.setImage(imageFile);
        }
        newPost.setUser(user);
        newPost.setTitle(title);
        newPost.setLocation(new ParseGeoPoint(point.latitude, point.longitude));
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("MapFragment", "Create post success");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(takenImage);
                PhotoHelper photoHelper = new PhotoHelper();
                File photoFile = photoHelper.getPhotoFileUri(getContext(), photoFileName);
                parseFile = new ParseFile(photoFile);
            } else {
                parseFile = null;
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap bitmap = null;
                Uri selectedImage = data.getData();
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                } catch (Exception e) {
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                parseFile = new ParseFile("profpic.jpg", image);
                final Bitmap finalBitmap = bitmap;
                imageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(finalBitmap).into(imageView);
            } else {
                parseFile = null;
            }
        }

    }

}
