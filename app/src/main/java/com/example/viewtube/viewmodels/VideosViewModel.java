package com.example.viewtube.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.repositories.VideosRepository;

import java.io.File;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;

public class VideosViewModel extends ViewModel {

    // List to hold all video items
    private static List<VideoItem> allVideoItems = new ArrayList<>();

    private VideosRepository mRepository;
    // LiveData to hold the video items list for observation
    private LiveData<List<VideoItem>> videoItemsLiveData;


   public VideosViewModel() {
       mRepository = new VideosRepository();
       videoItemsLiveData = mRepository.getAll();
   }
    public LiveData<List<VideoItem>> getVideoItemsLiveData() {
        return videoItemsLiveData;
    }

    // Method to add a new video item to the list
    public void addVideoItem(final VideoItem videoItem, File videoFile) {
        mRepository.add(videoItem,
                videoFile);
    }

    public void deleteVideoItem(VideoItem videoItem) {
        mRepository.delete(videoItem);
    }


    public void updateVideoList() {
        mRepository.reload();
    }

    public void removeVideoItem(int id) {
        VideoItem videoItem = mRepository.getById(id);
        if (videoItem != null) {
            mRepository.delete(videoItem);
        }
    }

    public void updateVideoItemTitle(int id, String title) {
        VideoItem videoItem = mRepository.getById(id);
        if (videoItem != null) {
            videoItem.setTitle(title);
            mRepository.update(videoItem);
        }
    }

    public void updateVideoItemDescription(int id, String description) {
        VideoItem videoItem = mRepository.getById(id);
        if (videoItem != null) {
            videoItem.setDescription(description);
            mRepository.update(videoItem);
        }
    }
}
