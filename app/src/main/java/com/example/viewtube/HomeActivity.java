package com.example.viewtube;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.R;
import com.example.viewtube.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView videoFeedRecyclerView;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        videoFeedRecyclerView = findViewById(R.id.video_feed_recycler_view);
        videoAdapter = new VideoAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoFeedRecyclerView.setLayoutManager(layoutManager);
        videoFeedRecyclerView.setAdapter(videoAdapter);

        // Populate the video feed with dummy data (replace this with your actual video data)
        List<VideoItem> videoItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            videoItems.add(new VideoItem("Video " + (i + 1), "https://example.com/video_" + (i + 1)));
        }
        videoAdapter.setVideoItems(videoItems);
    }
}
