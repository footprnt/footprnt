/*
 * MapConstants.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Util;

/**
 * MapConstants used in application
 *
 * @author Clarisa Leu
 */
public class Constants {
    // For ParseApplication:
    public static final String applicationId = "explor";
    public static final String clientKey = "System.out.footprnt";
    public static final String server = "http://footprnt.herokuapp.com/parse";

    // For getting attributes in database:
    public static String profileImage = "profileImg";
    public static String phone = "phone";
    public static String email = "email";
    public static String createdAt = "createdAt";
    public static String image = "image";
    public static String user = "user";
    public static String objectId = "objectId";

    // For getting extras for intents:
    public static String post = "Post";
    public static String position = "Position";


    // For camera:
    public static String photoFileName = "photo.jpg";
    public static String fileProvider = "com.example.fileprovider";
    public static String profileImagePath = "profPic.jpg";  // Used in taking new profile picture
    public static String imagePath = "image.jpg";  // User in posting a new image
    public static int captureImageQuality = 100;

    // Request Codes User Throughout Application:
    public static final int SIGN_UP_ACTIVITY_REQUEST_CODE = 20;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final int RELOAD_USERPROFILE_FRAGMENT_REQUEST_CODE = 2001;
    public static final int GET_FROM_GALLERY = 3;
}
