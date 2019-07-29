package com.example.footprnt.Util;

import com.google.android.gms.maps.model.LatLng;

public interface DataInterface {
    public void send(LatLng point);
    public LatLng get();
}
