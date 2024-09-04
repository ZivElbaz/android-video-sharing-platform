package com.example.viewtube.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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

    // UI components and fields
    private EditText titleEditText, descriptionEditText;
    private FileDescriptor fileDescriptor;
    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri videoUri;
    private VideosViewModel videosViewModel;
    private Bitmap thumbnailBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize UI components
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        Button selectVideoButton = findViewById(R.id.select_video_button);
        Button uploadButton = findViewById(R.id.upload_button);

        // Initialize ViewModel
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        // Handle select video button click
        selectVideoButton.setOnClickListener(v -> openFilePicker());

        // Handle upload button click
        uploadButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            if (title.isEmpty() || videoUri == null) {
                Toast.makeText(UploadActivity.this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    uploadVideo(title, description); // Upload video when input is valid
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // Open file picker to select a video
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData(); // Get the video URI
            fileDescriptor = uriToFileDescriptor(this, videoUri); // Convert URI to FileDescriptor
            try {
                thumbnailBitmap = createVideoThumbnail(videoUri); // Generate video thumbnail
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(this, "Video selected: " + videoUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    // Convert URI to FileDescriptor
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

    // Upload video and its metadata
    private void uploadVideo(String title, String description) throws IOException {
        String videoPath = createFileFromDescriptor(fileDescriptor, this); // Create file from descriptor
        if (videoPath == null) {
            Log.e("VideoUpload", "Failed to create video from file descriptor");
            return;
        }

        File videoFile = new File(videoPath);
        File thumbnailFile = saveThumbnail(this, thumbnailBitmap, title); // Save video thumbnail

        if (thumbnailFile == null) {
            Log.e("ThumbnailCreation", "Failed to create or save thumbnail");
            return;
        }

        // Get uploader's username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String uploader = sharedPreferences.getString("username", null);
        String duration = getVideoDuration(videoPath); // Get video duration

        // Create VideoItem object
        VideoItem videoItem = new VideoItem(0, title, description, uploader, 0, 0, null, duration, null, null);
        videosViewModel.addVideoItem(videoItem, videoFile, thumbnailFile); // Add video to ViewModel

        // Return to HomeActivity after uploading
        Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
        setResult(RESULT_OK, intent);
        finish(); // Close UploadActivity
    }

    // Create thumbnail from the selected video
    private Bitmap createVideoThumbnail(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);
            return retriever.getFrameAtTime(1000000); // Get frame at 1 second
        } catch (IllegalArgumentException e) {
            Log.e("ThumbnailCreation", "Failed to create thumbnail", e);
            return null;
        } finally {
            retriever.release();
        }
    }

    // Create video file from a FileDescriptor
    private String createFileFromDescriptor(FileDescriptor fd, Context context) {
        File outputFile = new File(context.getCacheDir(), "video_" + System.currentTimeMillis() + ".mp4");
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
        return outputFile.getAbsolutePath();
    }

    // Save thumbnail image to internal storage
    private File saveThumbnail(Context context, Bitmap bitmap, String title) {
        File thumbnailFile = new File(context.getFilesDir(), "thumbnail_" + title + ".png");
        try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e("ThumbnailSave", "Error saving thumbnail", e);
            return null;
        }
        return thumbnailFile;
    }

    // Get video duration in hours, minutes, and seconds
    private String getVideoDuration(String videoPath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
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
        } finally {
            retriever.release();
        }
    }
}
