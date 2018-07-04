package com.example.eliavmenachi.myapplication.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

public class Mall {

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String cityId;

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCityId() { return cityId;}

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
