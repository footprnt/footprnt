package com.example.footprnt.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.footprnt.Dao.DaoAccess;
import com.example.footprnt.Models.Post;


@Database(entities = {Post.class}, version = 1, exportSchema = false)
public abstract class PostDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();

}