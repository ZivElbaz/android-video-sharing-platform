package com.example.viewtube.managers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerManager {

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private ImageButton playPauseButton, rewindButton, forwardButton, fullscreenButton;
    private TextView exoPosition, exoDuration;
    private boolean isFullscreen = false;

    // Constructor to initialize the VideoPlayerManager with UI components
    public VideoPlayerManager(PlayerView playerView, ImageButton playPauseButton, ImageButton rewindButton,
                              ImageButton forwardButton, ImageButton fullscreenButton, TextView exoPosition, TextView exoDuration) {
        this.playerView = playerView;
        this.playPauseButton = playPauseButton;
        this.rewindButton = rewindButton;
        this.forwardButton = forwardButton;
        this.fullscreenButton = fullscreenButton;
        this.exoPosition = exoPosition;
        this.exoDuration = exoDuration;
    }

    // Method to initialize the player with a given video URI
    public void initializePlayer(Context context, Uri videoUri) {
        // Build the SimpleExoPlayer instance
        simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
        // Create a data source factory
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "viewtube"));
        // Create a media source from the video URI
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
        // Set the player to the player view
        playerView.setPlayer(simpleExoPlayer);
        // Keep the screen on while playing
        playerView.setKeepScreenOn(true);
        // Prepare the player with the media source
        simpleExoPlayer.prepare(mediaSource);
        // Start playback
        simpleExoPlayer.setPlayWhenReady(true);

        // Setup player controls and progress listener
        setupPlayerControls(context);
        setupProgressListener();
    }

    // Method to setup player control buttons
    private void setupPlayerControls(Context context) {
        playPauseButton.setOnClickListener(view -> togglePlayPause());
        rewindButton.setOnClickListener(view -> simpleExoPlayer.seekBack());
        forwardButton.setOnClickListener(view -> simpleExoPlayer.seekForward());
        fullscreenButton.setOnClickListener(view -> toggleFullscreen(context));
    }

    // Method to toggle play/pause state
    private void togglePlayPause() {
        if (simpleExoPlayer != null) {
            if (simpleExoPlayer.getPlayWhenReady()) {
                simpleExoPlayer.setPlayWhenReady(false);
                playPauseButton.setImageResource(R.drawable.play);
            } else {
                simpleExoPlayer.setPlayWhenReady(true);
                playPauseButton.setImageResource(R.drawable.pause);
            }
        }
    }

    // Method to toggle fullscreen mode
    private void toggleFullscreen(Context context) {
        if (isFullscreen) {
            exitFullscreen(context);
        } else {
            enterFullscreen(context);
        }
    }

    // Method to enter fullscreen mode
    private void enterFullscreen(Context context) {
        ((AppCompatActivity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (((AppCompatActivity) context).getSupportActionBar() != null) {
            ((AppCompatActivity) context).getSupportActionBar().hide();
        }
        ((AppCompatActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.width = params.MATCH_PARENT;
        params.height = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        isFullscreen = true;
    }

    // Method to exit fullscreen mode
    private void exitFullscreen(Context context) {
        ((AppCompatActivity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (((AppCompatActivity) context).getSupportActionBar() != null) {
            ((AppCompatActivity) context).getSupportActionBar().show();
        }
        ((AppCompatActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.width = params.MATCH_PARENT;
        params.height = (int) (200 * context.getResources().getDisplayMetrics().density);
        playerView.setLayoutParams(params);
        isFullscreen = false;
    }

    // Method to release the player and free resources
    public void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    // Method to setup progress listener for the player
    private void setupProgressListener() {
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    updateProgress();
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                updateProgress();
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                updateProgress();
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                updateProgress();
            }
        });
    }

    // Method to update the progress of the player
    private void updateProgress() {
        long position = simpleExoPlayer.getCurrentPosition();
        long duration = simpleExoPlayer.getDuration();
        exoPosition.setText(formatTime(position));
        exoDuration.setText(formatTime(duration));
    }

    // Utility method to format time from milliseconds to a string
    private String formatTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
