package com.example.footprnt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footprnt.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements
        GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    LocationManager locationManager;
    LocationListener locationListener;
    public static final int GET_FROM_GALLERY = 3;
    public final String APP_TAG = "footprnt";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    ImageView image;
    File photoFile;
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView newPost = getView().findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composePost();
            }
        });
    }

    public void composePost() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng currLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            showAlertDialogForPoint(currLocation);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(getActivity(), getAddress(latLng), Toast.LENGTH_LONG).show();
        showAlertDialogForPoint(latLng);
    }

    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final LatLng point) {
        View messageView = LayoutInflater.from(getActivity()).
                inflate(R.layout.message_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(messageView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        ImageView sendPost = alertDialog.findViewById(R.id.sendPost);
        ImageView cancelPost = alertDialog.findViewById(R.id.cancelPost);
        ImageView ivUpload = alertDialog.findViewById(R.id.ivUpload);
        ImageView ivCamera = alertDialog.findViewById(R.id.ivCamera);
        image = alertDialog.findViewById(R.id.image);
        TextView location = alertDialog.findViewById(R.id.location);
        location.setText(getAddress(point));

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDescriptor defaultMarker =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
                        getText().toString();
                String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
                        getText().toString();
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(point)
                        .title(title)
                        .snippet(snippet)
                        .icon(defaultMarker));
                File photoFile = getPhotoFileUri(photoFileName);
                ParseFile parseFile = new ParseFile(photoFile);
                ParseUser user = ParseUser.getCurrentUser();
                createPost(snippet, parseFile, user, point);
                alertDialog.dismiss();
            }
        });

        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

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
                photoFile = getPhotoFileUri(photoFileName);

                Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user, LatLng point){
        //final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        //pb.setVisibility(ProgressBar.VISIBLE);
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);
        newPost.setLocation(new ParseGeoPoint(point.latitude, point.longitude));
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("MapFragment", "Create post success");
                } else {
                    e.printStackTrace();
                }
                //pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
        posts.add(0, newPost);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                image.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
//        if (requestCode == GET_FROM_GALLERY){
//            if (resultCode == RESULT_OK) {
//                final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
//                final View mView = getLayoutInflater().inflate(R.layout.item_compose, null);
//                mBuilder.setView(mView);
//                final android.app.AlertDialog dialog = mBuilder.create();
//                ImageView dismiss = mView.findViewById(R.id.dismiss);
//                ImageView sendPost = mView.findViewById(R.id.sendPost);
//                final EditText etCaption = mView.findViewById(R.id.etCaption);
//                dismiss.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                Bitmap bitmap = null;
//
//                Uri selectedImage = data.getData();
//                try{
//                    bitmap = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(), selectedImage);
//                } catch (Exception e) {
//
//                }
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] image = stream.toByteArray();
//                final ParseFile parseFile = new ParseFile("profpic.jpg", image);
//                final Bitmap finalBitmap = bitmap;
//                ImageView ivPreview = mView.findViewById(R.id.ivPost);
//                Glide.with(HomeActivity.this).load(finalBitmap).into(ivPreview);
//
//                sendPost.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String caption = etCaption.getText().toString();
//                        createPost(caption, parseFile, ParseUser.getCurrentUser());
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
//        }
    }

    public String getAddress(LatLng point){
        try {
            Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.isEmpty()) {
                return "Waiting for location...";
            }
            else {
                if (addresses.size() > 0) {
                     return (addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
            return null;
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(this);
        Intent intent = getActivity().getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){
            // Zoom into users location
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //centreMapOnLocation(location,"Your Location");
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
                centreMapOnLocation(lastKnownLocation,"Your Location");
            } else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    public void centreMapOnLocation(Location location, String title){
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(userLocation).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,"Your Location");
            }
        }
    }

}
