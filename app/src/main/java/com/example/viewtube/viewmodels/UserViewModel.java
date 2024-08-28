package com.example.viewtube.viewmodels;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.entities.User;
import com.example.viewtube.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Boolean> checkUsernameExists(String username) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        userRepository.checkUsernameExists(username, liveData);
        return liveData;
    }

    public LiveData<User> authenticateUser(User user) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        userRepository.authenticateUser(user, liveData);
        return liveData;
    }

    public LiveData<User> registerUser(User user, Context context) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        userRepository.registerUser(user, context, liveData);
        return liveData;
    }


}