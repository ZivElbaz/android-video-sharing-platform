package com.example.viewtube;

import com.example.viewtube.VideoItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideoItemParser {

    public static List<VideoItem> parseVideoItems(InputStream inputStream) throws IOException, JSONException {
        List<VideoItem> videoItems = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String jsonStr = stringBuilder.toString();
        JSONArray jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            VideoItem videoItem = parseVideoItem(jsonObject);
            videoItems.add(videoItem);
        }
        return videoItems;
    }

    private static VideoItem parseVideoItem(JSONObject jsonObject) throws JSONException {
        // Parse individual video item from JSON object
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String author = jsonObject.getString("author");
        int views = jsonObject.getInt("views");
        int likes = jsonObject.getInt("likes");
        String date = jsonObject.getString("date");
        String duration = jsonObject.getString("duration");
        String videoURL = jsonObject.getString("videoURL");
        return new VideoItem(id, title, description, author, views, likes, date, duration, videoURL);
    }
}
