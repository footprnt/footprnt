package com.example.footprnt.Map;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    // Instance fields:
    ArrayList<Post> posts;    // list of posts
    Context context;          // context for rendering
    LocationHelper helper;

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
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    // Binds an inflated view to a new view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("tag", "onBindViewlHolder");
        Post post = posts.get(position);

        String description = post.getDescription();
        String title = post.getTitle();

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivUserPicture = itemView.findViewById(R.id.ivUserPicture);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTimePosted = itemView.findViewById(R.id.timePosted);
            tvTitle = itemView.findViewById(R.id.tvTitle);

            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            // gets item position
//            int position = getAdapterPosition();
//            // make sure the position is valid, i.e. actually exists in the view
//            if (position != RecyclerView.NO_POSITION) {
//                // get the movie at the position, this won't work if the class is static
//                Movie movie = movies.get(position);
//                // create intent for the new activity
//                Intent intent = new Intent(context, MovieDetailsActivity.class);
//                // serialize the movie using parceler, use its short name as a key
//                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
//
//                // Pass backdrop image
//                String imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
//                intent.putExtra("video_id", imageUrl);
//                intent.putExtra("name_movie", movie.getTitle());
//
//                // show the activity
//                context.startActivity(intent);
//            }
        }


    }


}