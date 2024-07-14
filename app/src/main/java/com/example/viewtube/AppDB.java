package com.example.viewtube;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.viewtube.entities.VideoItem;

@Database(entities = {VideoItem.class}, version = 1)
public abstract class AppDB extends RoomDatabase{

    public abstract VideoDao videoDao();


}
