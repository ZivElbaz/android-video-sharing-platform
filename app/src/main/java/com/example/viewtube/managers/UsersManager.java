package com.example.viewtube.managers;

import com.example.viewtube.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersManager {
    private static UsersManager instance;
    private List<User> users;
    private User currentUser;

    // Private constructor to prevent instantiation from outside the class
    private UsersManager() {
        users = new ArrayList<>();
    }

    // Method to get the singleton instance of UsersManager
    public static synchronized UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }

    // Method to add a user to the list
    public void addUser(User user) {
        users.add(user);
    }

    // Method to get a user by username
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Method to authenticate a user with username and password
    public boolean authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    // Method to set the current user
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    // Method to get the current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Method to log out the current user
    public void logoutCurrentUser() {
        currentUser = null;
    }
}
