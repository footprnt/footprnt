/*
 * PostDatabase.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.footprnt.Database.Dao.UserDaoAccess;
import com.example.footprnt.Database.Models.UserWrapper;
import com.example.footprnt.Util.AppConstants;
import com.example.footprnt.Util.Converters;


/**
 * UserDatabase is an abstract class where all the entities (i.e. tables that you want to create for the UserDatabase) are defined.
 * All the lists of operations we would like to perform on the UserDatabase will be defined in UserDaoAccess.java
 *
 * @author Clarisa Leu-Rodriguez
 */

@Database(entities = {UserWrapper.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase INSTANCE;

    public abstract UserDaoAccess daoAccess();

    public static UserDatabase getUserDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            UserDatabase.class, AppConstants.USER_DB_NAME)
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