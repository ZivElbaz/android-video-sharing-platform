package com.example.viewtube.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.R;
import com.example.viewtube.adapters.VideoList;
import com.example.viewtube.entities.Comment;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.CommentsManager;
import com.example.viewtube.managers.VideoDetailsManager;
import com.example.viewtube.managers.VideoPlayerManager;
import com.example.viewtube.viewmodels.CommentViewModel;
import com.example.viewtube.viewmodels.UserViewModel;
import com.example.viewtube.viewmodels.VideosViewModel;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class VideoWatchActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    private VideoPlayerManager videoPlayerManager;
    private VideoDetailsManager videoDetailsManager;
    private CommentsManager commentsManager;
    private Uri videoUri;
    private int videoIdentifier;
    private boolean liked = false;
    private Button btnLike;
    private VideosViewModel videosViewModel;
    private UserViewModel userViewModel;
    private CommentViewModel commentViewModel;
    private String user;
    private VideoItem currentVideo;
    private String currentVideoUploaderFirstName;
    private String currentVideoUploaderLastName;
    private boolean synced;
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveTitleButton;
    private Button saveDescriptionButton;
    private Button cancelTitleButton;
    private Button cancelDescriptionButton;

    private VideoList relatedVideosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        // Retrieve the video ID from the intent
        videoIdentifier = getIntent().getIntExtra("video_id", -1);

        // Initialize views
        PlayerView playerView = findViewById(R.id.videoPlayer);
        ImageButton playPauseButton = findViewById(R.id.exo_play_pause);
        ImageButton rewindButton = findViewById(R.id.exo_rew);
        ImageButton forwardButton = findViewById(R.id.exo_ffwd);
        ImageButton fullscreenButton = findViewById(R.id.exo_fullscreen);
        TextView exoPosition = findViewById(R.id.exo_position);
        TextView exoDuration = findViewById(R.id.exo_duration);
        TextView videoTitle = findViewById(R.id.videoTitle);
        TextView videoDescription = findViewById(R.id.videoDescription);
        TextView videoViews = findViewById(R.id.videoViews);
        TextView videoDate = findViewById(R.id.videoDate);
        ImageView uploaderProfilePic = findViewById(R.id.uploaderProfilePic);
        TextView uploaderName = findViewById(R.id.uploaderName);
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        EditText commentInput = findViewById(R.id.commentInput);
        btnLike = findViewById(R.id.btnLike);
        Button btnPostComment = findViewById(R.id.btnPostComment);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnDownload = findViewById(R.id.btnDownload);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton editTitleButton = findViewById(R.id.editTitleButton);
        ImageButton editDescriptionButton = findViewById(R.id.editDescriptionButton);
        editTitleEditText = findViewById(R.id.editTitleEditText);
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText);
        saveTitleButton = findViewById(R.id.saveTitleButton);
        saveDescriptionButton = findViewById(R.id.saveDescriptionButton);
        cancelTitleButton = findViewById(R.id.cancelTitleButton);
        cancelDescriptionButton = findViewById(R.id.cancelDescriptionButton);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Initialize ViewModel
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        // Observe the selected video item
        videosViewModel.getSelectedVideoItemLiveData().observe(this, videoItem -> {
            if (videoItem != null) {
                updateVideoDetails(videoItem);
                currentVideo = videoItem;

                userViewModel.getUserData(videoItem.getUploader()).observe(this, uploader -> {
                    if(uploader != null) {
                        currentVideoUploaderFirstName = uploader.getFirstName();
                        currentVideoUploaderLastName= uploader.getLastName();
                        synced = true;

                        if (this.user.equals(videoItem.getUploader())) {
                            // Show edit and delete buttons
                            editTitleButton.setVisibility(View.VISIBLE);
                            editDescriptionButton.setVisibility(View.VISIBLE);
                            btnDelete.setVisibility(View.VISIBLE);
                        } else {
                            // Hide edit and delete buttons
                            editTitleButton.setVisibility(View.GONE);
                            editDescriptionButton.setVisibility(View.GONE);
                            btnDelete.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // Fetch the video details if the videoIdentifier is valid and observe the changes
        if (videoIdentifier != -1) {
            videosViewModel.incrementViewCount(videoIdentifier);
            videosViewModel.fetchSelectedVideoItem(videoIdentifier);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        // Get current user
        if (username != null) {
            this.user = username;
        } else {
            this.user = "Guest";
        }

        // Initialize managers
        videoPlayerManager = new VideoPlayerManager(playerView, playPauseButton, rewindButton, forwardButton, fullscreenButton, exoPosition, exoDuration);
        videoDetailsManager = new VideoDetailsManager(this, videoTitle, videoDescription, videoDate, videoViews, btnLike, uploaderName, uploaderProfilePic);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsManager = new CommentsManager(commentViewModel, commentsRecyclerView, user);

        // Initialize related videos list
        relatedVideosList = new VideoList(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(relatedVideosList);
        videosViewModel.getVideoItemsLiveData().observe(this, videoItems -> relatedVideosList.setVideoItems(videoItems));

        // Fetch and observe comments for the video
        commentViewModel.getCommentsByVideoId(videoIdentifier);

        // Show edit buttons if the current user is the author
        if (this.user.equals(uploaderName.getText().toString())) {
            editTitleButton.setVisibility(View.VISIBLE);
            editDescriptionButton.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        // Set delete button listener
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Video")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        videosViewModel.deleteVideoItem(videoIdentifier, user);
                        Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set up click listeners for the edit buttons
        editTitleButton.setOnClickListener(v -> {
            videoTitle.setVisibility(View.GONE);
            editTitleEditText.setText(videoTitle.getText());
            editTitleEditText.setVisibility(View.VISIBLE);
            saveTitleButton.setVisibility(View.VISIBLE);
            cancelTitleButton.setVisibility(View.VISIBLE);
            editTitleButton.setVisibility(View.GONE);

            saveTitleButton.setOnClickListener(saveView -> {
                videoTitle.setText(editTitleEditText.getText());
                videoTitle.setVisibility(View.VISIBLE);
                editTitleEditText.setVisibility(View.GONE);
                saveTitleButton.setVisibility(View.GONE);
                cancelTitleButton.setVisibility(View.GONE);
                editTitleButton.setVisibility(View.VISIBLE);

                videosViewModel.updateVideoItemTitle(videoIdentifier, user, editTitleEditText.getText().toString());
                videosViewModel.reload();

                Toast.makeText(this, "Title updated", Toast.LENGTH_SHORT).show();
            });

            cancelTitleButton.setOnClickListener(cancelView -> {
                videoTitle.setVisibility(View.VISIBLE);
                editTitleEditText.setVisibility(View.GONE);
                saveTitleButton.setVisibility(View.GONE);
                cancelTitleButton.setVisibility(View.GONE);
                editTitleButton.setVisibility(View.VISIBLE);
            });
        });

        editDescriptionButton.setOnClickListener(v -> {
            videoDescription.setVisibility(View.GONE);
            editDescriptionEditText.setText(videoDescription.getText());
            editDescriptionEditText.setVisibility(View.VISIBLE);
            saveDescriptionButton.setVisibility(View.VISIBLE);
            cancelDescriptionButton.setVisibility(View.VISIBLE);
            editDescriptionButton.setVisibility(View.GONE);

            saveDescriptionButton.setOnClickListener(saveView -> {
                videoDescription.setText(editDescriptionEditText.getText());
                videoDescription.setVisibility(View.VISIBLE);
                editDescriptionEditText.setVisibility(View.GONE);
                saveDescriptionButton.setVisibility(View.GONE);
                cancelDescriptionButton.setVisibility(View.GONE);
                editDescriptionButton.setVisibility(View.VISIBLE);

                videosViewModel.updateVideoItemDescription(videoIdentifier, user, editDescriptionEditText.getText().toString());
                videosViewModel.reload();

                Toast.makeText(this, "Description updated", Toast.LENGTH_SHORT).show();
            });

            cancelDescriptionButton.setOnClickListener(cancelView -> {
                videoDescription.setVisibility(View.VISIBLE);
                editDescriptionEditText.setVisibility(View.GONE);
                saveDescriptionButton.setVisibility(View.GONE);
                cancelDescriptionButton.setVisibility(View.GONE);
                editDescriptionButton.setVisibility(View.VISIBLE);
            });
        });

        btnLike.setOnClickListener(view -> {
            if (currentVideo != null) {
                // Check if the user is a guest
                if (user.equals("Guest")) {
                    Toast.makeText(this, "Guest users cannot like videos.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the user has already liked the video
                    List<String> likedBy = currentVideo.getLikedBy();
                    if (likedBy.contains(user)) {
                        // User already liked the video, so remove their like
                        likedBy.remove(user);
                        currentVideo.setLikedBy(likedBy);
                        currentVideo.setLikes(currentVideo.getLikes() - 1);
                        liked = false;
                    } else {
                        // User hasn't liked the video yet, so add their like
                        likedBy.add(user);
                        currentVideo.setLikedBy(likedBy);
                        currentVideo.setLikes(currentVideo.getLikes() + 1);
                        liked = true;
                    }

                    // Update the video details in the local database
                    videosViewModel.updateVideo(currentVideo);

                    videosViewModel.userLiked(currentVideo.getId(), user);
                    videoDetailsManager.setVideoDetails(currentVideo);

                    // Update the UI
                    btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);
                }
            }
        });

        // Post a comment
        btnPostComment.setOnClickListener(view -> {
            if (user.equals("Guest")) {
                Toast.makeText(this, "Guest users cannot comment videos.", Toast.LENGTH_SHORT).show();
            } else {
                String commentText = commentInput.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    Comment newComment = new Comment(); // Create a new Comment object
                    newComment.setText(commentText);
                    newComment.setUploader(user); // Set the user as the uploader
                    newComment.setVideoId(videoIdentifier);
                    commentViewModel.createComment(newComment); // Send the comment to the server
                    commentsManager.addCommentToAdapter(newComment);
                    commentInput.setText(""); // Clear the input field after posting
                }
            }
        });

        btnDownload.setOnClickListener(view -> {
            Toast.makeText(this, "Video has been downloaded", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(view -> {
            String videoUrl = currentVideo.getVideoUrl();
            String shareText = "Check out this video: " + videoUrl;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share video via"));
        });


        uploaderProfilePic.setOnClickListener(view -> {
            if(synced) {
                Intent profileIntent = new Intent(VideoWatchActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("username", currentVideo.getUploader());
                profileIntent.putExtra("firstName", currentVideoUploaderFirstName);
                profileIntent.putExtra("lastName", currentVideoUploaderLastName);
                profileIntent.putExtra("profilePic", currentVideo.getProfilePicture());
                startActivity(profileIntent);
            }
        });

        uploaderName.setOnClickListener(view -> {
            if(synced) {
                Intent profileIntent = new Intent(VideoWatchActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("username", currentVideo.getUploader());
                profileIntent.putExtra("firstName", currentVideoUploaderFirstName);
                profileIntent.putExtra("lastName", currentVideoUploaderLastName);
                profileIntent.putExtra("profilePic", currentVideo.getProfilePicture());
                startActivity(profileIntent);
            }
        });
    }

    // Initialize the video player
    private void initializePlayer() {
        // Release the player if it's already initialized
        if (videoPlayerManager != null) {
            videoPlayerManager.releasePlayer();
        }
        // Check if video URI is valid, then initialize the player
        if (videoUri != null) {
            videoPlayerManager.initializePlayer(this, videoUri);
        } else {
            Log.e("VideoWatchActivity", "videoUri is null, cannot initialize player");
        }
    }

    // Update video details such as title, description, and play the video
    private void updateVideoDetails(VideoItem videoItem) {
        // Set the video details (title, description, etc.) using the VideoDetailsManager
        videoDetailsManager.setVideoDetails(videoItem);
        // Parse the video URL and prepare the player
        videoUri = Uri.parse(videoItem.getVideoUrl());
        initializePlayer();

        // If the user is logged in, check if they have already liked the video
        if (user != null && !user.equals("Guest")) {
            List<String> likedBy = videoItem.getLikedBy();
            liked = likedBy.contains(user); // Check if the current user has liked the video
            // Update the like button appearance based on the like status
            btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);
        } else {
            // If the user is a guest, reset the like button to its default state
            liked = false;
            btnLike.setTextColor(ContextCompat.getColor(this, R.color.black));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
        }
    }

    // Pause the video player when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        videoPlayerManager.releasePlayer();
    }

    // Release the video player when the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        videoPlayerManager.releasePlayer();
    }

    // Re-initialize the video player when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    // Handle video item click events
    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        synced = false; // Reset sync status
        // Set the selected video in the ViewModel
        videosViewModel.setSelectedVideoItem(videoItem);

        // Clear the current comments from the comments manager
        commentsManager.clearComments();

        // Fetch and display comments for the selected video
        commentViewModel.getCommentsByVideoId(videoItem.getId());

        // Scroll to the top of the page when a new video is selected
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, 0);
    }

}
