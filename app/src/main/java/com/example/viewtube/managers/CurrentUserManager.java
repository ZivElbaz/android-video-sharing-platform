package com.example.viewtube.managers;

import com.example.viewtube.models.User;

public class CurrentUserManager {
    private static CurrentUserManager instance;
    private UsersManager userManager;

    private CurrentUserManager() {
        userManager = UsersManager.getInstance();
    }

    public static synchronized CurrentUserManager getInstance() {
        if (instance == null) {
            instance = new CurrentUserManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public void logout() {
        userManager.logoutCurrentUser();
    }
}
