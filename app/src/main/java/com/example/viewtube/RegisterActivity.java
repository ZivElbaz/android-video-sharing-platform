package com.example.viewtube;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.managers.UsersManager;
import com.example.viewtube.models.User;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    // UI components
    private EditText firstName;
    private EditText lastName;
    private EditText password;
    private EditText confirmPassword;
    private EditText username;
    private ImageView profilePicture;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        username = findViewById(R.id.username);
        Button register = findViewById(R.id.register);
        profilePicture = findViewById(R.id.profile_picture);
        Button selectPictureButton = findViewById(R.id.select_picture_button);

        // Set click listener for selecting a profile picture
        selectPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        // Set click listener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataEntered();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image and set it to the profile picture ImageView
            profilePictureUri = data.getData();
            profilePicture.setImageURI(profilePictureUri);
        }
    }

    // Check if an EditText is empty
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    // Validate if the password meets the criteria
    boolean isPasswordValid(EditText text) {
        CharSequence str = text.getText().toString();
        if (str.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Check if the password and confirm password fields match
    boolean isPasswordsMatch(EditText password, EditText confirmPassword) {
        return password.getText().toString().equals(confirmPassword.getText().toString());
    }

    // Validate user input and register the user
    void checkDataEntered() {
        // Check if first name is empty
        if (isEmpty(firstName)) {
            firstName.setError("First name is required!");
            return;
        }

        // Check if username is already taken
        if (UsersManager.getInstance().getUser(username.getText().toString()) != null) {
            username.setError("Username is already taken!");
            return;
        }

        // Check if last name is empty
        if (isEmpty(lastName)) {
            lastName.setError("Last name is required!");
            return;
        }

        // Check if username is empty
        if (isEmpty(username)) {
            username.setError("Username is required!");
            return;
        }

        // Check if profile picture is selected
        if (profilePictureUri == null) {
            Toast.makeText(this, "Profile picture is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if password is valid
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password must be at least 8 characters and contain" +
                    " at least one uppercase letter, one lowercase letter, one number," +
                    " and one special character!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if password and confirm password fields match
        if (!isPasswordsMatch(password, confirmPassword)) {
            confirmPassword.setError("Passwords do not match!");
            return;
        }

        // Create a new user and add to UserManager
        User newUser = new User(
                username.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                password.getText().toString(),
                profilePictureUri.toString()
        );

        // Add the new user to UsersManager and set as current user
        UsersManager.getInstance().addUser(newUser);
        UsersManager.getInstance().setCurrentUser(newUser);

        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

        // Redirect to HomeActivity
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
