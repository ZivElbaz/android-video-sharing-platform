package com.example.viewtube.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.viewtube.entities.Comment;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.Converters;

// Define the Room database for the application
@Database(entities = {VideoItem.class, Comment.class, User.class}, version = 10)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase {

    public abstract VideoDao videoDao();
    public abstract CommentDao commentDao();
    public abstract UserDao userDao();

    private static volatile AppDB INSTANCE;

    // Method to get the singleton instance of the database
    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    // Build the database with destructive migration strategy
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "ViewTubeDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
