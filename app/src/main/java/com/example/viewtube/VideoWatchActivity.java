package com.example.viewtube;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class VideoWatchActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    private TextView videoTitle;
    private TextView videoDescription;
    private RecyclerView recyclerView;
    private RecyclerView commentsRecyclerView;
   // private CommentsAdapter commentsAdapter;
    private VideoList relatedVideos;
    private List<String> comments;
    private EditText commentInput;
    private Button btnLike, btnShare, btnDownload, btnPostComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);

        //initialize the views
        playerView = findViewById(R.id.videoPlayer);
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);
        recyclerView = findViewById(R.id.recyclerView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);
        btnLike = findViewById(R.id.btnLike);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        btnPostComment = findViewById(R.id.btnPostComment);



        //getting the date from the intent
        String videoResourceName = getIntent().getStringExtra("video_resource_name");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");

        videoTitle.setText(title);
        videoDescription.setText(description);

        int videoResourceId = getResources().getIdentifier(videoResourceName, "raw", getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "viewtube"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(videoUri);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);


        // initialize the RecyclerView with related videos
        relatedVideos = new VideoList(this, this); // Pass the context and the listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(relatedVideos);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.db);
            List<VideoItem> videoItems = VideoItemParser.parseVideoItems(inputStream, this);
            relatedVideos.setVideoItems(videoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        String videoResourceName = videoItem.getVideoURL(); // Ensure this is the correct resource name
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        startActivity(moveToWatch);
    }

}