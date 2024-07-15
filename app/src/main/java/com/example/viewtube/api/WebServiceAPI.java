package com.example.viewtube.api;

import com.example.viewtube.entities.Comment;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
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
    Call<VideoItem> updateVideo(@Path("id") int id, @Body VideoAPI.VideoUpdate videoUpdate);

    @DELETE("videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);

    @Multipart
    @POST("videos")
    Call<VideoItem> createVideo(
            @Part MultipartBody.Part videoFile,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("uploader") RequestBody uploader,
            @Part("duration") RequestBody duration
    );

    @POST("videos/{id}/like")
    Call<Void> userLiked(@Path("id") int id, @Body Map<String, String> body);

    @PATCH("videos/{id}/view")
    Call<Void> incrementViewCount(@Path("id") int id);

    // User APIs
    @POST("users")
    Call<User> createUser(@Body User user);

    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @POST("users/authenticate")
    Call<User> authenticateUser(@Body User user);

    @GET("users/check/{username}")
    Call<Boolean> checkUsernameExists(@Path("username") String username);



    @GET("users/picture/{username}")
    Call<ProfilePictureResponse> getPictureByUsername(@Path("username") String username);



    @PATCH("users/{username}")
    Call<User> updateUser(@Path("username") String username, @Body User user);

    @DELETE("users/{username}")
    Call<Void> deleteUser(@Path("username") String username);

    @POST("users/password")
    Call<Void> updatePassword(@Body Map<String, String> body);

    @GET("users/{id}/videos")
    Call<List<VideoItem>> getVideosByUploader(@Path("id") String uploader);

    // Comment APIs
    @POST("comments")
    Call<Comment> createComment(@Body Comment comment);

    @GET("comments/video/{videoId}")
    Call<List<Comment>> getCommentsByVideoId(@Path("videoId") int videoId);

    @PATCH("comments/{id}")
    Call<Comment> updateComment(@Path("id") int id, @Body Comment comment);

    @DELETE("comments/{id}")
    Call<Void> deleteComment(@Path("id") int id);
}