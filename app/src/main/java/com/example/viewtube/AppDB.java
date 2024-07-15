package com.example.viewtube;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.viewtube.entities.VideoItem;

@Database(entities = {VideoItem.class}, version = 5)
public abstract class AppDB extends RoomDatabase {

    public abstract VideoDao videoDao();

    private static volatile AppDB INSTANCE;

    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "VideosDB")
                            .fallbackToDestructiveMigration() // This line enables fallback to destructive migration
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
