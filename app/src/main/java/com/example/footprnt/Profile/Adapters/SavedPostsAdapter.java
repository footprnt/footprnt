package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprnt.Models.SavedPost;
import com.example.footprnt.R;

import java.util.ArrayList;

/**
 * Adapts saved posts to profile recycler view
 *
 * @author Clarisa Leu
 */
public class SavedPostsAdapter extends RecyclerView.Adapter<SavedPostsAdapter.ViewHolder> {

    static ArrayList<SavedPost> mPosts;    // list of posts
    static Context mContext;          // context for rendering

    public SavedPostsAdapter(ArrayList<SavedPost> posts, Context context) {
        this.mPosts = posts;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View postView = inflater.inflate(R.layout.item_post_card, parent, false);
        return new ViewHolder(postView);  // return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SavedPost post = mPosts.get(position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        ImageView mIvImage;
        TextView mTvTitle;
        View mVPalette;
        TextView mTvText;
        TextView mTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mIvImage = itemView.findViewById(R.id.ivImage);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mVPalette = itemView.findViewById(R.id.vPalette);
            mTitle = itemView.findViewById(R.id.eventTitle);
            mTvText = itemView.findViewById(R.id.tvText);
        }
    }
}