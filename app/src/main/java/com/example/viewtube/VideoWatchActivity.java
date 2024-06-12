package com.example.viewtube;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class VideoWatchActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    private TextView videoTitle;
    private TextView videoDescription, videoDate, videoViews;
    private RecyclerView recyclerView;
    private RecyclerView commentsRecyclerView;
   // private CommentsAdapter commentsAdapter;
    private VideoList relatedVideos;
    private List<String> comments;
    private EditText commentInput;
    private Button btnLike, btnShare, btnDownload, btnPostComment;
    private ImageButton playPauseButton, rewindButton, forwardButton, fullscreenButton;

    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        //initialize the views
        playerView = findViewById(R.id.videoPlayer);
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);
        videoViews = findViewById(R.id.videoViews);
        videoDate = findViewById(R.id.videoDate);
        recyclerView = findViewById(R.id.recyclerView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);
        btnLike = findViewById(R.id.btnLike);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        btnPostComment = findViewById(R.id.btnPostComment);
        playPauseButton = findViewById(R.id.exo_play_pause);
        rewindButton = findViewById(R.id.exo_rew);
        forwardButton = findViewById(R.id.exo_ffwd);
        fullscreenButton = findViewById(R.id.exo_fullscreen);



        //getting the date from the intent
        String videoResourceName = getIntent().getStringExtra("video_resource_name");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");
        String date = getIntent().getStringExtra("video_date");
        int likes = getIntent().getIntExtra("video_likes", 1);
        int views = getIntent().getIntExtra("video_views", 1);
        String videoLikes = Integer.toString(likes);
        String videoViewsString = Integer.toString(views) + "Views";

        videoTitle.setText(title);
        videoDescription.setText(description);
        videoViews.setText(videoViewsString);
        videoDate.setText(date);
        btnLike.setText(videoLikes);

        int videoResourceId = getResources().getIdentifier(videoResourceName, "raw", getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "viewtube"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(videoUri);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);




        // initialize the RecyclerView with related videos
        relatedVideos = new VideoList(this, this); // Pass the context and the listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(relatedVideos);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.db);
            List<VideoItem> videoItems = VideoItemParser.parseVideoItems(inputStream, this);
            relatedVideos.setVideoItems(videoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        playPauseButton.setOnClickListener(view -> togglePlayPause());
        rewindButton.setOnClickListener(view -> simpleExoPlayer.seekBack());
        forwardButton.setOnClickListener(view -> simpleExoPlayer.seekForward());
        fullscreenButton.setOnClickListener(view -> toggleFullscreen());

        // Listen for player state changes to update the play/pause button
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                if (playWhenReady) {
                    playPauseButton.setImageResource(R.drawable.pause);
                } else {
                    playPauseButton.setImageResource(R.drawable.play);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.width = params.MATCH_PARENT;
        params.height = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        isFullscreen = true;
    }

    private void exitFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if(getSupportActionBar() != null){
            getSupportActionBar().show();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.width = params.MATCH_PARENT;
        params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
        playerView.setLayoutParams(params);
        isFullscreen = false;
    }

    private void togglePlayPause() {
        if (simpleExoPlayer != null) {
            if (simpleExoPlayer.getPlayWhenReady()) {
                simpleExoPlayer.setPlayWhenReady(false);
            } else {
                simpleExoPlayer.setPlayWhenReady(true);
            }
        }
    }


    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        releasePlayer();
        String videoResourceName = videoItem.getVideoURL(); // Ensure this is the correct resource name
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        startActivity(moveToWatch);
    }

}