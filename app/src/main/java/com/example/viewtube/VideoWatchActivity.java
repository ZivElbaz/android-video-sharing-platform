package com.example.viewtube;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.managers.CommentsManager;
import com.example.viewtube.managers.CurrentUserManager;
import com.example.viewtube.managers.FileUtils;
import com.example.viewtube.managers.SessionManager;
import com.example.viewtube.managers.VideoDetailsManager;
import com.example.viewtube.managers.VideoPlayerManager;
import com.google.android.exoplayer2.ui.PlayerView;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoWatchActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    private VideoPlayerManager videoPlayerManager;
    private VideoDetailsManager videoDetailsManager;
    private CommentsManager commentsManager;

    private Uri videoUri;

    private int videoIdentifier;
    private boolean liked = false;
    private RecyclerView recyclerView;
    private VideoList relatedVideos;
    private ArrayList<VideoItem> mergedList;

    private VideoViewModel videoViewModel;

    private String user;

    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveTitleButton;
    private Button saveDescriptionButton;
    private Button cancelTitleButton;
    private Button cancelDescriptionButton;


    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        // Initialize ViewModel
        videoViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(VideoViewModel.class);
        mergedList = new ArrayList<>(videoViewModel.getVideoItems().getValue());


        // Initialize views
        PlayerView playerView = findViewById(R.id.videoPlayer);
        TextView videoTitle = findViewById(R.id.videoTitle);
        TextView videoDescription = findViewById(R.id.videoDescription);
        TextView videoViews = findViewById(R.id.videoViews);
        TextView videoDate = findViewById(R.id.videoDate);
        ImageView uploaderProfilePic = findViewById(R.id.uploaderProfilePic);
        TextView uploaderName = findViewById(R.id.uploaderName);
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        EditText commentInput = findViewById(R.id.commentInput);
        Button btnLike = findViewById(R.id.btnLike);
        Button btnPostComment = findViewById(R.id.btnPostComment);
        ImageButton playPauseButton = findViewById(R.id.exo_play_pause);
        ImageButton rewindButton = findViewById(R.id.exo_rew);
        ImageButton forwardButton = findViewById(R.id.exo_ffwd);
        ImageButton fullscreenButton = findViewById(R.id.exo_fullscreen);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnDownload = findViewById(R.id.btnDownload);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize uploader edit section views
        ImageButton editTitleButton = findViewById(R.id.editTitleButton);
        ImageButton editDescriptionButton = findViewById(R.id.editDescriptionButton);
        EditText editTitleEditText = findViewById(R.id.editTitleEditText);
        EditText editDescriptionEditText = findViewById(R.id.editDescriptionEditText);
        Button saveTitleButton = findViewById(R.id.saveTitleButton);
        Button saveDescriptionButton = findViewById(R.id.saveDescriptionButton);
        Button cancelTitleButton = findViewById(R.id.cancelTitleButton);
        Button cancelDescriptionButton = findViewById(R.id.cancelDescriptionButton);
        Button btnDelete = findViewById(R.id.btnDelete);


        if (CurrentUserManager.getInstance().getCurrentUser() == null) {
            this.user = "Guest";
        } else {
            this.user = CurrentUserManager.getInstance().getCurrentUser().getUsername();
        }

        // Initialize managers
        videoPlayerManager = new VideoPlayerManager(playerView, playPauseButton, rewindButton, forwardButton, fullscreenButton);
        videoDetailsManager = new VideoDetailsManager(this, videoTitle, videoDescription, videoDate, videoViews, btnLike, uploaderName, uploaderProfilePic);



        // Get video details from intent
        String videoResourceName = getIntent().getStringExtra("video_resource_name");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");
        String date = getIntent().getStringExtra("video_date");
        String author = getIntent().getStringExtra("video_author");
        int initialLikes = getIntent().getIntExtra("video_likes", 1);
        int views = getIntent().getIntExtra("video_views", 1);
        int id = getIntent().getIntExtra("video_id", 1);
        this.videoIdentifier = id;



        commentsRecyclerView.setAdapter(null);
        commentsManager = new CommentsManager(videoIdentifier, SessionManager.getInstance().getVideoCommentsMap(), commentInput, commentsRecyclerView);
        // Check if session has likes and liked status
        int likes = SessionManager.getInstance().getLikes(videoResourceName) == 0 ? initialLikes : SessionManager.getInstance().getLikes(videoResourceName);
        liked = SessionManager.getInstance().isLiked(videoResourceName);


        // Set video details
        videoDetailsManager.setVideoDetails(title, description, date, views, likes, author);
        videoDetailsManager.setUploaderImage(id);
        btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
        btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);

        // Show edit buttons if the current user is the author
        if (this.user.equals(author)) {
            editTitleButton.setVisibility(View.VISIBLE);
            editDescriptionButton.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Video")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        videoViewModel.removeVideoItem(videoIdentifier);
                        Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set up click listeners for the edit buttons
        editTitleButton.setOnClickListener(v -> {
            // Replace TextView with EditText for title
            videoTitle.setVisibility(View.GONE);
            editTitleEditText.setText(videoTitle.getText());
            editTitleEditText.setVisibility(View.VISIBLE);
            saveTitleButton.setVisibility(View.VISIBLE);
            cancelTitleButton.setVisibility(View.VISIBLE);
            editTitleButton.setVisibility(View.GONE);

            // Save button click listener
            saveTitleButton.setOnClickListener(saveView -> {
                videoTitle.setText(editTitleEditText.getText());
                videoTitle.setVisibility(View.VISIBLE);
                editTitleEditText.setVisibility(View.GONE);
                saveTitleButton.setVisibility(View.GONE);
                cancelTitleButton.setVisibility(View.GONE);
                editTitleButton.setVisibility(View.VISIBLE);

                //update the viewmodel list
                videoViewModel.updateVideoItemTitle(videoIdentifier, editTitleEditText.getText().toString());

                Toast.makeText(this, "Title updated", Toast.LENGTH_SHORT).show();
            });

            // Cancel button click listener
            cancelTitleButton.setOnClickListener(cancelView -> {
                videoTitle.setVisibility(View.VISIBLE);
                editTitleEditText.setVisibility(View.GONE);
                saveTitleButton.setVisibility(View.GONE);
                cancelTitleButton.setVisibility(View.GONE);
                editTitleButton.setVisibility(View.VISIBLE);
            });
        });

        editDescriptionButton.setOnClickListener(v -> {
            // Replace TextView with EditText for description
            videoDescription.setVisibility(View.GONE);
            editDescriptionEditText.setText(videoDescription.getText());
            editDescriptionEditText.setVisibility(View.VISIBLE);
            saveDescriptionButton.setVisibility(View.VISIBLE);
            cancelDescriptionButton.setVisibility(View.VISIBLE);
            editDescriptionButton.setVisibility(View.GONE);

            // Save button click listener
            saveDescriptionButton.setOnClickListener(saveView -> {
                videoDescription.setText(editDescriptionEditText.getText());
                videoDescription.setVisibility(View.VISIBLE);
                editDescriptionEditText.setVisibility(View.GONE);
                saveDescriptionButton.setVisibility(View.GONE);
                cancelDescriptionButton.setVisibility(View.GONE);
                editDescriptionButton.setVisibility(View.VISIBLE);

                //update the viewmodel list
                videoViewModel.updateVideoItemDescription(videoIdentifier, editDescriptionEditText.getText().toString());

                Toast.makeText(this, "Description updated", Toast.LENGTH_SHORT).show();

            });

            // Cancel button click listener
            cancelDescriptionButton.setOnClickListener(cancelView -> {
                videoDescription.setVisibility(View.VISIBLE);
                editDescriptionEditText.setVisibility(View.GONE);
                saveDescriptionButton.setVisibility(View.GONE);
                cancelDescriptionButton.setVisibility(View.GONE);
                editDescriptionButton.setVisibility(View.VISIBLE);
            });
        });

        if (id >= 1 && id <= 10) {
            // Local resource video
            int videoResourceId = getResources().getIdentifier(videoResourceName, "raw", getPackageName());
            videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);
        } else {
            // Newly added video
            File videoFile = new File(videoResourceName);
            videoUri = Uri.fromFile(videoFile);
        }

        Log.d("VideoWatchActivity", "Video URI: " + videoUri.toString() + " and video id: " + id);
        videoPlayerManager.initializePlayer(this, videoUri);



        // Set button listeners
        btnLike.setOnClickListener(view -> {
            int newLikes = liked ? likes : likes + 1;
            liked = !liked;
            btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);
            SessionManager.getInstance().setLikes(videoResourceName, newLikes);
            SessionManager.getInstance().setLiked(videoResourceName, liked);
            videoDetailsManager.setVideoDetails(title, description, date, views, newLikes, author);
        });


        btnPostComment.setOnClickListener(view -> {
            String commentText = user + ": " + commentInput.getText().toString().trim();
            commentsManager.addComment(commentText);
        });

        btnDownload.setOnClickListener(view -> {
            FileUtils.checkAndRequestPermission(this);
        });

        btnShare.setOnClickListener(view -> {
            Uri contentUri;
            if (id >= 1 && id <= 10) {
                // Local resource video
                int videoResourceId = getResources().getIdentifier(videoResourceName, "raw", getPackageName());
                contentUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);
            } else {
                // Newly added video
                contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(videoUri.getPath()));
            }
            FileUtils.shareVideo(this, contentUri, title);
        });

        // Initialize the RecyclerView with related videos
        relatedVideos = new VideoList(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(relatedVideos);

        relatedVideos.setVideoItems(mergedList);


    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayerManager.releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoPlayerManager.releasePlayer();
    }


    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        // Handle video item click
        String videoResourceName = videoItem.getVideoURL(); // Ensure this is the correct resource name
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        String author = videoItem.getAuthor();
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        int videoId = videoItem.getId();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        moveToWatch.putExtra("video_id", videoId);
        moveToWatch.putExtra("video_author", author);
        startActivity(moveToWatch);
    }
}