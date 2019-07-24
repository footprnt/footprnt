/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.footprnt.Discover.Activities.RestaurantsActivity;
import com.example.footprnt.Discover.Models.Restaurant;
import com.example.footprnt.R;

import java.util.ArrayList;


public class DiscoverFragment extends Fragment {
    public static final String TAG = DiscoverFragment.class.getSimpleName();

    Button btnFindRestaurants;
    EditText etZip;

    public ArrayList<Restaurant> restaurants = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        String location = intent.getStringExtra("location");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        // Bind views
        View view = inflater.inflate(R.layout.fragment_discover, parent, false);


        etZip = view.findViewById(R.id.etZip);

        ArrayList<Restaurant> restaurants = new ArrayList<>();



        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnFindRestaurants = view.findViewById(R.id.btnFindRestaurants);
        btnFindRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), RestaurantsActivity.class);
                i.putExtra("zipcode", etZip.getText().toString());
                startActivity(i);

            }
        });

    }


}