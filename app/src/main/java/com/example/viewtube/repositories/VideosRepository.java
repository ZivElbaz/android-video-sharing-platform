package com.example.viewtube.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.AppDB;
import com.example.viewtube.VideoDao;
import com.example.viewtube.api.VideoAPI;
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

    public LiveData<VideoItem> getVideoItem(int videoId) {
        return dao.get(videoId);
    }

    public void fetchAllVideos() {
        api.getAll();
    }

    public void fetchVideoDetails(int videoId) {
        api.getVideo(videoId);
    }

    public void setSelectedVideoItem(VideoItem videoItem) {
        selectedVideoItem.setValue(videoItem);
    }

    public LiveData<VideoItem> getSelectedVideoItem() {
        return selectedVideoItem;
    }

    public void add(final VideoItem videoItem, File videoFile) {
        api.add(videoItem, videoFile);
    }

    public void update(final int videoId, String title, String description) {
        api.update(videoId, title, description);
    }

    public void delete(final int videoId) {
        api.delete(videoId);
    }

    public void reload() {
        api.getAll();
    }

    public void userLiked(int videoId, String username) {
        api.userLiked(videoId, username);
    }
}
