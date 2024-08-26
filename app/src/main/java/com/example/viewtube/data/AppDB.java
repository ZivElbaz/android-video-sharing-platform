package com.example.viewtube.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.viewtube.entities.Comment;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;

@Database(entities = {VideoItem.class, Comment.class, User.class}, version = 8)
public abstract class AppDB extends RoomDatabase {

    public abstract VideoDao videoDao();
    public abstract CommentDao commentDao();
    public abstract UserDao userDao();

    private static volatile AppDB INSTANCE;

    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
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
