package com.example.viewtube.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.UserAPI;
import com.example.viewtube.data.AppDB;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;

import java.util.List;

public class UserRepository {
    private final UserAPI userAPI;

    // Constructor initializes the UserAPI with the database instance
    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userAPI = new UserAPI(db.userDao(), context);
    }

    // Checks if the username exists by calling the UserAPI and updating the LiveData
    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        userAPI.checkUsernameExists(username, liveData);
    }

    // Authenticates the user by calling the UserAPI and updating the LiveData
    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        userAPI.authenticateUser(user, liveData);
    }

    // Registers a new user by calling the UserAPI and updating the LiveData
    public void registerUser(User user, Context context, MutableLiveData<User> liveData) {
        userAPI.registerUser(user, context, liveData);
    }

    // Fetches videos uploaded by a specific user and updates the LiveData
    public LiveData<List<VideoItem>> getVideosByUsername(String username, MutableLiveData<List<VideoItem>> userVideosLiveData) {
        userAPI.getVideosByUsername(username, userVideosLiveData);
        return userVideosLiveData;
    }

    // Fetches user data by calling the UserAPI and updating the LiveData
    public void getUserData(String username, MutableLiveData<User> liveData) {
        userAPI.getUserData(username, liveData);
    }

    // Updates user data (first name, last name, profile picture) and updates the LiveData
    public void updateUserData(String username, String firstName, String lastName, String image, MutableLiveData<User> liveData) {
        userAPI.updateUserData(username, firstName, lastName, image, liveData);
    }

    // Updates the user's password by calling the UserAPI
    public void updateUserPassword(String username, String currentPassword, String newPassword, MutableLiveData<Boolean> liveData) {
        userAPI.updateUserPassword(username, currentPassword, newPassword, liveData);
    }

    // Deletes the user by calling the UserAPI and updates the LiveData
    public void deleteUser(String username, String password, MutableLiveData<Boolean> liveData) {
        userAPI.deleteUser(username, password, liveData);
    }
}
