package com.example.viewtube.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.VideoAPI;
import com.example.viewtube.data.AppDB;
import com.example.viewtube.data.VideoDao;
import com.example.viewtube.entities.VideoItem;

import java.io.File;
import java.util.List;

public class VideosRepository {

    private VideoDao dao;
    private VideoAPI api;
    private MutableLiveData<VideoItem> selectedVideoItem = new MutableLiveData<>();

    // Constructor initializes the repository by setting up the DAO and API
    public VideosRepository(Context context) {
        dao = AppDB.getInstance(context).videoDao();
        api = new VideoAPI(dao, context);
    }

    // Retrieves all video items from the Room database
    public LiveData<List<VideoItem>> getAllVideos() {
        return dao.getAll();
    }

    // Retrieves a specific video item by its ID from the Room database
    public VideoItem getVideoItem(int videoId) {
        return dao.getVideoItemSync(videoId);
    }

    // Fetches all video items from the API and updates the Room database
    public void fetchAllVideos() {
        api.getAll();
    }

    // Sets the selected video item in LiveData (used to share the selected video across components)
    public void setSelectedVideoItem(VideoItem videoItem) {
        selectedVideoItem.setValue(videoItem);
    }

    // Posts the selected video item asynchronously in LiveData
    public void postSelectedVideoItem(VideoItem videoItem) {
        selectedVideoItem.postValue(videoItem);
    }

    // Updates a video item in the Room database
    public void updateVideo(VideoItem videoItem) {
        new Thread(() -> dao.update(videoItem)).start();
    }

    // Retrieves the currently selected video item as LiveData
    public LiveData<VideoItem> getSelectedVideoItem() {
        return selectedVideoItem;
    }

    // Adds a new video item, along with its file and thumbnail, to the server via the API
    public void add(final VideoItem videoItem, File videoFile, File thumbnailFile) {
        api.add(videoItem, videoFile, thumbnailFile);
    }

    // Updates an existing video item on the server via the API
    public void update(final int videoId, String username, String title, String description) {
        api.updateVideo(videoId, username, title, description);
    }

    // Deletes a video item by its ID from both the server and the local Room database
    public void delete(final int videoId, String username) {
        api.deleteVideo(videoId, username);
    }

    // Reloads all video items by fetching them from the server
    public void reload() {
        api.getAll();
    }

    // Toggles the like status of a video for the current user on the server
    public void userLiked(int videoId, String username) {
        api.toggleLike(videoId, username);
    }

    // Increments the view count of a video on the server
    public void incrementViewCount(int videoId) {
        api.incrementViewCount(videoId);
    }
}
