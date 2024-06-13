package com.example.viewtube;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.managers.CommentsManager;
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
    private String videoFileName;
    private boolean liked = false;
    private RecyclerView recyclerView;
    private VideoList relatedVideos;
    private ArrayList<VideoItem> mergedList;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        // Initialize views
        PlayerView playerView = findViewById(R.id.videoPlayer);
        TextView videoTitle = findViewById(R.id.videoTitle);
        TextView videoDescription = findViewById(R.id.videoDescription);
        TextView videoViews = findViewById(R.id.videoViews);
        TextView videoDate = findViewById(R.id.videoDate);
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

        // Initialize managers
        videoPlayerManager = new VideoPlayerManager(playerView, playPauseButton, rewindButton, forwardButton, fullscreenButton);
        videoDetailsManager = new VideoDetailsManager(videoTitle, videoDescription, videoDate, videoViews, btnLike);
        commentsManager = new CommentsManager(SessionManager.getInstance().getComments(videoFileName), commentInput, commentsRecyclerView);

        // Get video details from intent
        String videoResourceName = getIntent().getStringExtra("video_resource_name");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");
        String date = getIntent().getStringExtra("video_date");
        int initialLikes = getIntent().getIntExtra("video_likes", 1);
        int views = getIntent().getIntExtra("video_views", 1);
        int id = getIntent().getIntExtra("video_id", 1);

        // Check if session has likes and liked status
        int likes = SessionManager.getInstance().getLikes(videoResourceName) == 0 ? initialLikes : SessionManager.getInstance().getLikes(videoResourceName);
        liked = SessionManager.getInstance().isLiked(videoResourceName);

        // Set video details
        videoDetailsManager.setVideoDetails(title, description, date, views, likes);
        btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
        btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);

        Log.d("VideoWatchActivity", "Video re name: " + videoResourceName + " and video id: " + id);

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
        videoFileName = videoResourceName + ".mp4";
        videoPlayerManager.initializePlayer(this, videoUri);

//

        // Set button listeners
        btnLike.setOnClickListener(view -> {
            int newLikes = liked ? likes : likes + 1;
            liked = !liked;
            btnLike.setTextColor(ContextCompat.getColor(this, liked ? R.color.red : R.color.black));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(liked ? R.drawable.liked : R.drawable.like, 0, 0, 0);
            SessionManager.getInstance().setLikes(videoResourceName, newLikes);
            SessionManager.getInstance().setLiked(videoResourceName, liked);
            videoDetailsManager.setVideoDetails(title, description, date, views, newLikes);
        });


        btnPostComment.setOnClickListener(view -> {
            String commentText = commentInput.getText().toString().trim();
            commentsManager.addComment(commentText);
            SessionManager.getInstance().addComment(videoResourceName, commentText);
        });

        btnDownload.setOnClickListener(view -> {
            FileUtils.checkAndRequestPermission(this);
        });

      //  btnShare.setOnClickListener(view -> FileUtils.shareVideo(this, videoResourceId, videoFileName, title));

        // Initialize the RecyclerView with related videos
        relatedVideos = new VideoList(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(relatedVideos);


        ArrayList<VideoItem> videoItems = getIntent().getParcelableArrayListExtra("video_items");
        relatedVideos.setVideoItems(videoItems);
        mergedList = videoItems;

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
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        int videoId = videoItem.getId();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putParcelableArrayListExtra("video_items", mergedList);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        moveToWatch.putExtra("video_id", videoId);
        startActivity(moveToWatch);
    }
}