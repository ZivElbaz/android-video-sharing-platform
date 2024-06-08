package com.example.viewtube;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    private RecyclerView videoRecyclerView;
    private VideoList videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        videoRecyclerView = findViewById(R.id.video_feed_recycler_view);
        videoList = new VideoList(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(layoutManager);
        videoRecyclerView.setAdapter(videoList);

        // Load video items from JSON file
        try (InputStream inputStream = getResources().openRawResource(R.raw.db)) {
            List<VideoItem> videoItems = VideoItemParser.parseVideoItems(inputStream);
            if (videoItems != null) {
                videoList.setVideoItems(videoItems);
            } else {
                // Handle case where videoItems is null
                Toast.makeText(this, "Failed to load video items", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // Handle IO or JSON exception
            // Show an error message or take appropriate action
            Toast.makeText(this, "Error loading video items", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        // Handle click on a video item
        // Start VideoWatchActivity and pass the video item data
        // For now, let's just display a toast with the video title
        Toast.makeText(this, "Clicked on video: " + videoItem.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
