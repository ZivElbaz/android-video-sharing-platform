package com.example.viewtube.managers;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.CommentsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentsManager implements CommentsAdapter.OnCommentActionListener {

    private Map<Integer, List<SessionManager.Comment>> videoCommentsMap;
    private int videoId;

    private String username;
    private EditText commentInput;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;

    // Constructor to initialize the CommentsManager with video ID, comments map, comment input field, and comments RecyclerView
    public CommentsManager(int videoId, Map<Integer, List<SessionManager.Comment>> videoCommentsMap, EditText commentInput, RecyclerView commentsRecyclerView, String username) {
        this.videoId = videoId;
        this.videoCommentsMap = videoCommentsMap;
        this.commentInput = commentInput;
        this.commentsRecyclerView = commentsRecyclerView;
        this.username = username;
        initializeComments();
    }

    // Method to initialize comments for the current video
    private void initializeComments() {
        List<SessionManager.Comment> comments = videoCommentsMap.get(videoId);
        if (comments == null) {
            comments = new ArrayList<SessionManager.Comment>();
            videoCommentsMap.put(videoId, comments);
        }
        commentsAdapter = new CommentsAdapter(comments, username, this);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(commentsRecyclerView.getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    // Method to add a comment to the current video's comments list
    public void addComment(String commentText, String username) {
        if (!commentText.isEmpty()) {
            List<SessionManager.Comment> comments = videoCommentsMap.get(videoId);
            if (comments == null) {
                comments = new ArrayList<SessionManager.Comment>();
                videoCommentsMap.put(videoId, comments);
            }

            comments.add(new SessionManager.Comment(username, commentText));
            commentsAdapter.notifyItemInserted(comments.size() - 1);
            commentInput.setText("");

        } else {
            Toast.makeText(commentInput.getContext(), "Enter a comment", Toast.LENGTH_SHORT).show();
        }
    }

    public void editComment(int position, String newCommentText) {
        List<SessionManager.Comment> comments = videoCommentsMap.get(videoId);
        if (comments != null && position < comments.size()) {
            SessionManager.Comment comment = comments.get(position);
            comment.setComment(newCommentText);
            commentsAdapter.notifyItemChanged(position);
        } else {
            Toast.makeText(commentInput.getContext(), "Comment not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteComment(int position) {
        List<SessionManager.Comment> comments = videoCommentsMap.get(videoId);
        if (comments != null && position < comments.size()) {
            comments.remove(position);
            commentsAdapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(commentInput.getContext(), "Comment not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditComment(int position, String newCommentText) {
        editComment(position, newCommentText);
    }

    @Override
    public void onDeleteComment(int position) {
        deleteComment(position);
    }


}
