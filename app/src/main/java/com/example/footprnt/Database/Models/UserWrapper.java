/*
 * UserWrapper.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.footprnt.Util.AppConstants;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Model class for database
 *
 * @author Clarisa Leu-Rodriguez
 */
@Entity(tableName = "user")
public class UserWrapper implements Serializable {
    @PrimaryKey
    @NonNull
    public String username;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "profileImg")
    public String profileImg;

    public UserWrapper() {
    }

    public UserWrapper(ParseUser user) {
        this.username = user.getUsername();
        String description = user.getString(AppConstants.description);
        if (description != null) {
            this.description = description;
        } else {
            this.description = "";
        }
        String imageUrl = user.getParseFile(AppConstants.profileImage).getUrl();
        if (imageUrl != null) {
            this.profileImg = imageUrl;
        } else {
            this.profileImg = "";
        }
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
