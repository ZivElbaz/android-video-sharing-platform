package com.example.viewtube.managers;

import com.example.viewtube.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersManager {
    private static UsersManager instance;
    private List<User> users;
    private User currentUser;

    private UsersManager() {
        users = new ArrayList<>();
    }

    public static synchronized UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logoutCurrentUser() {
        currentUser = null;
    }
}
