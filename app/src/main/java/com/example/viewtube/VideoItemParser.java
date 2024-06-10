
package com.example.viewtube;

import android.content.Context;

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

    public static List<VideoItem> parseVideoItems(InputStream inputStream, Context context) throws IOException, JSONException {
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
            VideoItem videoItem = parseVideoItem(jsonObject, context);
            videoItems.add(videoItem);
        }
        return videoItems;
    }

    private static VideoItem parseVideoItem(JSONObject jsonObject, Context context) throws JSONException {
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String author = jsonObject.getString("author");
        int views = jsonObject.getInt("views");
        int likes = jsonObject.getInt("likes");
        String date = jsonObject.getString("date");
        String duration = jsonObject.getString("duration");
        String videoURL = jsonObject.getString("videoURL");
        String thumbnail = jsonObject.getString("thumbnail");

        // Convert thumbnail string to resource ID
        int thumbnailResId = context.getResources().getIdentifier(thumbnail, "raw", context.getPackageName());

        return new VideoItem(id, title, description, author, views, likes, date, duration, videoURL, thumbnailResId);
    }
}