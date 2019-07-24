package com.example.footprnt.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.footprnt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.image_marker, null);
    }

    private void renderWindow(Marker marker, View view){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        if (title != null){
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
//        ImageView image = view.findViewById(R.id.imageMarker);
//        if (imageUrl != null && imageUrl.length() >0) {
//                Glide.with(view).load(imageUrl).into(image);
//        } else {
//            image.setVisibility(View.GONE);
//        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker, mWindow);
        return mWindow;
    }
}
