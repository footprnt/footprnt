/*
 * StatViewHolder.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Profile.Adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.example.footprnt.R;
import com.github.mikephil.charting.charts.PieChart;

/**
 * View holder for user statistics item for profile page
 *
 * @author Clarisa Leu-Rodriguez
 */
public class StatViewHolder extends RecyclerView.ViewHolder {
    ViewFlipper mViewFlipper;
    Button mNextView;
    Button mPreviousView;
    PieChart mPieChartCity;
    PieChart mPieChartCountry;
    PieChart mPieChartContinent;

    /**
     * Getter for the ViewFlipper to switch between pie charts
     *
     * @return ViewFlipper to switch views
     */
    public ViewFlipper getViewFlipper() {
        return mViewFlipper;
    }

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
     * Getter for next button
     *
     * @return next button for next view in view flipper
     */
    public Button getNextButton() {
        return mNextView;
    }

    /**
     * Getter for previous button
     *
     * @return previous button for previous view in view flipper
     */
    public Button getPreviousButton() {
        return mPreviousView;
    }

    /**
     * Constructor for StatViewHolder
     *
     * @param v view to define and describe meta data for
     */
    public StatViewHolder(View v) {
        super(v);
        mViewFlipper = v.findViewById(R.id.viewFlipper);
        mNextView = v.findViewById(R.id.btnNext);
        mPreviousView = v.findViewById(R.id.btnPrev);
        mPieChartCity = v.findViewById(R.id.pieChartCity);
        mPieChartCountry = v.findViewById(R.id.pieChartCountry);
        mPieChartContinent = v.findViewById(R.id.pieChartContinent);
    }

    /**
     * Show the previous view for the view flipper
     *
     * @param v view
     */
    public void previousView(View v) {
        mViewFlipper.setInAnimation(v.getContext(), R.anim.slide_in_right);
        mViewFlipper.setOutAnimation(v.getContext(), R.anim.slide_in_right);
        mViewFlipper.showPrevious();
    }

    /**
     * Shows the next view for the view flipper
     *
     * @param v view
     */
    public void nextView(View v) {
        mViewFlipper.setInAnimation(v.getContext(), android.R.anim.slide_in_left);
        mViewFlipper.setOutAnimation(v.getContext(), android.R.anim.slide_out_right);
        mViewFlipper.showNext();
    }
}
