package com.example.viewtube;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.viewtube.entities.VideoItem;

import java.util.List;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM VideoItem")
    LiveData<List<VideoItem>> getAll();

    @Query("SELECT * FROM VideoItem")
    List<VideoItem> getAllSync();

    @Query("SELECT * FROM VideoItem WHERE id = :id")
    LiveData<VideoItem> get(int id);

    @Insert
    void insert(VideoItem... video);

    @Insert
    void insertAll(List<VideoItem> videos);

    @Update
    void update(VideoItem... video);

    @Delete
    void delete(VideoItem... video);

    @Query("DELETE FROM VideoItem WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM VideoItem")
    void clear();

}
