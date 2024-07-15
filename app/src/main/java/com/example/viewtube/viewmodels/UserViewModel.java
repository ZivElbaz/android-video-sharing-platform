package com.example.viewtube.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.repositories.UserRepository;

import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<User> userLiveData;
    private LiveData<User> authenticatedUserLiveData;
    private LiveData<Boolean> deleteUserLiveData;
    private LiveData<Boolean> updatePasswordLiveData;
    private LiveData<List<VideoItem>> videosByUploaderLiveData;
    private LiveData<String> profilePictureLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userLiveData = userRepository.getUserLiveData();
        authenticatedUserLiveData = userRepository.getAuthenticatedUserLiveData();
        deleteUserLiveData = userRepository.getDeleteUserLiveData();
        updatePasswordLiveData = userRepository.getUpdatePasswordLiveData();
        videosByUploaderLiveData = userRepository.getVideosByUploaderLiveData();
        profilePictureLiveData = userRepository.getProfilePictureLiveData();
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
        userRepository.createUser(user);
    }

    public void getUser(String username) {
        userRepository.getUser(username);
    }

    public void authenticateUser(User user) {
        userRepository.authenticateUser(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
    }

    public void updateUser(String username, User user) {
        userRepository.updateUser(username, user);
    }

    public void updatePassword(Map<String, String> body) {
        userRepository.updatePassword(body);
    }

    public void getVideosByUploader(String uploader) {
        userRepository.getVideosByUploader(uploader);
    }

    // Method to fetch the profile picture of the logged-in user


    // Method to fetch the profile picture of a user by username without LiveData
    public void getProfilePictureByUsername(String username, Callback<ProfilePictureResponse> callback) {
        userRepository.getProfilePictureByUsername(username, callback);
    }
}
