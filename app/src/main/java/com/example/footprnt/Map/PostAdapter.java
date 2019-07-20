package com.example.footprnt.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapterOld";
    // Instance fields:
    ArrayList<Post> posts;    // list of posts
    Context context;          // context for rendering
    LocationHelper helper;
    Typeface montserrat;

    public PostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("tag", "onCreateViewHolder");
        context = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        montserrat = ResourcesCompat.getFont(context, R.font.montserrat_bold);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    // Binds an inflated view to a new view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("tag", "onBindViewlHolder");
        final Post post = posts.get(position);
        String description = post.getDescription();
        String title = post.getTitle();

        holder.iv5.setImageResource(R.drawable.ic_map);

        holder.tvTitle.setTypeface(montserrat);
        holder.tvUser.setText(post.getUser().getUsername());
        if (description.length() > 0) {
            holder.tvDescription.setText(description);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        if (title != null && title.length() > 0) {
            holder.tvTitle.setText(title);
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        Date d = post.getCreatedAt();
        String dateText;
        if (d==null){
            dateText = "0s";
        } else {
            dateText = getRelativeTimeAgo(d.toString());
        }
        holder.tvTimePosted.setText(dateText);

        if(post.getImage()!=null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(context).load(imgUrl).into(holder.ivPicture);
        } else {
            holder.ivPicture.setVisibility(View.GONE);
        }
        if(post.getUser().getParseFile("profileImg")!=null) {
            String userImgUrl = post.getUser().getParseFile("profileImg").getUrl();
            Glide.with(context).load(userImgUrl).apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        } else {
            Glide.with(context).load("http://via.placeholder.com/300.png").apply(RequestOptions.circleCropTransform()).into(holder.ivUserPicture);
        }

        helper = new LocationHelper();
        LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        String tvCityState = helper.getAddress(context, point);
        holder.tvLocation.setText(tvCityState);

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
            holder.tvTags.setText(tagname);
        } else {
            holder.tvTags.setVisibility(View.GONE);
        }

    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        /*
        Calculates relative time
         */
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPicture;
        TextView tvUser;           // Username
        TextView tvTitle;
        TextView tvDescription;    // Post Description
        ImageView ivUserPicture;   // Users Picture
        TextView tvLocation;      // Post Location
        TextView tvTimePosted;
        TextView tvTags;
        ImageView iv5;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivUserPicture = itemView.findViewById(R.id.ivUserPicture);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTimePosted = itemView.findViewById(R.id.timePosted);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTags = itemView.findViewById(R.id.tvTags);
            iv5 = itemView.findViewById(R.id.imageView5);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            System.out.println(position);
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Post.class.getSimpleName(), post);
                intent.putExtras(bundle);
                Date d = post.getCreatedAt();
                String dateText;
                if (d==null){
                    dateText = "0s";
                } else {
                    dateText = getRelativeTimeAgo(d.toString());
                }
                intent.putExtra("time", dateText);
                ((Activity) context).startActivityForResult(intent, 20);
            }
        }


    }


}