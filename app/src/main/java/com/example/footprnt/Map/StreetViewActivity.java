package com.example.footprnt.Map;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.footprnt.Map.Util.MapConstants;
import com.example.footprnt.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Street view activity
 *
 * @author Jocelyn Shen
 * @version 1.0
 * @since 2019-07-22
 */
public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Double lat = getIntent().getExtras().getDouble(MapConstants.LATITUDE);
        Double lon = getIntent().getExtras().getDouble(MapConstants.LONGITUDE);
        System.out.println(lat);
        mLatLng = new LatLng(lat, lon);
        setContentView(R.layout.activity_street_view);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(mLatLng);
    }
}
