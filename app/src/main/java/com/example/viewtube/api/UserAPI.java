package com.example.viewtube.api;

import static com.example.viewtube.activities.MainActivity.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.UserDao;
import com.example.viewtube.entities.AuthResponse;
import com.example.viewtube.entities.TokenRequest;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.UsernameCheckResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {

    private static final String BASE_URL = "http://10.0.0.5:12345/";
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
                    liveData.postValue(response.body());
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
                    liveData.postValue(response.body());
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
        Call<AuthResponse> call = webServiceAPI.authenticateUser(user);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    new Thread(() -> {
                        // Check if the user already exists in the database
                        User existingUser = userDao.getUserByUsername(authResponse.getUser().getUsername());

                        if (existingUser != null) {
                            // Update the existing user
                            userDao.update(authResponse.getUser());
                        } else {
                            // Insert the new user
                            userDao.insert(authResponse.getUser());
                        }

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


    public void verifyToken(TokenRequest tokenRequest, MutableLiveData<User> liveData) {
        Call<AuthResponse> call = webServiceAPI.verifyToken(tokenRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    User user = authResponse.getUser();
                    Log.d("UserAPI", "User received: " + (user != null ? user.getUsername() : "null"));
                    liveData.postValue(user);
                } else {
                    Log.e("UserAPI", "Response error or body is null");
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
                liveData.postValue(null);
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
