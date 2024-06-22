package com.example.viewtube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.managers.SessionManager;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private final List<SessionManager.Comment> comments;

    private final String currentUsername;

    private final OnCommentActionListener commentActionListener;

    // Constructor to initialize the comments list
    public CommentsAdapter(List<SessionManager.Comment> comments, String currentUsername, OnCommentActionListener commentActionListener) {
        this.comments = comments;
        this.currentUsername = currentUsername;
        this.commentActionListener = commentActionListener;
    }

    // Interface for handling comment actions
    public interface OnCommentActionListener {
        void onEditComment(int position, String newCommentText);
        void onDeleteComment(int position);
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for comments
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    // Called by RecyclerView to display the data at the specified position
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        // Get the comment for the current position
        SessionManager.Comment comment = comments.get(position);
        // Bind the comment data to the TextView
        String fullCommentText = comment.getUsername() + ": " + comment.getComment();
        holder.commentTextView.setText(fullCommentText);


        // Check if the current user is the author of the comment
        if (comment.getUsername().equals(currentUsername)) {
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editCommentButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Handle the edit button click
        holder.editCommentButton.setOnClickListener(v -> {
            holder.commentTextView.setVisibility(View.GONE);
            holder.editCommentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setText(comment.getComment());
            holder.saveButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.GONE);
        });

        // Handle the save button click for editing the comment
        holder.saveButton.setOnClickListener(v -> {
            String newCommentText = holder.editCommentTextView.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setComment(newCommentText);
                holder.commentTextView.setText(comment.getUsername() + ": " + newCommentText);
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.editCommentTextView.setVisibility(View.GONE);
                holder.saveButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                commentActionListener.onEditComment(holder.getAdapterPosition(), newCommentText);
            }
        });

        // Handle the cancel button click
        holder.cancelButton.setOnClickListener(v -> {
            holder.commentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setVisibility(View.GONE);
            holder.saveButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        });

        // Handle the delete button click
        holder.deleteButton.setOnClickListener(v -> commentActionListener.onDeleteComment(holder.getAdapterPosition()));
    }


    // Returns the total number of items in the comments list
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // ViewHolder class that holds references to each item view
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        EditText editCommentTextView;
        ImageButton editCommentButton;
        Button deleteButton;
        Button saveButton;
        Button cancelButton;

        // Constructor to initialize the item views
        public CommentViewHolder(View itemView) {
            super(itemView);
            // Find the views
            commentTextView = itemView.findViewById(R.id.commentTextView);
            editCommentTextView = itemView.findViewById(R.id.editCommentTextView);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
