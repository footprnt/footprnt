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

import com.example.footprnt.Database.Models.Stat;
import com.example.footprnt.Database.StatDatabase;

import java.util.List;

/**
 * Repository to mediate between the domain and data mapping layers, acting like an in-memory domain object
 * collection. We access the database class and the DAO class from the repository and perform list of
 * operations such as insert, update, delete, get, etc.
 *
 * @author Clarisa Leu
 */
public class StatRepository {

    public StatDatabase mStatDatabase;
    public List<Stat> mStats;

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
    public void insertStat(final Stat stat) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (mStatDatabase.daoAccess().getStat(stat.getObjectId()) == null) {
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
    public List<Stat> getStats() {
        if(mStats==null){
            mStats = mStatDatabase.daoAccess().fetchAllStats();
        }
        return mStats;
    }

}

