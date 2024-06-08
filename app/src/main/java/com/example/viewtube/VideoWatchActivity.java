package com.example.viewtube;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.VideoView;
import android.net.Uri;


public class VideoWatchActivity extends AppCompatActivity {

    private VideoView videoPlayer;
    private TextView videoTitle;
    private TextView videoDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        videoPlayer = findViewById(R.id.videoPlayer);
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);

        String videoUrl = getIntent().getStringExtra("video_url");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");

        videoTitle.setText(title);
        videoDescription.setText(description);

        Uri videoUri = Uri.parse(videoUrl);
        videoPlayer.setVideoURI(videoUri);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer.start();
            }
        });
        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoPlayer.seekTo(0); // Restart video
                videoPlayer.start();   // Start playing again
            }
        });

    }
}