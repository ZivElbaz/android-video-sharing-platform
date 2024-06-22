package com.example.viewtube;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoViewModel extends AndroidViewModel {

    // List to hold all video items
    private static List<VideoItem> allVideoItems = new ArrayList<>();
    // LiveData to hold the video items list for observation
    private static final MutableLiveData<List<VideoItem>> videoItemsLiveData = new MutableLiveData<>();

    // Constructor to initialize the ViewModel
    public VideoViewModel(@NonNull Application application) {
        super(application);
        // Load video items if the list is empty
        if (allVideoItems.isEmpty()) {
            loadVideoItems(application);
        } else {
            videoItemsLiveData.setValue(allVideoItems);
        }
    }

    // Method to load video items from a raw resource
    private void loadVideoItems(Application application) {
        try (InputStream inputStream = application.getResources().openRawResource(R.raw.db)) {
            // Parse the video items from the input stream
            allVideoItems = VideoItemParser.parseVideoItems(inputStream, application.getApplicationContext());
            // Set the parsed video items to the LiveData
            videoItemsLiveData.setValue(allVideoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Method to get the video items LiveData
    public LiveData<List<VideoItem>> getVideoItems() {
        return videoItemsLiveData;
    }

    // Method to update the video items list
    public void updateVideoList() {
        videoItemsLiveData.postValue(allVideoItems);
    }

    // Method to add a new video item to the list
    public void addVideoItem(VideoItem videoItem) {
        allVideoItems.add(videoItem);
        videoItemsLiveData.setValue(allVideoItems);
    }

    // Method to remove a video item from the list by ID
    public void removeVideoItem(int id) {
        for (int i = 0; i < allVideoItems.size(); i++) {
            if (allVideoItems.get(i).getId() == id) {
                allVideoItems.remove(i);
                break;
            }
        }
        videoItemsLiveData.setValue(allVideoItems);
    }

    // Method to update the title of a video item by ID
    public void updateVideoItemTitle(int id, String title) {
        for (int i = 0; i < allVideoItems.size(); i++) {
            if (allVideoItems.get(i).getId() == id) {
                allVideoItems.get(i).setTitle(title);
                break;
            }
        }
        videoItemsLiveData.setValue(allVideoItems);
    }

    // Method to update the description of a video item by ID
    public void updateVideoItemDescription(int id, String description) {
        for (int i = 0; i < allVideoItems.size(); i++) {
            if (allVideoItems.get(i).getId() == id) {
                allVideoItems.get(i).setDescription(description);
                break;
            }
        }
        videoItemsLiveData.setValue(allVideoItems);
    }
}
