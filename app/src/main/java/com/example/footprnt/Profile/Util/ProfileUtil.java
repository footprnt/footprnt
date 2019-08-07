/*
 * Profile.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Util;

import android.util.Log;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;

import java.util.List;

/**
 * Utility Functions for the Profile Fragment
 *
 * @author Clarisa Leu
 * @version 1.0
 * @since 7-22-19
 */
public class ProfileUtil {

    /**
     * Helper method to get photo from location using the Unsplash API
     *
     * @param query    photo to get
     * @param unsplash
     * @return
     */
    public static String getPhotoFromLocation(String query, Unsplash unsplash) {
        final String[] photoUrl = {""};
        unsplash.searchPhotos(query, new Unsplash.OnSearchCompleteListener() {
            @Override
            public void onComplete(SearchResults results) {
                Log.d("Photos", "Total Results Found " + results.getTotal());
                List<Photo> photos = results.getResults();
                photoUrl[0] = photos.get(0).getUrls().getRegular();
            }

            @Override
            public void onError(String error) {
                Log.d("Unsplash", error);
            }
        });
        return photoUrl[0];
    }


}
