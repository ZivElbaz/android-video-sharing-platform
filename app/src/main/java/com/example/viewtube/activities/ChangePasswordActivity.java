package com.example.viewtube.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.viewtube.R;
import com.example.viewtube.viewmodels.UserViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private UserViewModel userViewModel; // ViewModel to handle user-related data
    private EditText currentPasswordEditText; // Input field for current password
    private EditText newPasswordEditText; // Input field for new password
    private Button changePasswordButton; // Button to trigger password change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UI elements
        currentPasswordEditText = findViewById(R.id.edit_current_password);
        newPasswordEditText = findViewById(R.id.edit_new_password);
        changePasswordButton = findViewById(R.id.change_password_button);

        // Retrieve the username passed from the previous activity
        String username = getIntent().getStringExtra("username");

        // Set a click listener for the change password button
        changePasswordButton.setOnClickListener(v -> {
            // Get the input values
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();

            // Check if the input fields are not empty
            if (!TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(newPassword)) {
                // Call ViewModel to change the password
                userViewModel.updateUserPassword(username, currentPassword, newPassword).observe(this, success -> {
                    if (Boolean.TRUE.equals(success)) {
                        // Password change succeeded
                        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        // Password change failed (e.g., incorrect current password)
                        Toast.makeText(this, "Failed to change password. Check your current password and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Show a message if any field is empty
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
