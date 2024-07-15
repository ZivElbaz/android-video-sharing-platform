package com.example.viewtube.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.AppDB;
import com.example.viewtube.data.CommentDao;
import com.example.viewtube.api.CommentAPI;
import com.example.viewtube.entities.Comment;

import java.util.List;

public class CommentRepository {
    private CommentDao commentDao;
    private CommentAPI commentAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private MutableLiveData<Comment> createdCommentLiveData;
    private MutableLiveData<Boolean> deleteCommentLiveData;

    public CommentRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        commentDao = db.commentDao();
        commentAPI = new CommentAPI(commentDao, context);
        commentsLiveData = new MutableLiveData<>();
        createdCommentLiveData = new MutableLiveData<>();
        deleteCommentLiveData = new MutableLiveData<>();
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
        commentAPI.createComment(comment, createdCommentLiveData);
        createdCommentLiveData.observeForever(createdComment -> {
            if (createdComment != null) {
                new Thread(() -> commentDao.insert(createdComment)).start();
            }
        });
    }

    public void getCommentsByVideoId(int videoId) {
        commentAPI.getCommentsByVideoId(videoId, commentsLiveData);
        commentsLiveData.observeForever(comments -> {
            if (comments != null && !comments.isEmpty()) {
                new Thread(() -> {
                    commentDao.clear();
                    commentDao.insert(comments.toArray(new Comment[0]));
                }).start();
            }
        });
    }

    public void deleteComment(int commentId) {
        commentAPI.deleteComment(commentId, deleteCommentLiveData);
        deleteCommentLiveData.observeForever(deleted -> {
            if (deleted != null && deleted) {
                new Thread(() -> commentDao.deleteById(commentId)).start();
            }
        });
    }
}
