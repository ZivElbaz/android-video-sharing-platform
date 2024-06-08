package com.example.viewtube;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.LoginActivity;
import com.example.viewtube.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example: Navigate to the LoginActivity if the user is not logged in
        if (!isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Finish the MainActivity to prevent the user from returning to it
        }
    }

    private boolean isLoggedIn() {
        // Implement logic to check if the user is logged in
        // For example, check if there is a user session or token
        return false; // Replace with your authentication logic
    }
}
