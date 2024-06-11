package com.example.viewtube;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        uploadButton = findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(UploadActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the upload logic here
                    // For now, just show a toast message
                    Toast.makeText(UploadActivity.this, "Uploading video: " + title, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
