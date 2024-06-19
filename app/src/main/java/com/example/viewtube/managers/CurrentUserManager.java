package com.example.viewtube.managers;

import com.example.viewtube.models.User;

public class CurrentUserManager {
    private static CurrentUserManager instance;
    private UsersManager userManager;

    // Private constructor to prevent instantiation from outside the class
    private CurrentUserManager() {
        userManager = UsersManager.getInstance();
    }

    // Method to get the singleton instance of CurrentUserManager
    public static synchronized CurrentUserManager getInstance() {
        if (instance == null) {
            instance = new CurrentUserManager();
        }
        return instance;
    }

    // Method to get the current logged-in user
    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    // Method to log out the current user
    public void logout() {
        userManager.logoutCurrentUser();
    }
}
