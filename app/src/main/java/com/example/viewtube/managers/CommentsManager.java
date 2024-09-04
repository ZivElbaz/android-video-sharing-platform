package com.example.viewtube.managers;

import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.adapters.CommentsAdapter;
import com.example.viewtube.entities.Comment;
import com.example.viewtube.viewmodels.CommentViewModel;

import java.util.ArrayList;
import java.util.List;

// Manages comments, interacts with ViewModel and RecyclerView
public class CommentsManager implements CommentsAdapter.OnCommentActionListener {

    private CommentViewModel commentViewModel; // ViewModel for handling comments
    private CommentsAdapter commentsAdapter; // Adapter for displaying comments in RecyclerView

    // Constructor initializes the manager with ViewModel, RecyclerView, and current username
    public CommentsManager(CommentViewModel commentViewModel, RecyclerView commentsRecyclerView, String username) {
        this.commentViewModel = commentViewModel;
        commentsAdapter = new CommentsAdapter(null, username, this); // Initialize adapter with null comments
        commentsRecyclerView.setAdapter(commentsAdapter); // Set adapter for the RecyclerView

        // Observe changes in comments LiveData and update the adapter
        commentViewModel.getCommentsLiveData().observeForever(comments -> {
            commentsAdapter.setComments(comments); // Update the adapter's comments list
        });
    }

    // Add a new comment to the adapter and notify the adapter of the change
    public void addCommentToAdapter(Comment comment) {
        List<Comment> comments = commentsAdapter.getComments();

        if (comments == null) {
            // If comments list is null, initialize it
            comments = new ArrayList<>();
            commentsAdapter.setComments(comments); // Set the initialized list in the adapter
        }

        comments.add(comment); // Add the new comment
        commentsAdapter.notifyItemInserted(comments.size() - 1); // Notify adapter of the inserted item
    }

    // Handle editing of a comment (Triggered by the adapter)
    @Override
    public void onEditComment(int position, String newCommentText) {
        Comment comment = commentsAdapter.getComments().get(position); // Get the comment at the specified position
        comment.setText(newCommentText); // Update comment text
        commentViewModel.updateComment(comment); // Update the comment in the ViewModel
    }

    // Handle deletion of a comment (Triggered by the adapter)
    @Override
    public void onDeleteComment(int position) {
        Comment comment = commentsAdapter.getComments().get(position); // Get the comment to delete
        List<Comment> comments = commentsAdapter.getComments();
        commentViewModel.deleteComment(comment.getId(), comment.getVideoId()); // Delete the comment via ViewModel
        comments.remove(comment); // Remove the comment from the list
        commentsAdapter.notifyItemRemoved(position); // Notify adapter that an item was removed
    }

    // Clear all comments from the adapter
    public void clearComments() {
        List<Comment> comments = commentsAdapter.getComments(); // Get the current comments
        comments.clear(); // Clear the comments list
        commentsAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}
