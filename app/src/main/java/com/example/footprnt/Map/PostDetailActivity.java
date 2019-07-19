package com.example.footprnt.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.example.footprnt.Util.LocationHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

public class PostDetailActivity extends AppCompatActivity {
    ImageView mIvBackArrow;
    ImageView ivPicture;
    TextView tvUser;           // Username
    TextView tvTitle;
    TextView tvDescription;    // Post Description
    ImageView ivUserPicture;   // Users Picture
    TextView tvLocation;      // Post Location
    TextView tvTimePosted;
    TextView tvTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        final Post post= (Post)bundle.getSerializable(Post.class.getSimpleName());
        String description = post.getDescription();
        String title = post.getTitle();
        String time = getIntent().getExtras().getString("time");
        mIvBackArrow = findViewById(R.id.ivBack2);
        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivPicture = findViewById(R.id.ivPicture);
        tvUser = findViewById(R.id.tvUser);
        tvDescription = findViewById(R.id.tvDescription);
        ivUserPicture = findViewById(R.id.ivUserPicture);
        tvLocation = findViewById(R.id.tvLocation);
        tvTimePosted = findViewById(R.id.timePosted);
        tvTitle = findViewById(R.id.tvTitle);
        tvTags = findViewById(R.id.tvTags);

        tvUser.setText(post.getUser().getUsername());
        if (description.length() > 0) {
            tvDescription.setText(description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }
        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        tvTimePosted.setText(time);

        if(post.getImage()!=null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(this).load(imgUrl).into(ivPicture);
        } else {
            ivPicture.setVisibility(View.GONE);
        }
        if(post.getUser().getParseFile("profileImg")!=null) {
            String userImgUrl = post.getUser().getParseFile("profileImg").getUrl();
            Glide.with(this).load(userImgUrl).apply(RequestOptions.circleCropTransform()).into(ivUserPicture);
        } else {
            Glide.with(this).load("http://via.placeholder.com/300.png").apply(RequestOptions.circleCropTransform()).into(ivUserPicture);
        }

        LocationHelper helper = new LocationHelper();
        LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        String tvCityState = helper.getAddress(this, point);
        tvLocation.setText(tvCityState);

        String tagname = "";
        JSONArray arr = post.getTags();
        if (arr != null && arr.length() >0 ){
            for (int i = 0; i < arr.length(); i++){
                try {
                    tagname += "#" + arr.getString(i) + " ";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (tagname.length() > 0){
                tvTags.setText(tagname);
            } else {
                tvTags.setVisibility(View.GONE);
            }
        } else {
            tvTags.setVisibility(View.GONE);
        }
    }
}
