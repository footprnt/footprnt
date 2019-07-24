package com.example.footprnt.Map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;
    private ConstraintLayout mConstraintLayout;

    public CustomInfoWindowAdapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.image_marker, null);
        mConstraintLayout = mWindow.findViewById(R.id.constraintLayout);
    }

    public void getPostObject(final Marker marker, final View v){
        final Post.Query postQuery = new Post.Query();
        postQuery.withUser().whereEqualTo("objectId", marker.getSnippet());
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                Post post = objects.get(0);
                System.out.println(post.getObjectId());
                if (marker.getSnippet() != null){
                    if (post != null){
                        String title = marker.getTitle();
                        TextView tvTitle = v.findViewById(R.id.title);
                        if (title != null){
                            tvTitle.setText(title);
                        } else {
                            tvTitle.setVisibility(View.INVISIBLE);
                        }
                        System.out.println(title);
                        ImageView image = v.findViewById(R.id.imageMarker);
                        if(post.getImage()!=null) {
                            String imgUrl = post.getImage().getUrl();
                            Glide.with(mContext).load(imgUrl).into(image);
                        } else {
                            image.setVisibility(View.GONE);
                        }
                        mConstraintLayout.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        mConstraintLayout.setVisibility(View.INVISIBLE);
        getPostObject(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        mConstraintLayout.setVisibility(View.INVISIBLE);
        getPostObject(marker, mWindow);
        return mWindow;
    }
}
