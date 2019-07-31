package com.example.footprnt.Database;

/*
 * PostDatabase.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.footprnt.Database.Dao.StatDaoAccess;
import com.example.footprnt.Database.Models.Stat;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.Converters;


/**
 * PostDatabase is an abstract class where all the entities (i.e. tables that you want to create for the StatDatabase) are defined.
 * All the lists of operations we would like to perform on the StatDatabase will be defined in StatDaoAccess.java
 *
 * @author Clarisa Leu-Rodriguez
 */

@Database(entities = {Stat.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class StatDatabase extends RoomDatabase {

    private static StatDatabase INSTANCE;

    public abstract StatDaoAccess daoAccess();

    public static StatDatabase getStatDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PostDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            StatDatabase.class, AppConstants.STATS_DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
