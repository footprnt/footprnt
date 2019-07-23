/*
 * PostAdapter.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.footprnt.R;

import java.util.ArrayList;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    ArrayList<String> posts;
    Context context;

    public PostAdapter(ArrayList<String> posts) {
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
        View postView = inflater.inflate(R.layout.item_discover, parent, false);
        return new ViewHolder(postView);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cur = posts.get(position);
        holder.tvItem.setText(cur);
    }


    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);

        }

        @Override
        public void onClick(View v) {

        }
    }
}