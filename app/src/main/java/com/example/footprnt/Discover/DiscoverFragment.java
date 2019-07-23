/*
 * DiscoverFragment.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.footprnt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DiscoverFragment extends Fragment {
    // TODO: set up model for yelp business or query

//    @BindView(R.id.ivProfile)
//    ImageView ivProfile;
//    @BindView(R.id.cbRestaurant)
//    CheckBox cbRestaurant;
//    @BindView(R.id.cbLegal)
//    CheckBox cbLegal;
//    @BindView(R.id.tvTitle)
//    TextView tvTitle;
//    @BindView(R.id.tvPlace)
//    TextView tvPlace;
//    @BindView(R.id.tvPerson)
//    TextView tvPerson;
//    @BindView(R.id.tvFood)
//    TextView tvFood;
   // @BindView(R.id.tvYelp)
    TextView tvYelp;
//    @BindView(R.id.btnLink)
//    Button btnLink;
//    ImageView ivImage;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_yelp_service, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String secretKey = getString(R.string.yelp_api_key);
        //ButterKnife.bind(getContext(), view);
        tvYelp = view.findViewById(R.id.tvYelp);
        final OkHttpClient client = new OkHttpClient();



        // TODO: replace with users actual current location in query
        double lat = 37.421998333333335;
        double longitude = -122.08400000000002;
        final Request request = new Request.Builder()
                .url("https://api.yelp.com/v3/businesses/search?latitude=" + lat + "&longitude=" + longitude + "")
                //.url("https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco")
                .addHeader("Authorization", "Bearer " + secretKey)
                .build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject data = new JSONObject(response.body().string().trim());
                    JSONArray business = data.getJSONArray("businesses");
                    final JSONObject jsonObject = business.getJSONObject(0);

                    // TODO: pull data you want to display
                    //JSONArray myResponse = (JSONArray)jsonObject.get("id");
                    final String imageURL = jsonObject.getString("image_url");
                    final String url = jsonObject.getString("url");

                    String businessName = jsonObject.getString("name");
                   // tvYelp.setText(jsonObject.getString("name"));

//
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // TODO: set data in views on UI thread
                                tvYelp.setText(jsonObject.getString("name"));

//                                tvYelp.setText(jsonObject.getString("name"));
//                                Glide.with(getContext())
//                                        .load(imageURL)
//                                        .override(100, 100)
//                                        .into(ivImage);
//                                btnLink.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent i = new Intent(Intent.ACTION_VIEW);
//                                        i.setData(Uri.parse(url));
//                                        startActivity(i);
//                                        ((Activity) getContext()).finish();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException | JSONException e) {
                    Log.e("YelpServiceActivity", "Didn't respond");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        // TODO: set image of business and title of business
//        if (post.getTitle() != null) {
//            tvTitle.setText(post.getTitle());
//        }
//        if (post.getImage() != null) {
//            // Load profile image
//            Glide.with(this).load(post.getImage().getUrl()).into(ivProfile);
//        }
    }
}
