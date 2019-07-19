package com.example.footprnt.Profile.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.EditPost;
import com.example.footprnt.Profile.Util.Util;
import com.example.footprnt.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {
    private Activity mContext;
    private List<Post> mPosts;
    Util util = new Util();

    public PostAdapter(Activity context, List<Post> posts) {
        mContext = context;
        if (posts == null) {
            throw new IllegalArgumentException("Posts must not be null");
        }
        mPosts = posts;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card, parent, false);
        return new VH(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        final Post post = mPosts.get(position);
        holder.rootView.setTag(post);
        ArrayList<String> location = util.getAddress(mContext, post.getLocation());
        holder.tvName.setText(location.get(0)+", "+location.get(1));
        holder.vPalette.setBackgroundColor(Color.WHITE);
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                holder.ivProfile.setImageBitmap(resource);
                Palette p = Palette.from(resource).generate();
                holder.vPalette.setBackgroundColor(p.getVibrantColor(Color.WHITE));
            }
        };

        holder.ivProfile.setTag(target);
        Glide.with(mContext).asBitmap().load(post.getImage().getUrl()).centerCrop().into(target);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Post contact = (Post) v.getTag();
                    if (contact != null) {
                        // Fire an intent when a post is selected
                        Intent i = new Intent(mContext, EditPost.class);
                    }
                }
            });


        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            removeAt(0);
        }
    }

    public void removeAt(int position) {
        mPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPosts.size());
    }

}