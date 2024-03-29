/*
 * UserWrapper.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.footprnt.Util.AppConstants;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * UserWrapper for storing ParseUser object in database.
 * Table name is under @Entity annotation and the variables with @ColumnInfo annotation are the columns of the table
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
        ParseFile image = user.getParseFile(AppConstants.profileImage);
        if (image != null) {
            this.profileImg = image.getUrl();
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
