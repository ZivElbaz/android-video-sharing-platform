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

    private MutableLiveData<List<VideoItem>> videoItemsLiveData;
    private List<VideoItem> allVideoItems;

    public VideoViewModel(@NonNull Application application) {
        super(application);
        videoItemsLiveData = new MutableLiveData<>();
        allVideoItems = new ArrayList<>();
        loadVideoItems();
    }

    private void loadVideoItems() {
        try (InputStream inputStream = getApplication().getResources().openRawResource(R.raw.db)) {
            allVideoItems = VideoItemParser.parseVideoItems(inputStream, getApplication().getApplicationContext());
            if (allVideoItems != null) {
                videoItemsLiveData.postValue(allVideoItems);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<VideoItem>> getVideoItems() {
        return videoItemsLiveData;
    }

    public void addVideoItem(VideoItem videoItem) {
        allVideoItems.add(videoItem);
        videoItemsLiveData.postValue(allVideoItems);
    }

    public List<VideoItem> getAllVideoItems() {
        return allVideoItems;
    }
}
