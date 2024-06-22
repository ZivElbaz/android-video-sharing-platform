package com.example.viewtube;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.viewtube.managers.CurrentUserManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    // UI components
    private EditText titleEditText , descriptionEditText;

    // File descriptor for the selected video
    private FileDescriptor fileDescriptor;

    // Request code for picking a video
    private static final int PICK_VIDEO_REQUEST = 1;

    // URI of the selected video
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize UI components
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        Button selectVideoButton = findViewById(R.id.select_video_button);
        Button uploadButton = findViewById(R.id.upload_button);

        // Set click listener for the select video button
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker(); // Open the file picker to select a video
            }
        });

        // Set click listener for the upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                if (title.isEmpty() || videoUri == null) {
                    // Show a toast if the title or video is not selected
                    Toast.makeText(UploadActivity.this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
                } else {

                    // Create a new VideoItem object with the provided information
                    VideoItem newVideoItem = createfromFile(UploadActivity.this, title, description, fileDescriptor);

                    // Create an Intent to pass back the uploaded video data to HomeActivity
                    Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
                    intent.putExtra("uploadedVideoItem", newVideoItem);
                    setResult(RESULT_OK, intent);
                    finish(); // Finish the UploadActivity and return to HomeActivity
                }
            }
        });
    }

    // Method to open the file picker for selecting a video
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Video selected, get its URI
            videoUri = data.getData();
            fileDescriptor = uriToFileDescriptor(this, videoUri);
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

    // Create a file from a FileDescriptor
    private String createFileFromDescriptor(FileDescriptor fd, Context context) {
        int id = getIntent().getIntExtra("maxId", 10) + 1;
        File outputFile = new File(context.getCacheDir(), "video_" + id + ".mp4");
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

    // Create a VideoItem from a FileDescriptor
    private VideoItem createfromFile(Context context, String title, String description, FileDescriptor fd) {
        String videoPath = createFileFromDescriptor(fd, context);
        if (videoPath == null) {
            Log.e("VideoCreation", "Failed to create video from file descriptor");
            return null;
        }
        int id = getIntent().getIntExtra("maxId", 10) + 1;
        Bitmap thumbnailBitmap = createVideoThumbnail(videoPath);
        String thumbnailPath = null;
        if (thumbnailBitmap != null) {
            // Save the thumbnail bitmap and get its path
            thumbnailPath = saveThumbnail(context, thumbnailBitmap, id);
        }
        String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        return new VideoItem(id, title, description, CurrentUserManager.getInstance().getCurrentUser().getUsername(), 0, 0, currentDate, "", videoPath, thumbnailPath);
    }

    // Create a thumbnail from a video file
    private Bitmap createVideoThumbnail(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(1000000); // Get frame at 1 second
        } catch (IllegalArgumentException e) {
            Log.e("ThumbnailCreation", "Failed to create thumbnail", e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                Log.e("ThumbnailCreation", "Error releasing MediaMetadataRetriever", e);
            }
        }
    }

    // Save the thumbnail to internal storage and return the file path
    private String saveThumbnail(Context context, Bitmap bitmap, int id) {
        File thumbnailFile = new File(context.getFilesDir(), "thumbnail_" + id + ".png");
        try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e("ThumbnailSave", "Error saving thumbnail", e);
            return null;
        }
        return thumbnailFile.getAbsolutePath();
    }
}
