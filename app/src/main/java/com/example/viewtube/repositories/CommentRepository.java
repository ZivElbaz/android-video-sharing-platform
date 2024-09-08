package com.example.viewtube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.CommentAPI;
import com.example.viewtube.entities.Comment;

import java.util.List;

// Repository class for managing comments data, interacting with CommentAPI and LiveData
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

    // Get LiveData for the list of comments
    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    // Get LiveData for the newly created comment
    public LiveData<Comment> getCreatedCommentLiveData() {
        return createdCommentLiveData;
    }

    // Get LiveData for the updated comment
    public LiveData<Comment> getUpdatedCommentLiveData() {
        return updatedCommentLiveData;
    }

    // Get LiveData for the delete comment status (true if deletion succeeded)
    public LiveData<Boolean> getDeleteCommentLiveData() {
        return deleteCommentLiveData;
    }

    // Call the API to create a new comment and update LiveData
    public void createComment(Comment comment) {
        commentAPI.createComment(comment, createdCommentLiveData);
    }

    // Fetch comments for a video by its ID and update LiveData
    public void getCommentsByVideoId(int videoId) {
        commentAPI.getCommentsByVideoId(videoId, commentsLiveData);
    }

    // Call the API to update a comment and update LiveData
    public void updateComment(Comment comment) {
        commentAPI.updateComment(comment, updatedCommentLiveData);
    }

    // Call the API to delete a comment by its ID and update LiveData
    public void deleteComment(int commentId, int videoId) {
        commentAPI.deleteComment(commentId, videoId, deleteCommentLiveData);
    }
}
