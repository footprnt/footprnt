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

    // Tags properties
    public static String CULTURE = "culture";
    public static String FOOD = "food";
    public static String FASHION = "fashion";
    public static String TRAVEL = "travel";
    public static String NATURE = "nature";
    public static int POST_RADIUS = 5;

    // Map Styles:
    public static String MAP_STYLE = "map_style";
    public static int STYLE_AUBERGINE = R.raw.style_json_aubergine;
    public static int STYLE_DARKMODE = R.raw.style_json_darkmode;
    public static int STYLE_SILVER = R.raw.style_json_silver;
    public static int STYLE_RETRO = R.raw.style_json_retro;
    public static int STYLE_BASIC = R.raw.style_json_basic;
    public static BitmapDescriptor DEFAULT_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

    // Filter menu items
    public static String MENU_ITEMS[] = {"Create", "Current", "Discover", "View", "Street"};
    public static String CREATE = "Create";
    public static String VIEW = "View";
    public static String DISCOVER = "Discover";
    public static String CURRENT = "Current";
    public static String STREET = "Street";
}