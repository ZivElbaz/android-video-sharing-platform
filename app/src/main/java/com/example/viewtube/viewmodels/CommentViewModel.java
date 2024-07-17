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
    private LiveData<Boolean> deleteCommentLiveData;

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository(application);
        commentsLiveData = commentRepository.getCommentsLiveData();
        createdCommentLiveData = commentRepository.getCreatedCommentLiveData();
        deleteCommentLiveData = commentRepository.getDeleteCommentLiveData();
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public LiveData<Comment> getCreatedCommentLiveData() {
        return createdCommentLiveData;
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

    public void deleteComment(int commentId) {
        commentRepository.deleteComment(commentId);
    }
}
