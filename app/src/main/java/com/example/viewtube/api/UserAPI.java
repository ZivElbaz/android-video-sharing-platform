package com.example.viewtube.api;

import static com.example.viewtube.activities.MainActivity.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.UserDao;
import com.example.viewtube.entities.AuthResponse;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.TokenRequest;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.UsernameCheckResponse;
import com.example.viewtube.entities.VideoItem;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    public void registerUser(User user, Context context, MutableLiveData<User> liveData) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", user.getUsername());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("password", user.getPassword());
        userMap.put("image", user.getImage());


        Call<AuthResponse> call = webServiceAPI.createUser(userMap);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    new Thread(() -> {
                        // Post the value to LiveData on the main thread
                        liveData.postValue(authResponse.getUser());

                        // Save the token
                        saveToken(authResponse.getToken(), context);
                    }).start();

                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                liveData.postValue(null);
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        Call<AuthResponse> call = webServiceAPI.authenticateUser(user);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    new Thread(() -> {

                        // Post the value to LiveData on the main thread
                        liveData.postValue(authResponse.getUser());

                        // Save the token
                        saveToken(authResponse.getToken(), context);
                    }).start();

                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                liveData.postValue(null);
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }


    public void saveToken(String token, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwtToken", token);
        editor.apply();
    }

    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        Call<UsernameCheckResponse> call = webServiceAPI.checkUsernameExists(username);
        call.enqueue(new Callback<UsernameCheckResponse>() {
            @Override
            public void onResponse(Call<UsernameCheckResponse> call, Response<UsernameCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().isExists());
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                    liveData.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<UsernameCheckResponse> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
                liveData.postValue(false);
            }
        });
    }

}
