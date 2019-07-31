/*
 * PostRepository.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.footprnt.Database.Models.UserWrapper;
import com.example.footprnt.Database.UserDatabase;

/**
 * Repository to mediate between the domain and data mapping layers, acting like an in-memory domain object
 * collection. We access the database class and the DAO class from the repository and perform list of
 * operations such as insert, update, delete, get, etc.
 *
 * @author Clarisa Leu
 */
public class UserRepository {

    public UserDatabase mUserDatabase;
    public UserWrapper mUserWrapper;

    public UserRepository(Context context) {
        mUserDatabase = UserDatabase.getUserDatabase(context);
    }


    public UserDatabase getUserDatabase() {
        return mUserDatabase;
    }

    /**
     * Insert user into database
     *
     * @param user
     */
    @SuppressLint("StaticFieldLeak")
    public void insertUser(final UserWrapper user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (mUserDatabase.daoAccess().getUser(user.username) == null) {
                    mUserDatabase.daoAccess().insertUser(user);
                }
                return null;
            }
        }.execute();
    }

    /**
     * Get user from db
     *
     * @return all posts
     */
    public UserWrapper getUser() {
        if (mUserWrapper == null) {
            mUserWrapper = mUserDatabase.daoAccess().fetchUser();
        }
        return mUserWrapper;
    }


    /**
     * Get user with username
     *
     * @param username
     * @return user with username
     */
    public UserWrapper getUser(String username) {
        return mUserDatabase.daoAccess().getUser(username);
    }
}
