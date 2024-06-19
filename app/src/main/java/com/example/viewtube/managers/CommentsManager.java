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

public class CommentsManager {

    private Map<Integer, List<String>> videoCommentsMap;
    private int videoId;
    private EditText commentInput;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;

    // Constructor to initialize the CommentsManager with video ID, comments map, comment input field, and comments RecyclerView
    public CommentsManager(int videoId, Map<Integer, List<String>> videoCommentsMap, EditText commentInput, RecyclerView commentsRecyclerView) {
        this.videoId = videoId;
        this.videoCommentsMap = videoCommentsMap;
        this.commentInput = commentInput;
        this.commentsRecyclerView = commentsRecyclerView;
        initializeComments();
    }

    // Method to initialize comments for the current video
    private void initializeComments() {
        List<String> comments = videoCommentsMap.get(videoId);
        if (comments == null) {
            comments = new ArrayList<>();
            videoCommentsMap.put(videoId, comments);
        }
        commentsAdapter = new CommentsAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(commentsRecyclerView.getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    // Method to add a comment to the current video's comments list
    public void addComment(String commentText) {
        if (!commentText.isEmpty()) {
            List<String> comments = videoCommentsMap.get(videoId);
            if (comments == null) {
                comments = new ArrayList<>();
                videoCommentsMap.put(videoId, comments);
            }
            comments.add(commentText);
            commentsAdapter.notifyItemInserted(comments.size() - 1);
            commentInput.setText("");

        } else {
            Toast.makeText(commentInput.getContext(), "Enter a comment", Toast.LENGTH_SHORT).show();
        }
    }
}
