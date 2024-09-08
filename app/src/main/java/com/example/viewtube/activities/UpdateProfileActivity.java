package com.example.viewtube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.viewtube.R;
import com.example.viewtube.entities.User;
import com.example.viewtube.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    // UI components
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private ImageView profileImageView;
    private Button saveButton;
    private Button cancelButton;
    private Uri profilePictureUri;
    private UserViewModel userViewModel;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UI components
        firstNameEditText = findViewById(R.id.edit_first_name);
        lastNameEditText = findViewById(R.id.edit_last_name);
        profileImageView = findViewById(R.id.profile_image_view);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        Button selectPictureButton = findViewById(R.id.select_picture_button);

        // Get current user details from Intent
        username = getIntent().getStringExtra("username");
        String currentFirstName = getIntent().getStringExtra("firstName");
        String currentLastName = getIntent().getStringExtra("lastName");
        String currentProfilePic = getIntent().getStringExtra("profilePic");

        // Set current user details to UI components
        firstNameEditText.setText(currentFirstName);
        lastNameEditText.setText(currentLastName);
        setUserImage(currentProfilePic, profileImageView);

        // Set click listener for selecting a profile picture
        selectPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Set click listener for save button
        saveButton.setOnClickListener(v -> checkDataEntered());
        // Set click listener for cancel button
        cancelButton.setOnClickListener(v ->{
            Intent homeIntent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // Get selected image URI and set it to the profileImageView
            profilePictureUri = data.getData();
            profileImageView.setImageURI(profilePictureUri);
        }
    }

    // Validate if the entered data is correct
    private void checkDataEntered() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        // Check if first name is empty
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required!");
            return;
        }

        // Check if last name is empty
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required!");
            return;
        }

        // Proceed to update user profile if data is valid
        updateUserProfile(firstName, lastName);
    }

    // Update user profile with new details
    private void updateUserProfile(String firstName, String lastName) {
        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);

        // Convert the profile picture to base64 if a new picture is selected
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
                updatedUser.setImage("data:image/jpeg;base64," + encodedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image encoding failed", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // If no new image is selected, use the existing image from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            updatedUser.setImage(sharedPreferences.getString("image", null));
        }

        // Update user data using ViewModel
        userViewModel.updateUserData(username, firstName, lastName, updatedUser.getImage()).observe(this, user -> {
            if (user != null) {
                // Save updated user data
                saveUserData(user);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to UserProfileActivity with updated data
                Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("firstName", user.getFirstName());
                intent.putExtra("lastName", user.getLastName());
                intent.putExtra("profilePic", user.getImage());
                startActivity(intent);
                finish();  // Close the activity after updating
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save updated user data in SharedPreferences
    private void saveUserData(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remove old data and save new data
        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("image");

        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("image", user.getImage());
        editor.apply();
    }

    // Set user image from Base64 string
    private void setUserImage(String profilePic, ImageView imageView) {
        String base64Image = profilePic;
        if (base64Image != null) {
            if (base64Image.startsWith("data:image/jpeg;base64,")) {
                base64Image = base64Image.substring(23);  // Remove the prefix
            }
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap circularBitmap = getCircularBitmap(decodedByte);
            imageView.setImageBitmap(circularBitmap);
        } else {
            // Set default profile image if no image is available
            imageView.setImageResource(R.drawable.ic_profile_foreground);
        }
    }

    // Convert a rectangular Bitmap into a circular Bitmap
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);

        float r = size / 2f;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
