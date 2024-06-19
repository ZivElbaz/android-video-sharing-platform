package com.example.viewtube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<String> comments;

    // Constructor to initialize the comments list
    public CommentsAdapter(List<String> comments) {
        this.comments = comments;
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
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
        String comment = comments.get(position);
        // Bind the comment data to the TextView
        holder.commentTextView.setText(comment);
    }

    // Returns the total number of items in the comments list
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // ViewHolder class that holds references to each item view
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;

        // Constructor to initialize the item views
        public CommentViewHolder(View itemView) {
            super(itemView);
            // Find the TextView for the comment
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
