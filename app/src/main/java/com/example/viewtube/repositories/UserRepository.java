package com.example.viewtube.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.data.AppDB;
import com.example.viewtube.data.UserDao;
import com.example.viewtube.api.UserAPI;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;

import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class UserRepository {
    private UserDao userDao;
    private UserAPI userAPI;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<User> authenticatedUserLiveData;
    private MutableLiveData<Boolean> deleteUserLiveData;
    private MutableLiveData<Boolean> updatePasswordLiveData;
    private MutableLiveData<List<VideoItem>> videosByUploaderLiveData;
    private MutableLiveData<String> profilePictureLiveData;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userDao = db.userDao();
        userAPI = new UserAPI(userDao, context);
        userLiveData = new MutableLiveData<>();
        authenticatedUserLiveData = new MutableLiveData<>();
        deleteUserLiveData = new MutableLiveData<>();
        updatePasswordLiveData = new MutableLiveData<>();
        videosByUploaderLiveData = new MutableLiveData<>();
        profilePictureLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
    }

    public LiveData<Boolean> getDeleteUserLiveData() {
        return deleteUserLiveData;
    }

    public LiveData<Boolean> getUpdatePasswordLiveData() {
        return updatePasswordLiveData;
    }

    public LiveData<List<VideoItem>> getVideosByUploaderLiveData() {
        return videosByUploaderLiveData;
    }

    public LiveData<String> getProfilePictureLiveData() {
        return profilePictureLiveData;
    }

    public void createUser(User user) {
        userAPI.createUser(user, userLiveData);
        userLiveData.observeForever(createdUser -> {
            if (createdUser != null) {
                new Thread(() -> userDao.insert(createdUser)).start();
            }
        });
    }

    public void getUser(String username) {
        userAPI.getUser(username, userLiveData);
        userLiveData.observeForever(fetchedUser -> {
            if (fetchedUser != null) {
                new Thread(() -> userDao.insert(fetchedUser)).start();
            }
        });
    }

    public void authenticateUser(User user) {
        userAPI.authenticateUser(user, authenticatedUserLiveData);
        authenticatedUserLiveData.observeForever(authenticatedUser -> {
            if (authenticatedUser != null) {
                new Thread(() -> userDao.insert(authenticatedUser)).start();
            }
        });
    }

    public void deleteUser(String username) {
        userAPI.deleteUser(username, deleteUserLiveData);
        deleteUserLiveData.observeForever(deleted -> {
            if (deleted != null && deleted) {
                new Thread(() -> userDao.deleteByUsername(username)).start();
            }
        });
    }

    public void updateUser(String username, User user) {
        userAPI.updateUser(username, user, userLiveData);
        userLiveData.observeForever(updatedUser -> {
            if (updatedUser != null) {
                new Thread(() -> userDao.update(updatedUser)).start();
            }
        });
    }

    public void updatePassword(Map<String, String> body) {
        userAPI.updatePassword(body, updatePasswordLiveData);
    }

    public void getVideosByUploader(String uploader) {
        userAPI.getVideosByUploader(uploader, videosByUploaderLiveData);
    }

    // Method to fetch the profile picture of the logged-in user


    // Method to fetch the profile picture of a user by username without LiveData
    public void getProfilePictureByUsername(String username, Callback<ProfilePictureResponse> callback) {
        userAPI.getProfilePictureByUsername(username, callback);
    }
}
