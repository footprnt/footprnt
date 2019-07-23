package com.example.footprnt.Discover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.footprnt.Models.Restaurant;
import com.example.footprnt.R;

import java.util.ArrayList;


public class YelpAdapter extends RecyclerView.Adapter<YelpAdapter.ViewHolder> {
    ArrayList<Restaurant> posts;
    Context context;

    public YelpAdapter(ArrayList<Restaurant> posts) {
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
        View postView = inflater.inflate(R.layout.activity_yelp_service, parent, false);
        return new ViewHolder(postView);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant cur = posts.get(position);
        holder.tvItem.setText(cur.getText());

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