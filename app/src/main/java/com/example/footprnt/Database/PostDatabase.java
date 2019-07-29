/*
 * PostDatabase.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.footprnt.Dao.PostDaoAccess;
import com.example.footprnt.Models.PostWrapper;
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

    public abstract PostDaoAccess daoAccess();
}