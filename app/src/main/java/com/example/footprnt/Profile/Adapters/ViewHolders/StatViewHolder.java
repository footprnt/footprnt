/*
 * StatViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

/**
 * View holder for user statistics item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 */
public class StatViewHolder extends RecyclerView.ViewHolder {
    PieChart mPieChartCity;
    PieChart mPieChartCountry;
    PieChart mPieChartContinent;

    /**
     * Getter for the PieChart for the cities traveled statistic
     *
     * @return PieChart for cities
     */
    public PieChart getPieChartCity() {
        return mPieChartCity;
    }

    /**
     * Getter for the PieChart for the countries traveled statistic
     *
     * @return PieChart for countries
     */
    public PieChart getPieChartCountry() {
        return mPieChartCountry;
    }

    /**
     * Getter for the PieChart for the continents traveled statistic
     *
     * @return PieChart for continents
     */
    public PieChart getPieChartContinent() {
        return mPieChartContinent;
    }

    /**
     * Constructor for StatViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public StatViewHolder(View v) {
        super(v);
        mPieChartCity = v.findViewById(R.id.pieChartCity);
        mPieChartCountry = v.findViewById(R.id.pieChartCountry);
        mPieChartContinent = v.findViewById(R.id.pieChartContinent);
    }
}
