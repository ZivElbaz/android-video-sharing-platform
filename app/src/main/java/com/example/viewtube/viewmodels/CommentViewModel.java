package com.example.viewtube.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.viewtube.entities.Comment;
import com.example.viewtube.repositories.CommentRepository;

import java.util.List;

public class CommentViewModel extends AndroidViewModel {
    private CommentRepository commentRepository;
    private LiveData<List<Comment>> commentsLiveData;
    private LiveData<Comment> createdCommentLiveData;
    private LiveData<Comment> updatedCommentLiveData;
    private LiveData<Boolean> deleteCommentLiveData;

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository();
        commentsLiveData = commentRepository.getCommentsLiveData();
        createdCommentLiveData = commentRepository.getCreatedCommentLiveData();
        updatedCommentLiveData = commentRepository.getUpdatedCommentLiveData();
        deleteCommentLiveData = commentRepository.getDeleteCommentLiveData();
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
        commentRepository.createComment(comment);
    }

    public void getCommentsByVideoId(int videoId) {
        commentRepository.getCommentsByVideoId(videoId);
    }

    public void updateComment(Comment comment) {
        commentRepository.updateComment(comment);
    }

    public void deleteComment(int commentId, int videoId) {
        commentRepository.deleteComment(commentId, videoId);
    }
}
