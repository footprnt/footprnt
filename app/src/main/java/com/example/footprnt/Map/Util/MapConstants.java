package com.example.footprnt.Map.Util;

import com.example.footprnt.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Contains all constants for map fragment
 *
 * @author Jocelyn Shen, Clarisa Leu
 * @version 1.0
 * @since 2019-07-22
 */
public class MapConstants {

    // Map properties
    public static String CONTINENTS = "continents.json";

    // Tags properties
    public static String CULTURE = "culture";
    public static String FOOD = "food";
    public static String FASHION = "fashion";
    public static String TRAVEL = "travel";
    public static String NATURE = "nature";
    public static int POST_RADIUS = 50;

    // Map Styles:
    public static String MAP_STYLE = "map_style";
    public static int STYLE_AUBERGINE = R.raw.style_json_aubergine;
    public static int STYLE_DARKMODE = R.raw.style_json_darkmode;
    public static int STYLE_SILVER = R.raw.style_json_silver;
    public static int STYLE_RETRO = R.raw.style_json_retro;
    public static int STYLE_BASIC = R.raw.style_json_basic;
    public static BitmapDescriptor DEFAULT_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
    public static BitmapDescriptor MARKER_CYAN = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
    public static BitmapDescriptor MARKER_AZURE = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

    // Filter menu items
    public static String MENU_ITEMS[] = {"Create", "Current", "Discover", "View", "Street"};
    public static String CREATE = "Create";
    public static String VIEW = "View";
    public static String DISCOVER = "Discover";
    public static String CURRENT = "Current";
    public static String STREET = "Street";

    // Intent variables
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";

    // Privacy settings
    public static String PLACEHOLDER_IMAGE = "http://via.placeholder.com/300.png";
    public static String ANONYMOUS = "Anonymous User";
}