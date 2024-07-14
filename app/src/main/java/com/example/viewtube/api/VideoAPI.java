package com.example.viewtube.api;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.R;
import com.example.viewtube.VideoDao;
import com.example.viewtube.entities.VideoItem;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;

public class VideoAPI {

    private MutableLiveData<List<VideoItem>> videosListData;
    private VideoDao dao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;



    public VideoAPI(MutableLiveData<List<VideoItem>> videosListData, VideoDao dao, Context context) {
        this.videosListData = videosListData;
        this.dao = dao;

        retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void getAll() {
        Call<List<VideoItem>> call = webServiceAPI.getAllVideos();
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.body() != null) {
                    dao.clear();
                    dao.insertAll(response.body());
                    videosListData.postValue(dao.getAll());
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                // Error handling
            }
        });
    }

    public void add(VideoItem videoItem, File videoFile) {
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("videoFile", videoFile.getName(), videoRequestBody);

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getTitle());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getDescription());
        RequestBody uploaderBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getUploader());
        RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getDuration());

        Call<Void> call = webServiceAPI.createVideo(videoPart, titleBody, descriptionBody, uploaderBody, durationBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        dao.insert(videoItem);
                        videosListData.postValue(dao.getAll());
                    }).start();
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error handling
            }
        });
    }

    public void update(VideoItem videoItem) {
        Call<Void> call = webServiceAPI.updateVideo(videoItem.getId(), videoItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.update(videoItem);
                    videosListData.postValue(dao.getAll());
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error handling
            }
        });
    }

    public void delete(VideoItem videoItem) {
        Call<Void> call = webServiceAPI.deleteVideo(videoItem.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.delete(videoItem);
                    videosListData.postValue(dao.getAll());
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error handling
            }
        });
    }
}