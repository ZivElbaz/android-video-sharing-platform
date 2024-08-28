package com.example.viewtube.api;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.CommentDao;
import com.example.viewtube.entities.Comment;


import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;

public class CommentAPI {

    private static final String BASE_URL = "http://192.168.1.100:12345/";
    private WebServiceAPI webServiceAPI;
    private CommentDao commentDao;

    public CommentAPI(CommentDao commentDao, Context context) {
        this.commentDao = commentDao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void createComment(Comment comment, MutableLiveData<Comment> liveData) {
        Call<Comment> call = webServiceAPI.createComment(comment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        commentDao.insert(response.body());
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("CommentAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("CommentAPI", "Request failed", t);
            }
        });
    }

    public void getCommentsByVideoId(int videoId, MutableLiveData<List<Comment>> liveData) {
        Call<List<Comment>> call = webServiceAPI.getCommentsByVideoId(videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        commentDao.clear();
                        commentDao.insert(response.body().toArray(new Comment[0]));
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("CommentAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentAPI", "Request failed", t);
            }
        });
    }

    public void deleteComment(int commentId, MutableLiveData<Boolean> liveData) {
        Call<Void> call = webServiceAPI.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        commentDao.deleteById(commentId);
                        liveData.postValue(true);
                    }).start();
                } else {
                    liveData.postValue(false);
                    Log.e("CommentAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.postValue(false);
                Log.e("CommentAPI", "Request failed", t);
            }
        });
    }
}
