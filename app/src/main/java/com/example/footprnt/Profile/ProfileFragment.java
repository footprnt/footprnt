package com.example.footprnt.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.footprnt.MainActivity;
import com.example.footprnt.Models.Post;
import com.example.footprnt.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    public final static String TAG = "ProfileFragment";  // tag for logging from this activity
    // For user profile info view:
    CircleImageView ivProfileImage;
    TextView tvEditProfileImage;

    // For stats view:
    ArrayAdapter<String> statAdapter;  // Adapter for stats
    ArrayList<String> stats;
    ListView lvStats;
    HashMap<String, Integer> cities;
    HashMap<String, Integer> countries;
    HashMap<String, Integer> continents;
    ArrayList<HashMap<String,Integer>> statsList;


    // For post feed:
    ArrayList<Post> posts;  // list of current user posts
    RecyclerView rvPosts;
    PostAdapter postAdapter;
    SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Log out button
        final ImageView settings = v.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), settings);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ParseUser.logOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                popup.show();  //showing popup menu
            }
        });

        // For profile image:
        ivProfileImage = v.findViewById(R.id.ivProfileImageMain);
        if (ParseUser.getCurrentUser().getParseFile("profileImg") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("profileImg").getUrl()).into(ivProfileImage);
        } else {
            Glide.with(getContext()).load(R.drawable.ic_user).into(ivProfileImage);
        }

        tvEditProfileImage = v.findViewById(R.id.tvEditPhoto);
        tvEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Upload or Take a Photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Upload image
                    }
                });
                builder.setNegativeButton("Take a Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Take Photo
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        // Populate stat maps and get posts
        posts = new ArrayList<>();
        cities = new HashMap<>();
        countries = new HashMap<>();
        continents = new HashMap<>();
        getPosts();
        Log.e(TAG, String.format("Posts: %s, Cities: %s, Countries: %s, Continents: %s",posts.size(), cities.size(), countries.size(), continents.size()));

        // Populate statsList
        statsList  = new ArrayList<>();
        statsList.add(cities);
        statsList.add(countries);
        statsList.add(continents);
        updateStats();

        // For post feed view:
        postAdapter = new PostAdapter(posts);
        rvPosts = v.findViewById(R.id.rvFeed);
        rvPosts.setLayoutManager(new GridLayoutManager(v.getContext(), 3));
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setHasFixedSize(true);
        rvPosts.setAdapter(postAdapter);

        // For stat view
        lvStats = v.findViewById(R.id.lvStatKey);
        //stats = new ArrayList<>();

        //statAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, stats);
        //lvStats.setAdapter(statAdapter);


        // Refresh listener for post feed
        swipeContainer = v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });


        return v;
    }

    // Get posts
    private void getPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery
                .getTop()
                .withUser();
        postsQuery.addDescendingOrder("createdAt");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = (Post) objects.get(i);
                        // Only add current user's posts
                        if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            posts.add(post);
                            postAdapter.notifyItemInserted(posts.size() - 1);
                            // Get post stats and update user stats
                            LatLng pt = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
                            ArrayList<String> postStats = getAddress(getContext(), pt);
                            // Fill HashMaps
                            // Cities
                            if (!cities.containsKey(postStats.get(0))) {
                                // User first visit
                                cities.put(postStats.get(0), 1);
                            } else {
                                // User already visited, increment count
                                cities.put(postStats.get(0), cities.get(postStats.get(0)) + 1);
                            }
                            // Countries
                            if (!countries.containsKey(postStats.get(1))) {
                                countries.put(postStats.get(1), 1);
                            } else {
                                countries.put(postStats.get(1), countries.get(postStats.get(1)) + 1);
                            }
                            // Continents
                            if (!continents.containsKey(postStats.get(2))) {
                                continents.put(postStats.get(2), 1);
                            } else {
                                continents.put(postStats.get(2), continents.get(postStats.get(2)) + 1);
                            }

                        }
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    logError("Error querying posts", e, true);
                }
            }
        });
    }


    private void updateStats() {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for(int i = 0; i < statsList.size(); i++ ){  // Loop through number of stats we're tracking
            HashMap<String, Integer> innerList = statsList.get(i);
            ArrayList<String> toAdd = new ArrayList<>();
            toAdd.add(String.format("%s",innerList.size()));
            res.add(toAdd);
        }
        final ParseUser user = ParseUser.getCurrentUser();
        user.put("stats", res);
        user.saveInBackground();
    }


    // TODO: implement on click for item to allow user to edit post
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // Helper method to handle errors, log them, and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        Log.e(TAG, message, error);
        if (alertUser) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    // Returns ArrayList<String> of the [City, Country, and Continent] of point
    public ArrayList<String> getAddress(Context context, LatLng point) {
        try {
            String json_str = "{\"AD\":\"Europe\",\"AE\":\"Asia\",\"AF\":\"Asia\",\"AG\":\"North America\",\"AI\":\"North America\",\"AL\":\"Europe\",\"AM\":\"Asia\",\"AN\":\"North America\",\"AO\":\"Africa\",\"AQ\":\"Antarctica\",\"AR\":\"South America\",\"AS\":\"Australia\",\"AT\":\"Europe\",\"AU\":\"Australia\",\"AW\":\"North America\",\"AZ\":\"Asia\",\"BA\":\"Europe\",\"BB\":\"North America\",\"BD\":\"Asia\",\"BE\":\"Europe\",\"BF\":\"Africa\",\"BG\":\"Europe\",\"BH\":\"Asia\",\"BI\":\"Africa\",\"BJ\":\"Africa\",\"BM\":\"North America\",\"BN\":\"Asia\",\"BO\":\"South America\",\"BR\":\"South America\",\"BS\":\"North America\",\"BT\":\"Asia\",\"BW\":\"Africa\",\"BY\":\"Europe\",\"BZ\":\"North America\",\"CA\":\"North America\",\"CC\":\"Asia\",\"CD\":\"Africa\",\"CF\":\"Africa\",\"CG\":\"Africa\",\"CH\":\"Europe\",\"CI\":\"Africa\",\"CK\":\"Australia\",\"CL\":\"South America\",\"CM\":\"Africa\",\"CN\":\"Asia\",\"CO\":\"South America\",\"CR\":\"North America\",\"CU\":\"North America\",\"CV\":\"Africa\",\"CX\":\"Asia\",\"CY\":\"Asia\",\"CZ\":\"Europe\",\"DE\":\"Europe\",\"DJ\":\"Africa\",\"DK\":\"Europe\",\"DM\":\"North America\",\"DO\":\"North America\",\"DZ\":\"Africa\",\"EC\":\"South America\",\"EE\":\"Europe\",\"EG\":\"Africa\",\"EH\":\"Africa\",\"ER\":\"Africa\",\"ES\":\"Europe\",\"ET\":\"Africa\",\"FI\":\"Europe\",\"FJ\":\"Australia\",\"FK\":\"South America\",\"FM\":\"Australia\",\"FO\":\"Europe\",\"FR\":\"Europe\",\"GA\":\"Africa\",\"GB\":\"Europe\",\"GD\":\"North America\",\"GE\":\"Asia\",\"GF\":\"South America\",\"GG\":\"Europe\",\"GH\":\"Africa\",\"GI\":\"Europe\",\"GL\":\"North America\",\"GM\":\"Africa\",\"GN\":\"Africa\",\"GP\":\"North America\",\"GQ\":\"Africa\",\"GR\":\"Europe\",\"GS\":\"Antarctica\",\"GT\":\"North America\",\"GU\":\"Australia\",\"GW\":\"Africa\",\"GY\":\"South America\",\"HK\":\"Asia\",\"HN\":\"North America\",\"HR\":\"Europe\",\"HT\":\"North America\",\"HU\":\"Europe\",\"ID\":\"Asia\",\"IE\":\"Europe\",\"IL\":\"Asia\",\"IM\":\"Europe\",\"IN\":\"Asia\",\"IO\":\"Asia\",\"IQ\":\"Asia\",\"IR\":\"Asia\",\"IS\":\"Europe\",\"IT\":\"Europe\",\"JE\":\"Europe\",\"JM\":\"North America\",\"JO\":\"Asia\",\"JP\":\"Asia\",\"KE\":\"Africa\",\"KG\":\"Asia\",\"KH\":\"Asia\",\"KI\":\"Australia\",\"KM\":\"Africa\",\"KN\":\"North America\",\"KP\":\"Asia\",\"KR\":\"Asia\",\"KW\":\"Asia\",\"KY\":\"North America\",\"KZ\":\"Asia\",\"LA\":\"Asia\",\"LB\":\"Asia\",\"LC\":\"North America\",\"LI\":\"Europe\",\"LK\":\"Asia\",\"LR\":\"Africa\",\"LS\":\"Africa\",\"LT\":\"Europe\",\"LU\":\"Europe\",\"LV\":\"Europe\",\"LY\":\"Africa\",\"MA\":\"Africa\",\"MC\":\"Europe\",\"MD\":\"Europe\",\"ME\":\"Europe\",\"MG\":\"Africa\",\"MH\":\"Australia\",\"MK\":\"Europe\",\"ML\":\"Africa\",\"MM\":\"Asia\",\"MN\":\"Asia\",\"MO\":\"Asia\",\"MP\":\"Australia\",\"MQ\":\"North America\",\"MR\":\"Africa\",\"MS\":\"North America\",\"MT\":\"Europe\",\"MU\":\"Africa\",\"MV\":\"Asia\",\"MW\":\"Africa\",\"MX\":\"North America\",\"MY\":\"Asia\",\"MZ\":\"Africa\",\"NA\":\"Africa\",\"NC\":\"Australia\",\"NE\":\"Africa\",\"NF\":\"Australia\",\"NG\":\"Africa\",\"NI\":\"North America\",\"NL\":\"Europe\",\"NO\":\"Europe\",\"NP\":\"Asia\",\"NR\":\"Australia\",\"NU\":\"Australia\",\"NZ\":\"Australia\",\"OM\":\"Asia\",\"PA\":\"North America\",\"PE\":\"South America\",\"PF\":\"Australia\",\"PG\":\"Australia\",\"PH\":\"Asia\",\"PK\":\"Asia\",\"PL\":\"Europe\",\"PM\":\"North America\",\"PN\":\"Australia\",\"PR\":\"North America\",\"PS\":\"Asia\",\"PT\":\"Europe\",\"PW\":\"Australia\",\"PY\":\"South America\",\"QA\":\"Asia\",\"RE\":\"Africa\",\"RO\":\"Europe\",\"RS\":\"Europe\",\"RU\":\"Europe\",\"RW\":\"Africa\",\"SA\":\"Asia\",\"SB\":\"Australia\",\"SC\":\"Africa\",\"SD\":\"Africa\",\"SE\":\"Europe\",\"SG\":\"Asia\",\"SH\":\"Africa\",\"SI\":\"Europe\",\"SJ\":\"Europe\",\"SK\":\"Europe\",\"SL\":\"Africa\",\"SM\":\"Europe\",\"SN\":\"Africa\",\"SO\":\"Africa\",\"SR\":\"South America\",\"ST\":\"Africa\",\"SV\":\"North America\",\"SY\":\"Asia\",\"SZ\":\"Africa\",\"TC\":\"North America\",\"TD\":\"Africa\",\"TF\":\"Antarctica\",\"TG\":\"Africa\",\"TH\":\"Asia\",\"TJ\":\"Asia\",\"TK\":\"Australia\",\"TM\":\"Asia\",\"TN\":\"Africa\",\"TO\":\"Australia\",\"TR\":\"Asia\",\"TT\":\"North America\",\"TV\":\"Australia\",\"TW\":\"Asia\",\"TZ\":\"Africa\",\"UA\":\"Europe\",\"UG\":\"Africa\",\"US\":\"North America\",\"UY\":\"South America\",\"UZ\":\"Asia\",\"VC\":\"North America\",\"VE\":\"South America\",\"VG\":\"North America\",\"VI\":\"North America\",\"VN\":\"Asia\",\"VU\":\"Australia\",\"WF\":\"Australia\",\"WS\":\"Australia\",\"YE\":\"Asia\",\"YT\":\"Africa\",\"ZA\":\"Africa\",\"ZM\":\"Africa\",\"ZW\":\"Africa\"}";
            JSONObject jsonObject = new JSONObject(json_str);
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.isEmpty()) {
                return new ArrayList<>();
            } else {
                if (addresses.size() > 0) {
                    ArrayList<String> res = new ArrayList<>();
                    String city = addresses.get(0).getLocality();
                    res.add(city);
                    String country = addresses.get(0).getCountryName();
                    res.add(country);
                    String continent = jsonObject.getString(addresses.get(0).getCountryCode());
                    res.add(continent);
                    return res;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
            return null;
        }
        return null;
    }
}
