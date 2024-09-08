package com.example.viewtube.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.entities.Comment;
import com.example.viewtube.managers.Config;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAPI {

    private static final String BASE_URL = Config.getBaseUrl(); // Base URL for the API
    private WebServiceAPI webServiceAPI; // API interface for web service calls

    // Constructor to initialize the Retrofit instance and WebServiceAPI
    public CommentAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/") // Append "api/" to the base URL
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON serialization
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class); // Create WebServiceAPI instance
    }

    // Method to create a new comment using the API
    public void createComment(Comment comment, MutableLiveData<Comment> liveData) {
        Call<Comment> call = webServiceAPI.createComment(comment); // Make the API call
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body()); // Update live data with the new comment
                } else {
                    Log.e("CommentAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("CommentAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Method to retrieve comments by video ID
    public void getCommentsByVideoId(int videoId, MutableLiveData<List<Comment>> liveData) {
        Call<List<Comment>> call = webServiceAPI.getCommentsByVideoId(videoId); // Make the API call
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body()); // Update live data with the list of comments
                } else {
                    Log.e("CommentAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Method to update an existing comment using the API
    public void updateComment(Comment comment, MutableLiveData<Comment> liveData) {
        Call<Comment> call = webServiceAPI.updateComment(comment.getId(), comment); // Make the API call
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body()); // Update live data with the updated comment
                } else {
                    Log.e("CommentAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("CommentAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Method to delete a comment using the API
    public void deleteComment(int commentId, int videoId, MutableLiveData<Boolean> liveData) {
        Call<Void> call = webServiceAPI.deleteComment(commentId, videoId); // Make the API call
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(true); // Update live data to indicate success
                } else {
                    liveData.postValue(false); // Update live data to indicate failure
                    Log.e("CommentAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.postValue(false); // Update live data to indicate failure
                Log.e("CommentAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }
}
