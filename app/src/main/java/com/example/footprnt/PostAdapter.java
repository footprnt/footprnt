package com.example.footprnt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;

import java.util.ArrayList;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    // Instance fields:
    ArrayList<Post> posts;    // list of posts
    Context context;          // context for rendering

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
        context = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        // Create view using the item_post_one layout
        View postView = inflater.inflate(R.layout.item_post_grid, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    // Binds an inflated view to a new view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        // TODO: Set views here
        // TODO: Set placeholder image on error
        if(post.getImage()!=null) {
            String imgUrl = post.getImage().getUrl();
            Glide.with(context).load(imgUrl).centerCrop().into(holder.ivPicture);
        }
//        // Load image using Glide
//        Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, radius, margin)).placeholder(placeHolderID).error(placeHolderID).into(imageView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);


            // add this as the itemView's OnClickListener to edit
            //itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show MovieDetailsActivity for the selected movie
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