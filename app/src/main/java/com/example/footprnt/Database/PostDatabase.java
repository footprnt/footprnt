/*
 * PostDatabase.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.footprnt.Database.Dao.PostDaoAccess;
import com.example.footprnt.Database.Models.PostWrapper;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.Converters;


/**
 * PostDatabase is an abstract class where all the entities (i.e. tables that you want to create for the PostDatabase) are defined.
 * All the lists of operations we would like to perform on the PostDatabase will be defined in PostDaoAccess.java
 *
 * @author Clarisa Leu-Rodriguez
 */

@Database(entities = {PostWrapper.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PostDatabase extends RoomDatabase {

    private static PostDatabase INSTANCE;

    public abstract PostDaoAccess daoAccess();

    public static PostDatabase getPostDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PostDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            PostDatabase.class, AppConstants.POST_DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    public static void destroyInstance() {
        INSTANCE = null;
    }
}