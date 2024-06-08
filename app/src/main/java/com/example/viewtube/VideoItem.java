package com.example.viewtube;

public class VideoItem {
    private int id;
    private String title;
    private String description;
    private String author;
    private int views;
    private int likes;
    private String date;
    private String duration;
    private String videoURL;
    private int thumbnailResId;

    public VideoItem(int id, String title, String description, String author, int views, int likes, String date, String duration, String videoURL, int thumbnailResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.views = views;
        this.likes = likes;
        this.date = date;
        this.duration = duration;
        this.videoURL = videoURL;
        this.thumbnailResId = thumbnailResId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }
}
