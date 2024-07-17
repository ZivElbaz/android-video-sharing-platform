package com.example.viewtube.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.viewtube.entities.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User WHERE username = :username")
    User getUserByUsername(String username);

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

    @Query("DELETE FROM User WHERE username = :username")
    void deleteByUsername(String username);

    @Query("DELETE FROM User")
    void clear();
}
