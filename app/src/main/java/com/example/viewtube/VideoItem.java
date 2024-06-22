package com.example.viewtube;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class VideoItem implements Parcelable {
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
    private String thumbnailPath;

    // Constructor
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

    // Constructor for uploaded videos with thumbnail path
    public VideoItem(int id, String title, String description, String author, int views, int likes, String date, String duration, String videoURL, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.views = views;
        this.likes = likes;
        this.date = date;
        this.duration = calculateVideoDuration(videoURL);
        this.videoURL = videoURL;
        this.thumbnailPath = thumbnailPath;
    }

    // Parcelable constructor
    protected VideoItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        author = in.readString();
        views = in.readInt();
        likes = in.readInt();
        date = in.readString();
        duration = in.readString();
        videoURL = in.readString();
        thumbnailResId = in.readInt();
        thumbnailPath = in.readString();
    }

    // Parcelable CREATOR
    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeInt(views);
        dest.writeInt(likes);
        dest.writeString(date);
        dest.writeString(duration);
        dest.writeString(videoURL);
        dest.writeInt(thumbnailResId);
        dest.writeString(thumbnailPath);
    }

    // Method to calculate video duration
    private String calculateVideoDuration(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            File videoFile = new File(videoPath);
            retriever.setDataSource(videoFile.getAbsolutePath());

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time == null) {
                return "00:00";
            }

            long timeInMillisec = Long.parseLong(time);
            long duration = timeInMillisec / 1000;
            long hours = duration / 3600;
            long minutes = (duration % 3600) / 60;
            long seconds = duration % 60;

            if (hours > 0) {
                return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            return "00:00";
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
