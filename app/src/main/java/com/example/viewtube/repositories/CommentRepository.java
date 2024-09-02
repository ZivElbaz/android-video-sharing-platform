package com.example.viewtube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.CommentAPI;
import com.example.viewtube.entities.Comment;

import java.util.List;

public class CommentRepository {
    private CommentAPI commentAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private MutableLiveData<Comment> createdCommentLiveData;
    private MutableLiveData<Comment> updatedCommentLiveData;
    private MutableLiveData<Boolean> deleteCommentLiveData;

    public CommentRepository() {
        commentAPI = new CommentAPI();
        commentsLiveData = new MutableLiveData<>();
        createdCommentLiveData = new MutableLiveData<>();
        updatedCommentLiveData = new MutableLiveData<>();
        deleteCommentLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public LiveData<Comment> getCreatedCommentLiveData() {
        return createdCommentLiveData;
    }

    public LiveData<Comment> getUpdatedCommentLiveData() {
        return updatedCommentLiveData;
    }

    public LiveData<Boolean> getDeleteCommentLiveData() {
        return deleteCommentLiveData;
    }

    public void createComment(Comment comment) {
        commentAPI.createComment(comment, createdCommentLiveData);
    }

    public void getCommentsByVideoId(int videoId) {
        commentAPI.getCommentsByVideoId(videoId, commentsLiveData);
    }

    public void updateComment(Comment comment) {
        commentAPI.updateComment(comment, updatedCommentLiveData);
    }

    public void deleteComment(int commentId, int videoId) {
        commentAPI.deleteComment(commentId, videoId, deleteCommentLiveData);
    }
}
