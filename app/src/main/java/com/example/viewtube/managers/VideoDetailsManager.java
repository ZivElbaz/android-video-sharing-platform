package com.example.viewtube.managers;

import android.widget.TextView;
import android.widget.Button;

public class VideoDetailsManager {

    private TextView videoTitle, videoDescription, videoDate, videoViews;
    private Button btnLike;

    public VideoDetailsManager(TextView videoTitle, TextView videoDescription, TextView videoDate,
                               TextView videoViews, Button btnLike) {
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoViews = videoViews;
        this.btnLike = btnLike;
    }

    public void setVideoDetails(String title, String description, String date, int views, int likes) {
        videoTitle.setText(title);
        videoDescription.setText(description);
        videoViews.setText(String.valueOf(views) + " Views");
        videoDate.setText(date);
        btnLike.setText(String.valueOf(likes));
    }
}