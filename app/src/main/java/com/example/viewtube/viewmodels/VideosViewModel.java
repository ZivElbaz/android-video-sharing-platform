package com.example.viewtube.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.repositories.VideosRepository;

import java.io.File;
import java.util.List;

public class VideosViewModel extends AndroidViewModel {

    private VideosRepository mRepository;
    private LiveData<List<VideoItem>> videoItemsLiveData;
    private LiveData<VideoItem> selectedVideoItemLiveData;

    // Constructor initializes the repository and LiveData for video items
    public VideosViewModel(Application application) {
        super(application);
        mRepository = new VideosRepository(application);
        videoItemsLiveData = mRepository.getAllVideos(); // Get all videos as LiveData
        selectedVideoItemLiveData = mRepository.getSelectedVideoItem(); // Get selected video as LiveData
    }

    // Returns LiveData for the list of video items
    public LiveData<List<VideoItem>> getVideoItemsLiveData() {
        return videoItemsLiveData;
    }

    // Returns LiveData for the currently selected video item
    public LiveData<VideoItem> getSelectedVideoItemLiveData() {
        return selectedVideoItemLiveData;
    }

    // Fetches all videos from the repository (API or database)
    public void fetchAllVideos() {
        mRepository.fetchAllVideos();
    }

    // Retrieves a video item by its ID
    public VideoItem getVideoById(int videoId) {
        return mRepository.getVideoItem(videoId);
    }

    // Sets the selected video item
    public void setSelectedVideoItem(VideoItem videoItem) {
        mRepository.setSelectedVideoItem(videoItem);
    }

    // Posts the selected video item asynchronously
    public void postSelectedVideoItem(VideoItem videoItem) {
        mRepository.postSelectedVideoItem(videoItem);
    }

    // Fetches a video item by its ID and updates the selected video item
    public void fetchSelectedVideoItem(int videoId) {
        new Thread(() -> {
            VideoItem videoItem = getVideoById(videoId);
            postSelectedVideoItem(videoItem);
        }).start();
    }

    // Adds a new video item along with its file and thumbnail
    public void addVideoItem(final VideoItem videoItem, File videoFile, File thumbnailFile) {
        mRepository.add(videoItem, videoFile, thumbnailFile);
    }

    // Updates the title of a video item
    public void updateVideoItemTitle(int videoId, String username, String title) {
        VideoItem videoItem = selectedVideoItemLiveData.getValue();
        if (videoItem != null) {
            videoItem.setTitle(title);
            mRepository.update(videoId, username, title, videoItem.getDescription());
        }
    }

    // Updates the description of a video item
    public void updateVideoItemDescription(int videoId, String username, String description) {
        VideoItem videoItem = selectedVideoItemLiveData.getValue();
        if (videoItem != null) {
            videoItem.setDescription(description);
            mRepository.update(videoId, username, videoItem.getTitle(), description);
        }
    }

    // Deletes a video item by its ID and username
    public void deleteVideoItem(int videoId, String username) {
        mRepository.delete(videoId, username);
    }

    // Reloads all video items by fetching from the repository
    public void reload() {
        mRepository.reload();
    }

    // Updates a video item in the repository
    public void updateVideo(VideoItem videoItem) {
        mRepository.updateVideo(videoItem);
    }

    // Toggles the like status of a video item for a user
    public void userLiked(int videoId, String username) {
        mRepository.userLiked(videoId, username);
    }

    // Increments the view count for a video item
    public void incrementViewCount(int videoId) {
        mRepository.incrementViewCount(videoId);
    }
}
