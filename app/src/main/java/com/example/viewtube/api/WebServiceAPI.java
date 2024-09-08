package com.example.viewtube.api;

import com.example.viewtube.entities.AuthResponse;
import com.example.viewtube.entities.Comment;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.UsernameCheckResponse;
import com.example.viewtube.entities.VideoItem;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface WebServiceAPI {
    // Video APIs
    @GET("videos")
    Call<List<VideoItem>> getAllVideos();

    @PATCH("users/{username}/videos/{id}")
    Call<VideoItem> updateVideo(@Path("username") String username, @Path("id") int id, @Body VideoAPI.VideoUpdate videoUpdate);

    @DELETE("users/{username}/videos/{id}")
    Call<Void> deleteVideo(@Path("username") String username, @Path("id") int id);

    @Multipart
    @POST("videos")
    Call<VideoItem> createVideo(
            @Part MultipartBody.Part videoFile,
            @Part MultipartBody.Part thumbnail,
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
    Call<AuthResponse> createUser(@Body Map<String, String> userMap);

    @GET("users/{username}")
    Call<User> getUserData(@Path("username") String username);

    @POST("token")
    Call<AuthResponse> authenticateUser(@Body User user);

    @GET("users/check/{username}")
    Call<UsernameCheckResponse> checkUsernameExists(@Path("username") String username);

    @GET("users/picture/{username}")
    Call<ProfilePictureResponse> getPictureByUsername(@Path("username") String username);

    @PUT("users/{username}")
    Call<User> updateUser(@Path("username") String username, @Body Map<String, String> userData);

    @HTTP(method = "DELETE", path = "users/{username}", hasBody = true)
    Call<Void> deleteUser(@Path("username") String username, @Body Map<String, String> passwordData);

    @POST("users/password")
    Call<AuthResponse> updatePassword(@Body Map<String, String> passwordData);

    @GET("users/{username}/videos")
    Call<List<VideoItem>> getVideosByUsername(@Path("username") String username);


    // Comment APIs
    @POST("comments")
    Call<Comment> createComment(@Body Comment comment);

    @GET("comments/video/{videoId}")
    Call<List<Comment>> getCommentsByVideoId(@Path("videoId") int videoId);

    @PATCH("comments/{id}")
    Call<Comment> updateComment(@Path("id") int id, @Body Comment comment);

    @DELETE("comments/{id}")
    Call<Void> deleteComment(@Path("id") int id, @Query("videoId") int videoId);


}
