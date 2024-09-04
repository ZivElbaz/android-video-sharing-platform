package com.example.viewtube.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.R;
import com.example.viewtube.entities.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> comments; // List of comments to display
    private final String currentUsername; // Current logged-in user's username
    private final OnCommentActionListener commentActionListener; // Listener for comment actions (edit, delete)

    // Constructor for the adapter, initializing the list of comments, current user's username, and action listener
    public CommentsAdapter(List<Comment> comments, String currentUsername, OnCommentActionListener commentActionListener) {
        this.comments = comments;
        this.currentUsername = currentUsername;
        this.commentActionListener = commentActionListener;
    }

    // Interface for handling edit and delete comment actions
    public interface OnCommentActionListener {
        void onEditComment(int position, String newCommentText); // Edit action
        void onDeleteComment(int position); // Delete action
    }

    // Set the list of comments and refresh the UI
    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    // Get the current list of comments
    public List<Comment> getComments() {
        return comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the comment item layout and create a new view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        // Get the comment at the current position
        Comment comment = comments.get(position);
        String fullCommentText = comment.getUploader() + ": " + comment.getText();
        holder.commentTextView.setText(fullCommentText); // Set comment text with the uploader's name

        // Show edit and delete buttons only if the comment was posted by the current user
        if (comment.getUploader().equals(currentUsername)) {
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editCommentButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Handle edit comment button click
        holder.editCommentButton.setOnClickListener(v -> {
            // Hide the comment text view and show the edit comment text box with save and cancel buttons
            holder.commentTextView.setVisibility(View.GONE);
            holder.editCommentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setText(comment.getText());
            holder.saveButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.GONE);
        });

        // Handle save button click after editing the comment
        holder.saveButton.setOnClickListener(v -> {
            String newCommentText = holder.editCommentTextView.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setText(newCommentText); // Update the comment text
                holder.commentTextView.setText(comment.getUploader() + ": " + newCommentText); // Update the comment view
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.editCommentTextView.setVisibility(View.GONE);
                holder.saveButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                commentActionListener.onEditComment(holder.getAdapterPosition(), newCommentText); // Notify listener of the edit action
            }
        });

        // Handle cancel button click when editing the comment
        holder.cancelButton.setOnClickListener(v -> {
            // Hide the edit text box and show the comment text view
            holder.commentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setVisibility(View.GONE);
            holder.saveButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        });

        // Handle delete button click for deleting the comment
        holder.deleteButton.setOnClickListener(v -> commentActionListener.onDeleteComment(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0; // Return the number of comments
    }

    // View holder class for the comment item layout
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView; // Text view to display the comment
        EditText editCommentTextView; // Edit text view for editing the comment
        ImageButton editCommentButton; // Button for editing the comment
        Button deleteButton; // Button for deleting the comment
        Button saveButton; // Button for saving the edited comment
        Button cancelButton; // Button for canceling the edit action

        public CommentViewHolder(View itemView) {
            super(itemView);
            // Initialize all the UI components from the layout
            commentTextView = itemView.findViewById(R.id.commentTextView);
            editCommentTextView = itemView.findViewById(R.id.editCommentTextView);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
