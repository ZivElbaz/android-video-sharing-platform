package com.example.viewtube.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.viewtube.api.UserAPI;
import com.example.viewtube.data.AppDB;
import com.example.viewtube.entities.TokenRequest;
import com.example.viewtube.entities.User;

public class UserRepository {
    private final UserAPI userAPI;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<User> authenticatedUserLiveData;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userAPI = new UserAPI(db.userDao(), context);
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        authenticatedUserLiveData = new MutableLiveData<>();
    }

    public void checkUsernameExists(String username, MutableLiveData<Boolean> liveData) {
        userAPI.checkUsernameExists(username, liveData);
    }

    public void authenticateUser(User user, MutableLiveData<User> liveData) {
        userAPI.authenticateUser(user, new MutableLiveData<User>() {
            @Override
            public void postValue(User authenticatedUser) {
                // Save authenticated user to shared preferences or keep it in memory
                if (authenticatedUser != null) {
                    authenticatedUserLiveData.postValue(authenticatedUser);
                }
                liveData.postValue(authenticatedUser);
            }
        });
    }


    public void verifyToken(MutableLiveData<User> liveData) {
        String token = sharedPreferences.getString("jwtToken", null);
        if (token != null) {
            // Create a TokenRequest object to send the token in the body
            TokenRequest tokenRequest = new TokenRequest(token);
            userAPI.verifyToken(tokenRequest, liveData);
        } else {
            liveData.postValue(null);
        }
    }





    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwtToken");
        editor.apply();
    }

    public LiveData<User> getAuthenticatedUserLiveData() {
        if (authenticatedUserLiveData.getValue() == null) {
            verifyToken(authenticatedUserLiveData);
        }
        return authenticatedUserLiveData;
    }


    public void logoutUser() {
        clearToken(); // Clear the token
        authenticatedUserLiveData.setValue(null); // Clear the authenticated user LiveData
    }
}