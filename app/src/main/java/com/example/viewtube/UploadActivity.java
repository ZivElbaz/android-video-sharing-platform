package com.example.viewtube;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.managers.CurrentUserManager;

public class UploadActivity extends AppCompatActivity {

    private EditText titleEditText;
    private Button selectVideoButton;
    private Button uploadButton;

    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        titleEditText = findViewById(R.id.title_edit_text);
        selectVideoButton = findViewById(R.id.select_video_button);
        uploadButton = findViewById(R.id.upload_button);

        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();

                if (title.isEmpty() || videoUri == null) {
                    Toast.makeText(UploadActivity.this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new VideoItem object with the provided information
                    VideoItem newVideoItem = createNewVideoItem(title, videoUri.toString());

                    // Create an Intent to pass back the uploaded video data to HomeActivity
                    Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
                    intent.putExtra("uploadedVideoItem", newVideoItem);
                    setResult(RESULT_OK, intent);
                    finish(); // Finish the UploadActivity and return to HomeActivity
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Video selected, get its URI
            videoUri = data.getData();
            Toast.makeText(this, "Video selected: " + videoUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private VideoItem createNewVideoItem(String title, String videoUrl) {
        // Create a new VideoItem object with the provided information
        // Here you can set additional attributes like thumbnail, views, likes, etc.
        // For now, I'll just set a default thumbnail resource ID
        int thumbnailResId = R.drawable.ic_home_background;

        // Create and return the new VideoItem object
        int id = getIntent().getIntExtra("maxId", 10) + 1;
        return new VideoItem(id, title, "", CurrentUserManager.getInstance().getCurrentUser().getUsername(), 0, 0, "", "", videoUrl, thumbnailResId);
    }
}
