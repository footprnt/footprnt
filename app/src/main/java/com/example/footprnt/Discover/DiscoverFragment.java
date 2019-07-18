package com.example.footprnt.Discover;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.footprnt.R;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class DiscoverFragment extends Fragment {
    PostAdapter mPostAdapter;
    RecyclerView mRvPosts;
    YelpFusionApi yelpFusionApi;
    YelpFusionApiFactory yelpFusionApiFactory;
    ArrayList<String> resteraunts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String API_KEY = "cXle3bHvt-l2jAbYGJC0Eii7fCTIqLGVOAIQUhZgjw3c0HKPaW3uEJSkU2pSOX8x3170E1zuJJQn298CKdqlqYFZAiu_eA_qRWGAdhsWbI9bBkxnZwa9pGJ-iAswXXYx";
        String CLIENT_ID = "Ao9IvqqNvqXJSj7j8b9mcg";

        yelpFusionApiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = yelpFusionApiFactory.createAPI(API_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            businessSearchTest();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // For post feed view:
        resteraunts = new ArrayList<>();
        mPostAdapter = new PostAdapter(resteraunts);
        mRvPosts = view.findViewById(R.id.rvFeed);
        mRvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRvPosts.setAdapter(mPostAdapter);


    }


    public void businessSearchTest() throws IOException {
        Map<String, String> parms = new HashMap<>();
        parms.put("term", "indian food");
        parms.put("latitude", "40.581140");
        parms.put("longitude", "-111.914184");
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(parms);
        SearchResponse response = call.execute().body();
        Log.d("TAG", response.toString());
    }

}
