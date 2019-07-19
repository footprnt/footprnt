package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprnt.Models.Post;
import com.example.footprnt.Profile.EditPost;
import com.example.footprnt.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    View rootView;
    ImageView ivProfile;
    TextView tvName;
    View vPalette;

    public View getRootView() {
        return rootView;
    }

    public ImageView getIvProfile() {
        return ivProfile;
    }

    public TextView getTvName() {
        return tvName;
    }

    public View getvPalette() {
        return vPalette;
    }

    public PostViewHolder(View v) {
        super(v);
        rootView = itemView;
        ivProfile = itemView.findViewById(R.id.ivProfile);
        tvName = itemView.findViewById(R.id.tvName);
        vPalette = itemView.findViewById(R.id.vPalette);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Post contact = (Post) v.getTag();
                if (contact != null) {
                    Intent i = new Intent(v.getContext(), EditPost.class);
                }
            }
        });
    }

}
