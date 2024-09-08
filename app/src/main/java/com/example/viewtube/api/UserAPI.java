package com.example.viewtube.api;

import static com.example.viewtube.activities.MainActivity.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.UserDao;
import com.example.viewtube.entities.AuthResponse;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.UsernameCheckResponse;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {

    private static final String BASE_URL = Config.getBaseUrl(); // Base URL for API calls
    private WebServiceAPI webServiceAPI; // Web service interface

    // Constructor to initialize Retrofit and create WebServiceAPI
    public UserAPI(UserDao userDao, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "api/") // Appending "api/" to base URL
                .addConverterFactory(GsonConverterFactory.create()) // Gson for JSON parsing
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class); // Create WebServiceAPI instance
    }

    // Register a new user with the API
    public void registerUser(User user, Context context, MutableLiveData<User> liveData) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", user.getUsername());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("password", user.getPassword());
        userMap.put("image", user.getImage());

        // Make the API call to register a new user
        Call<AuthResponse> call = webServiceAPI.createUser(userMap);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    new Thread(() -> {
                        // Post the value to LiveData on the main thread
                        liveData.postValue(authResponse.getUser());

                        // Save the token locally
                        saveToken(authResponse.getToken(), context);
                    }).start();
                } else {
                    liveData.postValue(null); // Registration failed
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                liveData.postValue(null); // API request failed
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    // Authenticate user with username and password
    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        Call<AuthResponse> call = webServiceAPI.authenticateUser(user);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    new Thread(() -> {
                        // Post user details to LiveData on the main thread
                        liveData.postValue(authResponse.getUser());

                        // Save authentication token locally
                        saveToken(authResponse.getToken(), context);
                    }).start();
                } else {
                    liveData.postValue(null); // Authentication failed
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                liveData.postValue(null); // API request failed
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    // Save the authentication token to SharedPreferences
    public void saveToken(String token, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwtToken", token); // Save token
        editor.apply(); // Apply changes
    }

    // Check if a username already exists in the system
    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        Call<UsernameCheckResponse> call = webServiceAPI.checkUsernameExists(username);
        call.enqueue(new Callback<UsernameCheckResponse>() {
            @Override
            public void onResponse(Call<UsernameCheckResponse> call, Response<UsernameCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().isExists()); // Post result to LiveData
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                    liveData.postValue(false); // Username does not exist
                }
            }

            @Override
            public void onFailure(Call<UsernameCheckResponse> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
                liveData.postValue(false); // Request failed
            }
        });
    }

    // Retrieve videos uploaded by a specific username
    public void getVideosByUsername(String username, MutableLiveData<List<VideoItem>> liveData) {
        Call<List<VideoItem>> call = webServiceAPI.getVideosByUsername(username);
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<VideoItem> videoItems = response.body();
                        for (VideoItem videoItem : videoItems) {
                            // Set full video URL and thumbnail
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
                                        base64Image = base64Image.substring(23); // Remove base64 prefix
                                    }
                                    videoItem.setProfilePicture(base64Image);
                                } else {
                                    videoItem.setProfilePicture(null);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                videoItem.setProfilePicture(null); // Default to null if fetch fails
                            }
                        }
                        // Post video items to LiveData
                        liveData.postValue(videoItems);
                    }).start();
                } else {
                    Log.e("VideoAPI", "Failed to fetch videos: " + response.errorBody());
                    liveData.postValue(null); // Post null if fetch fails
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Log.e("VideoAPI", "Request failed", t);
                liveData.postValue(null); // Request failed
            }
        });
    }

    // Retrieve user data based on the username
    public void getUserData(String username, MutableLiveData<User> liveData) {
        Call<User> call = webServiceAPI.getUserData(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body()); // Post user data to LiveData
                } else {
                    Log.e("UserAPI", "Failed to fetch user data: " + response.errorBody());
                    liveData.postValue(null); // Fetch failed
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
                liveData.postValue(null); // Request failed
            }
        });
    }

    // Update user details (name, profile picture, etc.)
    public void updateUserData(String username, String firstName, String lastName, String image, MutableLiveData<User> liveData) {
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("image", image);

        Call<User> call = webServiceAPI.updateUser(username, userData);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    new Thread(() -> {
                        // Post updated user data to LiveData
                        liveData.postValue(updatedUser);
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

    // Update user password
    public void updateUserPassword(String username, String currentPassword, String newPassword, MutableLiveData<Boolean> liveData) {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("username", username);
        passwordData.put("currentPassword", currentPassword);
        passwordData.put("newPassword", newPassword);

        Call<AuthResponse> call = webServiceAPI.updatePassword(passwordData);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(true); // Password changed successfully
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                    liveData.postValue(false); // password change failed
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
            }
        });
    }

    // Delete a user account
    public void deleteUser(String username, String password, MutableLiveData<Boolean> liveData) {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("password", password);

        Call<Void> call = webServiceAPI.deleteUser(username, passwordData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(true); // Account deletion successful
                } else {
                    Log.e("UserAPI", "Response error: " + response.errorBody());
                    liveData.postValue(false); // Deletion failed
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserAPI", "Request failed", t);
                liveData.postValue(false); // Request failed
            }
        });
    }

    // Utility method to construct full video URLs
    private String getFullVideoUrl(String relativeUrl) {
        return BASE_URL + relativeUrl; // Combine base URL with the relative video URL
    }
}
