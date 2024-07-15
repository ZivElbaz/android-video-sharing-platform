package com.example.viewtube.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VideoItem {

    @PrimaryKey(autoGenerate=true)
    private int id;
    private String title;
    private String description;
    private String uploader;
    private int views;
    private int likes;
    private String date;
    private String duration;
    private String videoUrl;
    private String thumbnailPath;

    // Constructor
    public VideoItem(int id, String title, String description, String uploader, int views, int likes, String date, String duration, String videoUrl, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.uploader = uploader;
        this.views = views;
        this.likes = likes;
        this.date = date;
        this.duration = duration;
        this.videoUrl = videoUrl;
        this.thumbnailPath = thumbnailPath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
