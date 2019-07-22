package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

public class StatViewHolder extends RecyclerView.ViewHolder {
    PieChart pieChartCity;
    PieChart pieChartCountry;
    PieChart pieChartContinent;

    public PieChart getmPieChartCity() {
        return pieChartCity;
    }

    public PieChart getmPieChartCountry() {
        return pieChartCountry;
    }

    public PieChart getmPieChartContinent() {
        return pieChartContinent;
    }


    public StatViewHolder(View v) {
        super(v);
        pieChartCity = v.findViewById(R.id.pieChartCity);
        pieChartCountry = v.findViewById(R.id.pieChartCountry);
        pieChartContinent = v.findViewById(R.id.pieChartContinent);
    }
}
