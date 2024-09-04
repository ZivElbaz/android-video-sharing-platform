package com.example.viewtube.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.UserAPI;
import com.example.viewtube.data.AppDB;
import com.example.viewtube.entities.TokenRequest;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;

import java.util.List;

public class UserRepository {
    private final UserAPI userAPI;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userAPI = new UserAPI(db.userDao(), context);
    }

    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        userAPI.checkUsernameExists(username, liveData);
    }

    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        userAPI.authenticateUser(user, liveData);
    }

    public void registerUser(User user, Context context, MutableLiveData<User> liveData) {
        userAPI.registerUser(user, context, liveData);
    }

    public LiveData<List<VideoItem>> getVideosByUsername(String username, MutableLiveData<List<VideoItem>> userVideosLiveData) {
        userAPI.getVideosByUsername(username, userVideosLiveData);
        return userVideosLiveData;
    }

    public void getUserData(String username, MutableLiveData<User> liveData) {
        userAPI.getUserData(username, liveData);
    }

    public void updateUserData(String username, String firstName, String lastName, String image, MutableLiveData<User> liveData) {
        userAPI.updateUserData(username, firstName, lastName, image, liveData);
    }

    public void updateUserPassword(String username, String currentPassword, String newPassword, MutableLiveData<Boolean> liveData) {
        userAPI.updateUserPassword(username, currentPassword, newPassword);
    }

    public void deleteUser(String username, String password, MutableLiveData<Boolean> liveData) {
        userAPI.deleteUser(username, password, liveData);
    }
}