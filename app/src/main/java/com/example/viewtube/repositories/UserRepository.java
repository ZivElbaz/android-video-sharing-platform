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

public class UserRepository {
    private final UserAPI userAPI;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userAPI = new UserAPI(db.userDao(), context);
    }

    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        userAPI.checkUsernameExists(username, liveData);
    }

    public void registerUser(User user, Context context, MutableLiveData<User> liveData) {
        userAPI.registerUser(user, context, liveData);
    }




    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        userAPI.authenticateUser(user, liveData);
    }

}