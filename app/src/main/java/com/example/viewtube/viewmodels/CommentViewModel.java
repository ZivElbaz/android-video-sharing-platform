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

    // Constructor initializes the repository and LiveData for comments
    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository();
        commentsLiveData = commentRepository.getCommentsLiveData();
    }

    // Returns LiveData for the list of comments
    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    // Creates a new comment by calling the repository method
    public void createComment(Comment comment) {
        commentRepository.createComment(comment);
    }

    // Fetches comments for a specific video ID by calling the repository method
    public void getCommentsByVideoId(int videoId) {
        commentRepository.getCommentsByVideoId(videoId);
    }

    // Updates an existing comment by calling the repository method
    public void updateComment(Comment comment) {
        commentRepository.updateComment(comment);
    }

    // Deletes a comment by its ID and the associated video ID by calling the repository method
    public void deleteComment(int commentId, int videoId) {
        commentRepository.deleteComment(commentId, videoId);
    }
}
