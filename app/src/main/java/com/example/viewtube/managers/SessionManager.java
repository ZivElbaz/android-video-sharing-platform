package com.example.viewtube.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private Map<String, Integer> likes;
    private Map<String, Boolean> likedVideos;
    private Map<Integer, List<Comment>> videoCommentsMap;

    // Private constructor to prevent instantiation from outside the class
    private SessionManager() {
        likes = new HashMap<>();
        likedVideos = new HashMap<>();
        videoCommentsMap = new HashMap<>();
    }

    // Method to get the singleton instance of SessionManager
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }



    // Method to get the map of comments for all videos
    public Map<Integer, List<Comment>> getVideoCommentsMap() {
        return videoCommentsMap;
    }


    // Inner class to represent a comment along with the username
    public static class Comment {
        private String username;
        private String comment;

        public Comment(String username, String comment) {
            this.username = username;
            this.comment = comment;
        }

        public String getUsername() {
            return username;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }



}
