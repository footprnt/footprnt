package com.example.footprnt.Discover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.parse.ParseImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YelpService extends AppCompatActivity {

    Post post;

    @BindView(R.id.ivProfile)
    ParseImageView ivProfile;
    @BindView(R.id.cbRestaurant)
    CheckBox cbRestaurant;
    @BindView(R.id.cbLegal)
    CheckBox cbLegal;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvPlace)
    TextView tvPlace;
    @BindView(R.id.tvPerson)
    TextView tvPerson;
    @BindView(R.id.tvFood)
    TextView tvFood;
    @BindView(R.id.tvYelp)
    TextView tvYelp;
    @BindView(R.id.btnLink)
    Button btnLink;
    @BindView(R.id.ivImage)
    ImageView ivImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_service);

        String secretKey = getString(R.string.yelp_api_key);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://api.yelp.com/v3/businesses/search?latitude=" + post.getLocation().getLatitude() + "&longitude=" + post.getLocation().getLongitude() + "")
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

                    //JSONArray myResponse = (JSONArray)jsonObject.get("id");
                    final String imageURL = jsonObject.getString("image_url");
                    final String url = jsonObject.getString("url");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tvYelp.setText(jsonObject.getString("name"));
                                Glide.with(YelpService.this)
                                        .load(imageURL)
                                        .override(100, 100)
                                        .into(ivImage);
                                btnLink.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                        finish();
                                    }
                                });
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
        if (post.getTitle() != null) {
            tvTitle.setText(post.getTitle());
        }
        if (post.getImage() != null) {
            ivProfile.setParseFile(post.getImage());
            ivProfile.loadInBackground();
        }
    }
}
