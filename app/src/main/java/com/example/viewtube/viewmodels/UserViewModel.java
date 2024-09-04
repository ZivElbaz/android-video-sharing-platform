package com.example.viewtube.viewmodels;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    // Constructor initializes the UserRepository
    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    // Checks if the username exists and returns a LiveData<Boolean>
    public LiveData<Boolean> checkUsernameExists(String username) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        userRepository.checkUsernameExists(username, liveData);
        return liveData;
    }

    // Authenticates the user and returns a LiveData<User> containing user information
    public LiveData<User> authenticateUser(User user) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        userRepository.authenticateUser(user, liveData);
        return liveData;
    }

    // Registers a new user and returns a LiveData<User> containing user information
    public LiveData<User> registerUser(User user, Context context) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        userRepository.registerUser(user, context, liveData);
        return liveData;
    }

    // Fetches videos uploaded by a specific user and returns LiveData<List<VideoItem>>
    public LiveData<List<VideoItem>> getVideosByUsername(String username) {
        MutableLiveData<List<VideoItem>> userVideosLiveData = new MutableLiveData<>();
        return userRepository.getVideosByUsername(username, userVideosLiveData);
    }

    // Fetches user data by username and returns LiveData<User>
    public LiveData<User> getUserData(String username) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        userRepository.getUserData(username, userLiveData);
        return userLiveData;
    }

    // Updates the user's profile information and returns LiveData<User>
    public LiveData<User> updateUserData(String username, String firstName, String lastName, String image) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        userRepository.updateUserData(username, firstName, lastName, image, liveData);
        return liveData;
    }

    // Updates the user's password and returns LiveData<Boolean> indicating success or failure
    public LiveData<Boolean> updateUserPassword(String username, String currentPassword, String newPassword) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        userRepository.updateUserPassword(username, currentPassword, newPassword, liveData);
        return liveData;
    }

    // Deletes a user account and returns LiveData<Boolean> indicating success or failure
    public LiveData<Boolean> deleteUser(String username, String password) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        userRepository.deleteUser(username, password, liveData);
        return liveData;
    }
}
