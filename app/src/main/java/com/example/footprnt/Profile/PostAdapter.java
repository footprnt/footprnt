package com.example.footprnt.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;

import java.util.ArrayList;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    // Instance fields:
    ArrayList<Post> posts;    // list of posts
    Context context;          // context for rendering

    public PostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post_grid, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        // TODO: Set views here
        if(post.getImage()!=null) {
            Glide.with(context).load(post.getImage().getUrl()).centerCrop().placeholder(R.drawable.ic_add_photo).error(R.drawable.ic_add_photo).into(holder.ivPicture);
        } else {
            Glide.with(context).load(R.drawable.ic_add_photo).placeholder(R.drawable.ic_add_photo).error(R.drawable.ic_add_photo).into(holder.ivPicture);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
        }
    }
}