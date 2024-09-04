package com.example.viewtube.api;

import android.content.Context;
import android.util.Log;

import com.example.viewtube.data.VideoDao;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.Config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;

public class VideoAPI {

    private static final String BASE_URL = Config.getBaseUrl(); // Base URL for API requests
    private WebServiceAPI webServiceAPI;
    private VideoDao videoDao;

    // Constructor to initialize Retrofit and WebServiceAPI
    public VideoAPI(VideoDao dao, Context context) {
        this.videoDao = dao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/") // Append "api/" to the base URL
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class); // Create WebServiceAPI instance
    }

    // Helper method to get the full URL of a video or thumbnail
    private String getFullVideoUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    // Fetch all videos from the API and store them in the local database
    public void getAll() {
        Call<List<VideoItem>> call = webServiceAPI.getAllVideos(); // Make API call to fetch all videos
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<VideoItem> videoItems = response.body();
                        for (VideoItem videoItem : videoItems) {
                            videoItem.setVideoUrl(getFullVideoUrl(videoItem.getVideoUrl())); // Set full video URL
                            videoItem.setThumbnail(getFullVideoUrl(videoItem.getThumbnail())); // Set full thumbnail URL
                            videoItem.setTimestamp(System.currentTimeMillis()); // Set current timestamp

                            // Fetch and set profile picture for each video item
                            Call<ProfilePictureResponse> pictureCall = webServiceAPI.getPictureByUsername(videoItem.getUploader());
                            try {
                                Response<ProfilePictureResponse> pictureResponse = pictureCall.execute();
                                if (pictureResponse.isSuccessful() && pictureResponse.body() != null) {
                                    String base64Image = pictureResponse.body().getProfilePicture();
                                    if (base64Image != null && base64Image.startsWith("data:image/jpeg;base64,")) {
                                        base64Image = base64Image.substring(23); // Remove base64 prefix
                                    }
                                    videoItem.setProfilePicture(base64Image); // Set profile picture
                                } else {
                                    videoItem.setProfilePicture(null); // No profile picture
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                videoItem.setProfilePicture(null); // Handle failure
                            }
                        }
                        videoDao.clear(); // Clear existing videos in local database
                        videoDao.insertAll(videoItems); // Insert new video items into the database
                    }).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if the request failed
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the API call fails
            }
        });
    }

    // Upload a new video and its thumbnail to the server
    public void add(VideoItem videoItem, File videoFile, File thumbnailFile) {
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile); // Create request body for video
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("videoFile", videoFile.getName(), videoRequestBody); // Prepare video part

        // Create request bodies for other fields
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getTitle());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getDescription());
        RequestBody uploaderBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getUploader());
        RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), videoItem.getDuration());

        // Handle the thumbnail file
        RequestBody thumbnailRequestBody = RequestBody.create(MediaType.parse("image/png"), thumbnailFile);
        MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData("thumbnail", thumbnailFile.getName(), thumbnailRequestBody);

        // Make the API call to upload the video and thumbnail
        Call<VideoItem> call = webServiceAPI.createVideo(videoPart, thumbnailPart, titleBody, descriptionBody, uploaderBody, durationBody);
        call.enqueue(new Callback<VideoItem>() {
            @Override
            public void onResponse(Call<VideoItem> call, Response<VideoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        VideoItem serverVideoItem = response.body();
                        serverVideoItem.setVideoUrl(getFullVideoUrl(serverVideoItem.getVideoUrl())); // Set full video URL
                        serverVideoItem.setThumbnail(getFullVideoUrl(serverVideoItem.getThumbnail())); // Set full thumbnail URL
                        serverVideoItem.setTimestamp(System.currentTimeMillis()); // Set timestamp

                        // Fetch profile picture for the uploaded video
                        Call<ProfilePictureResponse> pictureCall = webServiceAPI.getPictureByUsername(serverVideoItem.getUploader());
                        try {
                            Response<ProfilePictureResponse> pictureResponse = pictureCall.execute();
                            if (pictureResponse.isSuccessful() && pictureResponse.body() != null) {
                                String base64Image = pictureResponse.body().getProfilePicture();
                                if (base64Image != null && base64Image.startsWith("data:image/jpeg;base64,")) {
                                    base64Image = base64Image.substring(23); // Remove base64 prefix
                                }
                                serverVideoItem.setProfilePicture(base64Image); // Set profile picture
                            } else {
                                serverVideoItem.setProfilePicture(null); // No profile picture
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            serverVideoItem.setProfilePicture(null); // Handle failure
                        }

                        videoDao.insert(serverVideoItem); // Insert video into the local database
                    }).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Update an existing video with new title and description
    public void updateVideo(int videoId, String username, String title, String description) {
        Call<VideoItem> call = webServiceAPI.updateVideo(username, videoId, new VideoUpdate(title, description)); // Make API call to update video
        call.enqueue(new Callback<VideoItem>() {
            @Override
            public void onResponse(Call<VideoItem> call, Response<VideoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VideoItem updatedVideoItem = response.body();
                    updatedVideoItem.setVideoUrl(getFullVideoUrl(updatedVideoItem.getVideoUrl())); // Set full video URL
                    new Thread(() -> videoDao.update(updatedVideoItem)).start(); // Update video in local database
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Delete a video by ID
    public void deleteVideo(int videoId, String username) {
        Call<Void> call = webServiceAPI.deleteVideo(username, videoId); // Make API call to delete video
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> videoDao.deleteById(videoId)).start(); // Delete video from local database
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Increment the view count for a video
    public void incrementViewCount(int videoId) {
        Call<Void> call = webServiceAPI.incrementViewCount(videoId); // Make API call to increment view count
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("VideoAPI", "View count incremented successfully"); // Log success
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Toggle the like status of a video
    public void toggleLike(int videoId, String username) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        Call<Void> call = webServiceAPI.userLiked(videoId, body); // Make API call to toggle like status
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("VideoAPI", "Like toggled successfully"); // Log success
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody()); // Log error if response is unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t); // Log failure if the request fails
            }
        });
    }

    // Inner class to represent video update data
    class VideoUpdate {
        private String title;
        private String description;

        public VideoUpdate(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
