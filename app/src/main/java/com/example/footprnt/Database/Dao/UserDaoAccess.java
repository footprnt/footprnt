package com.example.footprnt.Database.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.footprnt.Database.Models.UserWrapper;

/**
 * @author Clarisa Leu-Rodriguez
 */
@Dao
public interface UserDaoAccess {
    @Insert
    void insertUser(UserWrapper user);

    @Query("SELECT * FROM user")
    UserWrapper fetchUser();

    @Query("SELECT * FROM user WHERE username=:username")
    UserWrapper getUser(String username);
}


