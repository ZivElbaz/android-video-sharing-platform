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
    private List<Comment> comments;
    private final String currentUsername;
    private final OnCommentActionListener commentActionListener;

    public CommentsAdapter(List<Comment> comments, String currentUsername, OnCommentActionListener commentActionListener) {
        this.comments = comments;
        this.currentUsername = currentUsername;
        this.commentActionListener = commentActionListener;
    }

    public interface OnCommentActionListener {
        void onEditComment(int position, String newCommentText);
        void onDeleteComment(int position);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getComments() {
        return comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        String fullCommentText = comment.getUploader() + ": " + comment.getText();
        holder.commentTextView.setText(fullCommentText);

        if (comment.getUploader().equals(currentUsername)) {
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editCommentButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.editCommentButton.setOnClickListener(v -> {
            holder.commentTextView.setVisibility(View.GONE);
            holder.editCommentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setText(comment.getText());
            holder.saveButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.GONE);
        });

        holder.saveButton.setOnClickListener(v -> {
            String newCommentText = holder.editCommentTextView.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setText(newCommentText);
                holder.commentTextView.setText(comment.getUploader() + ": " + newCommentText);
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.editCommentTextView.setVisibility(View.GONE);
                holder.saveButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                commentActionListener.onEditComment(holder.getAdapterPosition(), newCommentText);
            }
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.commentTextView.setVisibility(View.VISIBLE);
            holder.editCommentTextView.setVisibility(View.GONE);
            holder.saveButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        });

        holder.deleteButton.setOnClickListener(v -> commentActionListener.onDeleteComment(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        EditText editCommentTextView;
        ImageButton editCommentButton;
        Button deleteButton;
        Button saveButton;
        Button cancelButton;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            editCommentTextView = itemView.findViewById(R.id.editCommentTextView);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
