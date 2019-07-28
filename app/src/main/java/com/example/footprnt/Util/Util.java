/*
 * MapUtil.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Utility functions used throughout application
 * @author Clarisa Leu, Jocelyn Shen
 */
public class Util {
    public final String APP_TAG = "footprnt";

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public File getPhotoFileUri(Context context, String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    /**
     *
     * @param context
     * @param point
     * @return
     */
    public String getAddress(Context context, LatLng point) {
        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.isEmpty()) {
                return "Waiting for location...";
            } else {
                if (addresses.size() > 0) {
                    String address = (addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    address = address.replaceAll(" null,", "");
                    address = address.replaceAll(", null", "");
                    return address;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
            return null;
        }
        return null;
    }

    /**
     * Gets the relative date of post
     * @param rawJsonDate raw data from parse object
     * @return relative time ago
     */
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

}
