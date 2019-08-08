/*
 * AppConstants.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Util;

/**
 * Constants used in application
 *
 * @author Clarisa Leu, Jocelyn Shen, Stanley
 * @version 1.0
 * @since 2019-07-22
 */
public class AppConstants {

    // For Database:
    public static final String POST_DB_NAME = "db_posts";
    public static final String STATS_DB_NAME = "db_stats";
    public static final String USER_DB_NAME = "db_user";

    // For Facebook Permissions:
    public static final String PUBLIC_PROFILE = "public_profile";
    public static final String USER_FRIENDS = "user_friends";

    // For getting attributes in database:
    public static String profileImage = "profileImg";
    public static String savedPost = "SavedPost";
    public static String savedActivity = "SavedActivity";
    public static String phone = "phone";
    public static String email = "email";
    public static String createdAt = "createdAt";
    public static String image = "image";
    public static String user = "user";
    public static String objectId = "objectId";
    public static String description = "description";
    public static final String location = "location";
    public static final String username = "username";
    public static final String name = "name";
    public static final String title = "title";
    public static final String country = "country";
    public static final String city = "city";
    public static final String continent = "continent";
    public static final String tags = "tags";
    public static final String privacy = "private";
    public static final String imageUrl = "imageUrl";
    public static final String address = "address";
    public static final String rating = "rating";
    public static final String website = "website";
    public static final String categories = "categories";
    public static final String uncompleted_adventure = "uncompleted_adventure";
    public static final String completed_adventure = "completed_adventure";


    public static final int postLimit = 20;

    // For getting extras for intents:
    public static String post = "post";
    public static String Post = "Post";
    public static String position = "Position";

    // For camera:
    public static String photoFileName = "photo.jpg";
    public static String photoFileNameShare = "share.jpg";
    public static String photoType = "image/jpeg";
    public static String fileProvider = "com.example.fileprovider";
    public static String profileImagePath = "profPic.jpg";  // Used in taking new profile picture
    public static String profileImagePathJPEG = "profPic.jpeg";
    public static String imagePath = "image.jpg";  // User in posting a new image
    public static int captureImageQuality = 100;

    // Request Codes User Throughout Application:
    public static final int SIGN_UP_ACTIVITY_REQUEST_CODE = 20;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final int RELOAD_USER_PROFILE_FRAGMENT_REQUEST_CODE = 2001;
    public static final int GET_FROM_GALLERY = 3;
    public static final int UPDATE_POST_FROM_PROFILE = 301;
    public static final int DELETE_POST_FROM_PROFILE = 302;
    public static final int SAVED_POST_DETAILS_FROM_PROFILE = 2005;
    public static final int VIEW_BUSINESS_PAGE = 21;

    // For saved posts & sharing:
    public static final String hideView = "hideView";
    public static final String send = "send";
}
