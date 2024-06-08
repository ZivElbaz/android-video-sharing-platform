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
    // Add more attributes as needed

    public VideoItem(String title, String description, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
