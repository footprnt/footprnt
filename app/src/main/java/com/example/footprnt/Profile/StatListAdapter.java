/*
 * Copyright 2019 Footprnt Inc.
 */
package com.example.footprnt.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.footprnt.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays stats to list view in ProfileFramgnet
 * Created by Clarisa Leu 2019
 */
public class StatListAdapter extends BaseAdapter {
    public static final String TAG = "StatListAdapter";
    ArrayList<HashMap<String, Integer>> mStats;    // list of stats
    Context mContext;          // Context for rendering
    private static LayoutInflater sInflater = null;


    public StatListAdapter(ArrayList<HashMap<String, Integer>> mStats, Context mContext) {
        this.mStats = mStats;
        this.mContext = mContext;
        sInflater = (LayoutInflater) mContext
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mStats.size();
    }

    @Override
    public Object getItem(int position) {
        return mStats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = sInflater.inflate(R.layout.row, null);
        TextView count = v.findViewById(R.id.tvCount);
        TextView title = v.findViewById(R.id.tvTitle);
        count.setText(mStats.get(position).get("cities"));
        title.setText("cities");
        return v;
    }
}
