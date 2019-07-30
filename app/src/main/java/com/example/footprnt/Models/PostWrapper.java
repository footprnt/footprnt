/*
 * PostWrapper.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

/**
 * PostWrapper for storing Post Objects in database.
 * Table name is under @Entity annotation and the variables with @ColumnInfo annotation are the columns of the table
 *
 * @author Clarisa Leu-Rodriguez
 */
@Entity(tableName = "posts")
public class PostWrapper implements Serializable {
    // Attributes of PostWrapper:
    @PrimaryKey
    @NonNull
    public String objectId;

    @ColumnInfo(name = "updatedAt")
    public Date updatedAt;

    @ColumnInfo(name = "createdAt")
    public Date createdAt;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "continent")
    public String continent;

    @ColumnInfo(name = "imageUrl")
    public String imageUrl;

    @ColumnInfo(name = "username")
    public String username;


    /**
     * Default Constructor
     */
    public PostWrapper() {

    }

    /**
     * Constructor for PostWrapper
     *
     * @param post - post to wrap
     */
    public PostWrapper(Post post) {
        objectId = post.getObjectId();
        updatedAt = post.getUpdatedAt();
        createdAt = post.getCreatedAt();
        description = post.getDescription();
        latitude = post.getLocation().getLatitude();
        longitude = post.getLocation().getLongitude();
        city = post.getCity();
        country = post.getCountry();
        continent = post.getContinent();
        if (post.getImage() != null) {
            imageUrl = post.getImage().getUrl();
        } else {
            imageUrl = "";
        }
        username = post.getUser().getUsername();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
