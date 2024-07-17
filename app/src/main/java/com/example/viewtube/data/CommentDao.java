package com.example.viewtube.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.viewtube.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM Comment WHERE videoId = :videoId")
    LiveData<List<Comment>> getCommentsByVideoId(int videoId);

    @Query("SELECT * FROM Comment WHERE id = :id")
    Comment getCommentSync(int id);

    @Insert
    void insert(Comment... comments);

    @Update
    void update(Comment... comments);

    @Delete
    void delete(Comment... comments);

    @Query("DELETE FROM Comment WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM Comment")
    void clear();
}
