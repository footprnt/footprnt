package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

public class StatViewHolder extends RecyclerView.ViewHolder {
    PieChart pieChart;

    public PieChart getmPieChart() {
        return pieChart;
    }

    public StatViewHolder(View v) {
        super(v);
        pieChart = v.findViewById(R.id.pieChart);
    }
}
