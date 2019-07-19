package com.example.footprnt.Profile.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.Util.Util;
import com.example.footprnt.R;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Complex adapter for the multiple views within the user profile page
 * Created By: Clarisa Leu-Rodriguez
 */
public class MultiViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private ArrayList<Object> items;
    Util util = new Util();

    // identifier for items
    private final int USER_INFO = 0, POST = 1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MultiViewAdapter(Context context, ArrayList<Object> items) {
        this.items = items;
        this.mContext = context;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Post) {
            return POST;
        } else if (items.get(position) instanceof ParseUser) {
            return USER_INFO;
        }
        return -1;
    }


    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case POST:
                View v1 = inflater.inflate(R.layout.item_post_card, viewGroup, false);
                viewHolder = new PostViewHolder(v1);
                break;
            case USER_INFO:
                View v2 = inflater.inflate(R.layout.item_user_information, viewGroup, false);
                viewHolder = new UserInfoViewHolder(v2);
                break;
            default:
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case POST:
                PostViewHolder vh1 = (PostViewHolder) viewHolder;
                configurePostViewHolder(vh1, position);
                break;
            case USER_INFO:
                UserInfoViewHolder vh2 = (UserInfoViewHolder) viewHolder;
                configureUserInfoViewHolder(vh2, position);
                break;
        }
    }

    private void configurePostViewHolder(final PostViewHolder vh1, int position) {
        final Post post = (Post) items.get(position);
        if (post != null) {
            vh1.getRootView().setTag(post);
            ArrayList<String> location = util.getAddress(mContext, post.getLocation());
            // Set location
            vh1.getTvName().setText(location.get(0)+", "+location.get(1));
            vh1.getvPalette().setBackgroundColor(Color.WHITE);

            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    vh1.getIvProfile().setImageBitmap(resource);
                    Palette p = Palette.from(resource).generate();
                    vh1.getvPalette().setBackgroundColor(p.getVibrantColor(Color.WHITE));
                }
            };
            vh1.getIvProfile().setTag(target);
            Glide.with(mContext).asBitmap().load(post.getImage().getUrl()).centerCrop().into(target);
        }
    }


    private void configureUserInfoViewHolder(UserInfoViewHolder vh2, int position) {
        ParseUser user = (ParseUser) items.get(position);
        if (user != null) {
            // TODO: fix to be real prof image an
            vh2.setmIvProfileImage(user.getParseFile("profileImg").getUrl(), mContext);
        }
    }
}
