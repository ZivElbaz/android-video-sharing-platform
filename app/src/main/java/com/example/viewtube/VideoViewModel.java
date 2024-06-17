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

    private static List<VideoItem> allVideoItems = new ArrayList<>();
    private static MutableLiveData<List<VideoItem>> videoItemsLiveData = new MutableLiveData<>();

    public VideoViewModel(@NonNull Application application) {
        super(application);
        if (allVideoItems.isEmpty()) {
            loadVideoItems(application);
        } else {
            videoItemsLiveData.setValue(allVideoItems);
        }
    }

    private void loadVideoItems(Application application) {
        try (InputStream inputStream = application.getResources().openRawResource(R.raw.db)) {
            allVideoItems = VideoItemParser.parseVideoItems(inputStream, application.getApplicationContext());
            videoItemsLiveData.setValue(allVideoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<VideoItem>> getVideoItems() {
        return videoItemsLiveData;
    }

    public void updateVideoList() {
        videoItemsLiveData.postValue(allVideoItems);
    }

    public void addVideoItem(VideoItem videoItem) {
        allVideoItems.add(videoItem);
        videoItemsLiveData.setValue(allVideoItems);
    }

    public void removeVideoItem(int id) {
        for (int i = 0; i < allVideoItems.size(); i++) {
            if (allVideoItems.get(i).getId() == id) {
                allVideoItems.remove(i);
                break;
            }
        }
        videoItemsLiveData.setValue(allVideoItems);
    }

    public void updateVideoItemTitle(int id, String title) {
        for (int i = 0; i < allVideoItems.size(); i++) {
            if (allVideoItems.get(i).getId() == id) {
                allVideoItems.get(i).setTitle(title);
                break;
            }
        }
        videoItemsLiveData.setValue(allVideoItems);
    }

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
