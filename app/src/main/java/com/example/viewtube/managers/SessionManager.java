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

    private SessionManager() {
        likes = new HashMap<>();
        likedVideos = new HashMap<>();
        videoCommentsMap = new HashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getLikes(String videoId) {
        Integer likeCount = likes.get(videoId);
        return likeCount != null ? likeCount : 0;
    }

    public void setLikes(String videoId, int likesCount) {
        likes.put(videoId, likesCount);
    }

    public boolean isLiked(String videoId) {
        Boolean isLiked = likedVideos.get(videoId);
        return isLiked != null ? isLiked : false;
    }

    public void setLiked(String videoId, boolean isLiked) {
        likedVideos.put(videoId, isLiked);
    }

    public Map<Integer, List<String>> getVideoCommentsMap() {
        return videoCommentsMap;
    }

    public List<String> getComments(int videoId) {
        List<String> comments = videoCommentsMap.get(videoId);
        return comments != null ? comments : new ArrayList<>();
    }

    public void addComment(int videoId, String comment) {
        List<String> comments = videoCommentsMap.get(videoId);
        if (comments == null) {
            comments = new ArrayList<>();
            videoCommentsMap.put(videoId, comments);
        }
        comments.add(comment);
    }
}
