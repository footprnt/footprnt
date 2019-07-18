package com.example.footprnt.Profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.footprnt.R;

import java.util.ArrayList;
import java.util.HashMap;

public class StatListAdapter extends BaseAdapter {
    private static final String TAG = "StatListAdapter";
    // Instance fields:
    ArrayList<HashMap<String,Integer>> stats;    // list of stats
    Context context;          // context for rendering
    private static LayoutInflater inflater = null;


    public StatListAdapter(ArrayList<HashMap<String, Integer>> stats, Context context) {
        this.stats = stats;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stats.size();
    }

    @Override
    public Object getItem(int position) {
        return stats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView count = (TextView) vi.findViewById(R.id.tvCount);
        TextView title = (TextView) vi.findViewById(R.id.tvTitle);
        count.setText(stats.get(position).get("cities"));
        title.setText("cities");
        Log.e("StatAdapter","Running Adapter", new Throwable());
        return vi;
    }
}
