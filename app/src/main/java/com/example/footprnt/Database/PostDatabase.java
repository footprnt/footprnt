package com.example.footprnt.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.footprnt.Dao.DaoAccess;
import com.example.footprnt.Models.PostWrapper;
import com.example.footprnt.Util.Converters;


@Database(entities = {PostWrapper.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PostDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}