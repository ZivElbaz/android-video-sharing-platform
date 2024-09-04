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

    public VideosRepository(Context context) {
        dao = AppDB.getInstance(context).videoDao();
        api = new VideoAPI(dao, context);
    }

    public LiveData<List<VideoItem>> getAllVideos() {
        return dao.getAll();
    }

    public VideoItem getVideoItem(int videoId) {
        return dao.getVideoItemSync(videoId);
    }

    public LiveData<VideoItem> getLiveVideoItem(int videoId) {
        return dao.get(videoId);
    }

    public void fetchAllVideos() {
        api.getAll();
    }


    public void setSelectedVideoItem(VideoItem videoItem) {
        selectedVideoItem.setValue(videoItem);
    }

    public void postSelectedVideoItem(VideoItem videoItem) {
        selectedVideoItem.postValue(videoItem);
    }

    public void updateVideo(VideoItem videoItem) {
        new Thread(() -> dao.update(videoItem)).start();
    }


    public LiveData<VideoItem> getSelectedVideoItem() {
        return selectedVideoItem;
    }

    public void add(final VideoItem videoItem, File videoFile, File thumbnailFile) {
        api.add(videoItem, videoFile, thumbnailFile);
    }

    public void update(final int videoId, String username, String title, String description) {
        api.updateVideo(videoId, username, title, description);
    }

    public void delete(final int videoId, String username) {
        api.deleteVideo(videoId, username);
    }

    public void reload() {
        api.getAll();
    }

    public void userLiked(int videoId, String username) {
        api.toggleLike(videoId, username);
    }

    public void incrementViewCount(int videoId) {
        api.incrementViewCount(videoId);
    }
}
