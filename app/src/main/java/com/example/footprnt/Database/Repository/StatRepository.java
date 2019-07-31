package com.example.footprnt.Database.Repository;

/*
 * StatRepository.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.Models.StatWrapper;
import com.example.footprnt.Database.StatDatabase;

/**
 * Repository to mediate between the domain and data mapping layers, acting like an in-memory domain object
 * collection. We access the database class and the DAO class from the repository and perform list of
 * operations such as insert, update, delete, get, etc.
 *
 * @author Clarisa Leu
 */
public class StatRepository {

    public StatDatabase mStatDatabase;
    public StatWrapper mStats;

    public StatRepository(Context context) {
        mStatDatabase = StatDatabase.getStatDatabase(context);
    }

    public StatDatabase getStatsDatabase() {
        return mStatDatabase;
    }

    /**
     * Insert post into database
     *
     * @param stat
     */
    @SuppressLint("StaticFieldLeak")
    public void insertStat(final StatWrapper stat) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (mStatDatabase.daoAccess().getStat(stat.getUsername()) == null) {
                    mStatDatabase.daoAccess().insertStat(stat);
                }
                return null;
            }
        }.execute();
    }

    /**
     * Get all stats in database
     *
     * @return all posts
     */
    public StatWrapper getStats() {
        if(mStats==null){
            mStats = mStatDatabase.daoAccess().fetchAllStats();
        }
        return mStats;
    }

    /**
     * Get stat in DB with username
     *
     * @param username
     * @return stat with associated user
     */
    public StatWrapper getStat(String username) {
        return mStatDatabase.daoAccess().getStat(username);
    }

}

