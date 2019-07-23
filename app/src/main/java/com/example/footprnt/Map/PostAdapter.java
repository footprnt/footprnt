package com.example.footprnt.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprnt.Map.Util.Util;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Adapts posts to feed recycler view
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    static ArrayList<Post> mPosts;    // list of posts
    static Context mContext;          // context for rendering
    Typeface montserrat;

    public PostAdapter(ArrayList<Post> posts) {
        this.mPosts = posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(mContext);
        montserrat = ResourcesCompat.getFont(mContext, R.font.montserrat_bold);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        Util.setPostText(post, holder, mContext);
        Util.setPostImages(post, holder, mContext);
        holder.tvTitle.setTypeface(montserrat); // type specific to feed
        holder.iv5.setImageResource(R.drawable.ic_map); // image specific to feed
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivPicture;
        public TextView tvUser;           // Username
        public TextView tvTitle;          // Title of post
        public TextView tvDescription;    // Post Description
        public ImageView ivUserPicture;   // Users Picture
        public TextView tvLocation;      // Post Location
        public TextView tvTimePosted; // Time post created
        public TextView tvTags; // Tags
        public ImageView iv5; // icon

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
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Post.class.getSimpleName(), post);
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, 20);
            }
        }
    }
}