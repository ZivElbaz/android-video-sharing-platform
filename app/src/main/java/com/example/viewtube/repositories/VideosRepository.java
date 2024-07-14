package com.example.viewtube.repositories;

import static com.example.viewtube.MainActivity.context;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.viewtube.AppDB;
import com.example.viewtube.VideoDao;
import com.example.viewtube.api.VideoAPI;
import com.example.viewtube.entities.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideosRepository {
    private AppDB db;
    private VideoDao dao;

    private VideoAPI api;

    private VideoListData videoListData;


    public VideosRepository() {
        db = Room.databaseBuilder(context, AppDB.class, "VideosDB")
                .allowMainThreadQueries()
                .build();

        dao = db.videoDao();

        videoListData = new VideoListData();

        api = new VideoAPI(videoListData, dao, context);

    }

    class VideoListData extends MutableLiveData<List<VideoItem>> {

        public VideoListData() {
            super();
            setValue(dao.getAll());

        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread( () -> {
                api.getAll();
            }).start();


        }
    }

    public LiveData<List<VideoItem>> getAll() {
        return videoListData;
    }

    public VideoItem getById(int id) {
        return dao.get(id);
    }

    public void add(final VideoItem videoItem, File videoFile) {
        api.add(videoItem, videoFile);
    }

    public void update(final VideoItem videoItem) {
        api.update(videoItem);
    }

    public void delete(final VideoItem videoItem) {
        api.delete(videoItem);
    }

    public void reload() {
        api.getAll();
    }

}
