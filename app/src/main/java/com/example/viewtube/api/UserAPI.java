package com.example.viewtube.api;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.UserDao;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;

import java.util.List;
import java.util.Map;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;

public class UserAPI {

    private static final String BASE_URL = "http://192.168.1.100:12345/";
    private WebServiceAPI webServiceAPI;
    private UserDao userDao;

    public UserAPI(UserDao userDao, Context context) {
        this.userDao = userDao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void createUser(User user, MutableLiveData<User> liveData) {
        Call<User> call = webServiceAPI.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        userDao.insert(response.body());
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void getUser(String username, MutableLiveData<User> liveData) {
        Call<User> call = webServiceAPI.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        userDao.insert(response.body());
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        Call<User> call = webServiceAPI.authenticateUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        userDao.insert(response.body());
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        Call<Boolean> call = webServiceAPI.checkUsernameExists(username);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }


    public void getProfilePictureByUsername(String username, Callback<ProfilePictureResponse> callback) {
        Call<ProfilePictureResponse> call = webServiceAPI.getPictureByUsername(username);
        call.enqueue(callback);
    }



    public void updateUser(String username, User user, MutableLiveData<User> liveData) {
        Call<User> call = webServiceAPI.updateUser(username, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        userDao.update(response.body());
                        liveData.postValue(response.body());
                    }).start();
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void deleteUser(String username, MutableLiveData<Boolean> liveData) {
        Call<Void> call = webServiceAPI.deleteUser(username);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        userDao.deleteByUsername(username);
                        liveData.postValue(true);
                    }).start();
                } else {
                    liveData.postValue(false);
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.postValue(false);
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void updatePassword(Map<String, String> body, MutableLiveData<Boolean> liveData) {
        Call<Void> call = webServiceAPI.updatePassword(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(true);
                } else {
                    liveData.postValue(false);
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.postValue(false);
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void getVideosByUploader(String uploader, MutableLiveData<List<VideoItem>> liveData) {
        Call<List<VideoItem>> call = webServiceAPI.getVideosByUploader(uploader);
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }
}
