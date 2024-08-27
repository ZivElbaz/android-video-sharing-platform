package com.example.viewtube.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.viewtube.R;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.viewmodels.VideosViewModel;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;



public class UploadActivity extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText;
    private FileDescriptor fileDescriptor;
    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri videoUri;
    private VideosViewModel videosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        Button selectVideoButton = findViewById(R.id.select_video_button);
        Button uploadButton = findViewById(R.id.upload_button);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        selectVideoButton.setOnClickListener(v -> openFilePicker());
        uploadButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            if (title.isEmpty() || videoUri == null) {
                Toast.makeText(UploadActivity.this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
            } else {
                uploadVideo(title, description);
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            fileDescriptor = uriToFileDescriptor(this, videoUri);
            Toast.makeText(this, "Video selected: " + videoUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    public static FileDescriptor uriToFileDescriptor(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        try {
            AssetFileDescriptor assetFileDescriptor = resolver.openAssetFileDescriptor(uri, "r");
            if (assetFileDescriptor != null) {
                return assetFileDescriptor.getFileDescriptor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createFileFromDescriptor(FileDescriptor fd, String title, Context context) {
        File outputFile = new File(context.getCacheDir(), "video_" + title + ".mp4");
        try (FileInputStream fis = new FileInputStream(fd);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buf = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buf)) > 0) {
                fos.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            Log.e("VideoCreation", "Error creating video file", e);
            return null;
        }
        Log.i("VideoCreation", "File created at " + outputFile.getAbsolutePath());
        return outputFile.getAbsolutePath();
    }

    private void uploadVideo(String title, String description) {
        String videoPath = createFileFromDescriptor(fileDescriptor, title, this);
        if (videoPath == null) {
            Log.e("VideoUpload", "Failed to create video from file descriptor");
            return;
        }

        File videoFile = new File(videoPath);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String uploader = sharedPreferences.getString("username", null);
        String duration = getVideoDuration(videoPath);

        // Create a temporary VideoItem without id, date, videoUrl, and thumbnail
        VideoItem videoItem = new VideoItem(0, title, description, uploader, 0, 0, null, duration, null, null);
        videosViewModel.addVideoItem(videoItem, videoFile);
    }

    // Method to calculate video duration
    private String getVideoDuration(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time == null) {
                return "00:00";
            }

            long timeInMillisec = Long.parseLong(time);
            long duration = timeInMillisec / 1000;
            long hours = duration / 3600;
            long minutes = (duration % 3600) / 60;
            long seconds = duration % 60;

            if (hours > 0) {
                return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            return "00:00";
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
