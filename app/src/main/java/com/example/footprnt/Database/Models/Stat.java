/*
 * Stat.java
 * v1.0
 * July 2019
 * Copyright ©2019 Footprnt Inc.
 */
package com.example.footprnt.Database.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "stats")
public class Stat implements Serializable {

    // Attributes of Stat:
    @PrimaryKey
    @NonNull
    public String objectId;

    @ColumnInfo(name = "stat")
    public String stat;

    @NonNull
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(@NonNull String objectId) {
        this.objectId = objectId;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}