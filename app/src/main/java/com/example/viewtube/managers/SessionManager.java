package com.example.viewtube.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private Map<String, Integer> likes;
    private Map<String, Boolean> likedVideos;
    private Map<Integer, List<String>> videoCommentsMap;

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

    // Method to get the number of likes for a specific video
    public int getLikes(String videoId) {
        Integer likeCount = likes.get(videoId);
        return likeCount != null ? likeCount : 0;
    }

    // Method to set the number of likes for a specific video
    public void setLikes(String videoId, int likesCount) {
        likes.put(videoId, likesCount);
    }

    // Method to check if a specific video is liked
    public boolean isLiked(String videoId) {
        Boolean isLiked = likedVideos.get(videoId);
        return isLiked != null ? isLiked : false;
    }

    // Method to set the liked status of a specific video
    public void setLiked(String videoId, boolean isLiked) {
        likedVideos.put(videoId, isLiked);
    }

    // Method to get the map of comments for all videos
    public Map<Integer, List<String>> getVideoCommentsMap() {
        return videoCommentsMap;
    }

    // Method to get the comments for a specific video
    public List<String> getComments(int videoId) {
        List<String> comments = videoCommentsMap.get(videoId);
        return comments != null ? comments : new ArrayList<>();
    }

    // Method to add a comment to a specific video
    public void addComment(int videoId, String comment) {
        List<String> comments = videoCommentsMap.get(videoId);
        if (comments == null) {
            comments = new ArrayList<>();
            videoCommentsMap.put(videoId, comments);
        }
        comments.add(comment);
    }
}
