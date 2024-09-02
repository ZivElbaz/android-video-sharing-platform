package com.example.viewtube.managers;

import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.adapters.CommentsAdapter;
import com.example.viewtube.entities.Comment;
import com.example.viewtube.viewmodels.CommentViewModel;

import java.util.List;

public class CommentsManager implements CommentsAdapter.OnCommentActionListener {

    private CommentViewModel commentViewModel;
    private CommentsAdapter commentsAdapter;

    public CommentsManager(CommentViewModel commentViewModel, RecyclerView commentsRecyclerView, String username) {
        this.commentViewModel = commentViewModel;
        commentsAdapter = new CommentsAdapter(null, username, this);
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Observe the comments from the ViewModel and update the adapter
        commentViewModel.getCommentsLiveData().observeForever(comments -> {
            commentsAdapter.setComments(comments);
        });
    }

    public void addCommentToAdapter(Comment comment) {
        List<Comment> comments = commentsAdapter.getComments();
        comments.add(comment);
        commentsAdapter.notifyItemInserted(comments.size() - 1);
    }

    @Override
    public void onEditComment(int position, String newCommentText) {
        Comment comment = commentsAdapter.getComments().get(position);
        comment.setText(newCommentText);
        commentViewModel.updateComment(comment);
    }

    @Override
    public void onDeleteComment(int position) {
        Comment comment = commentsAdapter.getComments().get(position);
        List<Comment> comments = commentsAdapter.getComments();
        commentViewModel.deleteComment(comment.getId(), comment.getVideoId());
        comments.remove(comment);
        commentsAdapter.notifyItemRemoved(position);
    }

}
