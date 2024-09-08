package com.example.viewtube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viewtube.R;

import com.example.viewtube.entities.User;

import androidx.lifecycle.ViewModelProvider;

import com.example.viewtube.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private UserViewModel userViewModel;

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
        profilePicture = findViewById(R.id.profile_picture);
        Button register = findViewById(R.id.register);
        Button selectPictureButton = findViewById(R.id.select_picture_button);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Set click listener for selecting a profile picture
        selectPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Set click listener for register button
        register.setOnClickListener(view -> checkDataEntered());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            profilePictureUri = data.getData();
            profilePicture.setImageURI(profilePictureUri);
        }
    }

    // Check if an EditText is empty
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    // Validate if password meets criteria
    boolean isPasswordValid(EditText text) {
        CharSequence str = text.getText().toString();
        if (str.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        // Check each character in password for criteria
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Check if password and confirm password match
    boolean isPasswordsMatch(EditText password, EditText confirmPassword) {
        return password.getText().toString().equals(confirmPassword.getText().toString());
    }

    // Validate all fields before registering the user
    void checkDataEntered() {
        if (isEmpty(firstName)) {
            firstName.setError("First name is required!");
            return;
        }

        if (isEmpty(lastName)) {
            lastName.setError("Last name is required!");
            return;
        }

        if (isEmpty(username)) {
            username.setError("Username is required!");
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special character!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordsMatch(password, confirmPassword)) {
            confirmPassword.setError("Passwords do not match!");
            return;
        }

        checkUsernameAndRegister();
    }

    // Check if the username is already taken before registering the user
    private void checkUsernameAndRegister() {
        userViewModel.checkUsernameExists(username.getText().toString()).observe(this, exists -> {
            if (exists != null && exists) {
                username.setError("Username is already taken!");
            } else {
                registerUser();
            }
        });
    }

    // Register the new user in the system
    private void registerUser() {
        User newUser = new User();
        newUser.setUsername(username.getText().toString().trim());
        newUser.setFirstName(firstName.getText().toString().trim());
        newUser.setLastName(lastName.getText().toString().trim());
        newUser.setPassword(password.getText().toString().trim());

        // Convert the profile picture to base64
        if (profilePictureUri != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(profilePictureUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                // Resize the image to a smaller resolution (e.g., 350x350)
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 350, 350, false);

                // Compress the resized image to JPEG format
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                // Store the compressed and encoded image as a Base64 string in the user object
                newUser.setImage("data:image/jpeg;base64," + encodedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image encoding failed", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Use the ViewModel to register the user
        userViewModel.registerUser(newUser, this).observe(this, registeredUser -> {
            if (registeredUser != null) {
                // Save user data to SharedPreferences
                saveUserData(registeredUser);

                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                // Navigate to the next activity or home screen
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save registered user data in SharedPreferences
    private void saveUserData(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("image", user.getImage());
        editor.apply();
    }
}

