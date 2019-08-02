package com.example.footprnt.Map.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.footprnt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Adapts Google Maps marker to custom info window
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;
    private Handler mHandler;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.image_marker, null);
    }

    /**
     * Grabs the post associated with marker
     *
     * @param marker marker clicked on by user
     * @param v view
     */
    public void getPostObject(final Marker marker, final View v) {
        final ImageView image = v.findViewById(R.id.imageMarker);
        if (marker.getSnippet().length() > 0) {
            image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(marker.getSnippet()).placeholder(R.drawable.ic_add_photo).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if(marker.getSnippet().equals(v.getTag())) {
                        return false;
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            v.setTag(marker.getSnippet());
                            marker.showInfoWindow();
                        }
                    });

                    return false;
                }
            }).into(image);
        } else {
            image.setVisibility(View.INVISIBLE);
        }
        String title = marker.getTitle();
        TextView tvTitle = v.findViewById(R.id.eventTitle);
        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        try {
            mHandler = new Handler();
            getPostObject(marker, mWindow);
        } catch (Exception e) {}
        return mWindow;
    }
}
