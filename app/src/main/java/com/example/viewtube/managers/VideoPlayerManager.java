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

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
    private boolean isFullscreen = false;

    public VideoPlayerManager(PlayerView playerView, ImageButton playPauseButton, ImageButton rewindButton,
                              ImageButton forwardButton, ImageButton fullscreenButton) {
        this.playerView = playerView;
        this.playPauseButton = playPauseButton;
        this.rewindButton = rewindButton;
        this.forwardButton = forwardButton;
        this.fullscreenButton = fullscreenButton;
    }

    public void initializePlayer(Context context, Uri videoUri) {
        simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "viewtube"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);

        setupPlayerControls(context);
    }

    private void setupPlayerControls(Context context) {
        playPauseButton.setOnClickListener(view -> togglePlayPause());
        rewindButton.setOnClickListener(view -> simpleExoPlayer.seekBack());
        forwardButton.setOnClickListener(view -> simpleExoPlayer.seekForward());
        fullscreenButton.setOnClickListener(view -> toggleFullscreen(context));
    }

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

    private void toggleFullscreen(Context context) {
        if (isFullscreen) {
            exitFullscreen(context);
        } else {
            enterFullscreen(context);
        }
    }

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

    public void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private String getFilePathFromContentUri(Context context, Uri contentUri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
}