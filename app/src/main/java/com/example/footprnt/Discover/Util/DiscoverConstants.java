/*
 * DiscoverConstants.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Discover.Util;

/**
 * DiscoverConstants used in Discover Fragment
 *
 * @author Stanley Nwakamma, Clarisa Leu-Rodriguez, Jocelyn Shen
 */
public class DiscoverConstants {
    // For connecting with Yelp and querying:
    public static final String YELP_TOKEN = "Bearer k34xmk0G2uUZnaKukdwytk36ALUe0dtxY38bZcZjovZnmHuSW4VjJ1UPy0DimPsw7pOdYscHus84r9l8YoopJo-68O9iVN0LYjf7W6KFD3RrrUeSo0BodpAWZv85XXYx";
    public static final String YELP_BASE_URL = "https://api.yelp.com/v3/businesses/search?location=";
    public static final String YELP_EVENT_BASE_URL = "https://api.yelp.com/v3/events?location=";
    public static final String RESTAURANT = "restaurant";
    public static final String HOTEL = "hotel";
    public static final String MUSEUM = "museum";
    public static final String CLUB = "club";
    public static final String DEFAULT_LOCATION = "1 Hacker Way Menlo Park, CA 94025";

    // For business views & display:
    public static final String IMAGE_PLACEHOLDER_PATH = "https://pyzikscott.files.wordpress.com/2016/03/yelp-placeholder.png?w=584";
    public static final int CARD_ELEVATION = 30;
    public static final String NO_BUSINESS_MESSAGE = "No businesses here";
    public static final String NOT_VALID_LOCATION_MESSAGE = "Not valid location";

}
