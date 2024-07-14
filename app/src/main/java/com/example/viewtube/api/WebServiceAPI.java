package com.example.viewtube.api;

import com.example.viewtube.entities.VideoItem;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Body;


public interface WebServiceAPI {

    @GET("videos")
    Call<List<VideoItem>> getAllVideos();

    @GET("videos/{id}")
    Call<VideoItem> getVideo(@Path("id") int id);

    @PATCH("videos/{id}")
    Call<Void> updateVideo(@Path("id") int id, @Body VideoItem videoItem);

    @DELETE("videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);

    @Multipart
    @POST("videos")
    Call<Void> createVideo(
            @Part MultipartBody.Part videoFile,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("uploader") RequestBody uploader,
            @Part("duration") RequestBody duration
    );

    @POST("videos/{id}/like")
    Call<Void> likeVideo(@Path("id") int id, @Body Map<String, String> body);

    @PATCH("videos/{id}/view")
    Call<Void> incrementViewCount(@Path("id") int id);


}
