package com.example.viewtube.managers;

import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.CommentsAdapter;

import java.util.List;

public class CommentsManager {

    private List<String> comments;
    private EditText commentInput;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;

    public CommentsManager(List<String> comments, EditText commentInput, RecyclerView commentsRecyclerView) {
        this.comments = comments;
        this.commentInput = commentInput;
        this.commentsRecyclerView = commentsRecyclerView;
        initializeComments();
    }

    private void initializeComments() {
        commentsAdapter = new CommentsAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(commentsRecyclerView.getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    public void addComment(String commentText) {
        if (!commentText.isEmpty()) {
            comments.add(commentText);
            commentsAdapter.notifyItemInserted(comments.size() - 1);
            commentInput.setText("");
        } else {
            Toast.makeText(commentInput.getContext(), "Enter a comment", Toast.LENGTH_SHORT).show();
        }
    }
}