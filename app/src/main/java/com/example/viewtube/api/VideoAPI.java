package com.example.viewtube.api;

import android.content.Context;
import android.util.Log;

import com.example.viewtube.data.VideoDao;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.VideoItem;

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

    private static final String BASE_URL = "http://192.168.1.100:12345/";

    private WebServiceAPI webServiceAPI;
    private VideoDao videoDao;

    public VideoAPI(VideoDao dao, Context context) {
        this.videoDao = dao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    private String getFullVideoUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void getAll() {
        Call<List<VideoItem>> call = webServiceAPI.getAllVideos();
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<VideoItem> videoItems = response.body();
                        for (VideoItem videoItem : videoItems) {
                            videoItem.setVideoUrl(getFullVideoUrl(videoItem.getVideoUrl()));
                            videoItem.setThumbnail(getFullVideoUrl(videoItem.getThumbnail()));
                            videoItem.setTimestamp(System.currentTimeMillis());

                            // Fetch profile picture for each video item
                            Call<ProfilePictureResponse> pictureCall = webServiceAPI.getPictureByUsername(videoItem.getUploader());
                            try {
                                Response<ProfilePictureResponse> pictureResponse = pictureCall.execute();
                                if (pictureResponse.isSuccessful() && pictureResponse.body() != null) {
                                    String base64Image = pictureResponse.body().getProfilePicture();
                                    if (base64Image != null && base64Image.startsWith("data:image/jpeg;base64,")) {
                                        base64Image = base64Image.substring(23);  // Remove the prefix
                                    }
                                    videoItem.setProfilePicture(base64Image);
                                } else {
                                    videoItem.setProfilePicture(null);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                videoItem.setProfilePicture(null);
                            }
                        }
                        videoDao.clear();
                        videoDao.insertAll(videoItems);
                    }).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }


    public void getVideo(int videoId) {
        Call<VideoItem> call = webServiceAPI.getVideo(videoId);
        call.enqueue(new Callback<VideoItem>() {
            @Override
            public void onResponse(Call<VideoItem> call, Response<VideoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VideoItem videoItem = response.body();
                    videoItem.setVideoUrl(getFullVideoUrl(videoItem.getVideoUrl()));
                    videoItem.setThumbnail(getFullVideoUrl(videoItem.getThumbnail()));

                    // Fetch profile picture for the video item
                    Call<ProfilePictureResponse> pictureCall = webServiceAPI.getPictureByUsername(videoItem.getUploader());
                    pictureCall.enqueue(new Callback<ProfilePictureResponse>() {
                        @Override
                        public void onResponse(Call<ProfilePictureResponse> call, Response<ProfilePictureResponse> pictureResponse) {
                            if (pictureResponse.isSuccessful() && pictureResponse.body() != null) {
                                String base64Image = pictureResponse.body().getProfilePicture();
                                if (base64Image != null && base64Image.startsWith("data:image/jpeg;base64,")) {
                                    base64Image = base64Image.substring(23);  // Remove the prefix
                                }
                                videoItem.setProfilePicture(base64Image);
                            } else {
                                videoItem.setProfilePicture(null); // or a default value if you prefer
                            }

                            new Thread(() -> {
                                // Check if the video item already exists
                                VideoItem existingVideoItem = videoDao.getVideoItemSync(videoItem.getId());
                                if (existingVideoItem == null) {
                                    videoDao.insert(videoItem);
                                } else {
                                    videoDao.update(videoItem);
                                }
                            }).start();
                        }

                        @Override
                        public void onFailure(Call<ProfilePictureResponse> call, Throwable t) {
                            videoItem.setProfilePicture(null); // or a default value if you prefer

                            new Thread(() -> {
                                // Check if the video item already exists
                                VideoItem existingVideoItem = videoDao.getVideoItemSync(videoItem.getId());
                                if (existingVideoItem == null) {
                                    videoDao.insert(videoItem);
                                } else {
                                    videoDao.update(videoItem);
                                }
                            }).start();
                        }
                    });
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }

    public void add(VideoItem videoItem, File videoFile, File thumbnailFile) {
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("videoFile", videoFile.getName(), videoRequestBody);

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
                    VideoItem serverVideoItem = response.body();
                    serverVideoItem.setVideoUrl(getFullVideoUrl(serverVideoItem.getVideoUrl()));
                    new Thread(() -> videoDao.insert(serverVideoItem)).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }



    public void update(int videoId, String title, String description) {
        Call<VideoItem> call = webServiceAPI.updateVideo(videoId, new VideoUpdate(title, description));
        call.enqueue(new Callback<VideoItem>() {
            @Override
            public void onResponse(Call<VideoItem> call, Response<VideoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VideoItem updatedVideoItem = response.body();
                    updatedVideoItem.setVideoUrl(getFullVideoUrl(updatedVideoItem.getVideoUrl()));
                    new Thread(() -> videoDao.update(updatedVideoItem)).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }

    public void delete(int videoId) {
        Call<Void> call = webServiceAPI.deleteVideo(videoId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> videoDao.deleteById(videoId)).start();
                } else {
                    Log.e("VideoAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }

    public void userLiked(int videoId, String username) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        Call<Void> call = webServiceAPI.userLiked(videoId, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Handle response
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
            }
        });
    }

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