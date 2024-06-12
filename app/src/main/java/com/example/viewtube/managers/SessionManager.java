package com.example.viewtube.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private Map<String, Integer> likes;
    private Map<String, Boolean> likedVideos;
    private Map<String, List<String>> comments;

    private SessionManager() {
        likes = new HashMap<>();
        likedVideos = new HashMap<>();
        comments = new HashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getLikes(String videoId) {
        if(likes.containsKey(videoId))
            return likes.get(videoId);
        return 0;
    }

    public void setLikes(String videoId, int likesCount) {
        likes.put(videoId, likesCount);
    }

    public boolean isLiked(String videoId) {
        if(likedVideos.containsKey(videoId))
            return likedVideos.get(videoId);
        return false;
    }

    public void setLiked(String videoId, boolean isLiked) {
        likedVideos.put(videoId, isLiked);
    }

    public List<String> getComments(String videoId) {
        if(comments.containsKey(videoId))
            return comments.get(videoId);
        return new ArrayList<>();
    }

    public void addComment(String videoId, String comment) {
        if (!comments.containsKey(videoId)) {
            comments.put(videoId, new ArrayList<>());
        }
        comments.get(videoId).add(comment);
    }
}
