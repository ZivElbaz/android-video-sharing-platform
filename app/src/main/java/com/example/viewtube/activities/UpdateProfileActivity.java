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

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private ImageView profileImageView;
    private Button saveButton;
    private Uri profilePictureUri;
    private UserViewModel userViewModel;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        firstNameEditText = findViewById(R.id.edit_first_name);
        lastNameEditText = findViewById(R.id.edit_last_name);
        profileImageView = findViewById(R.id.profile_image_view);
        saveButton = findViewById(R.id.save_button);
        Button selectPictureButton = findViewById(R.id.select_picture_button);

        username = getIntent().getStringExtra("username");
        String currentFirstName = getIntent().getStringExtra("firstName");
        String currentLastName = getIntent().getStringExtra("lastName");
        String currentProfilePic = getIntent().getStringExtra("profilePic");

        firstNameEditText.setText(currentFirstName);
        lastNameEditText.setText(currentLastName);
        setUserImage(currentProfilePic, profileImageView);

        selectPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        saveButton.setOnClickListener(v -> checkDataEntered());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            profilePictureUri = data.getData();
            profileImageView.setImageURI(profilePictureUri);
        }
    }

    private void checkDataEntered() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required!");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required!");
            return;
        }

        updateUserProfile(firstName, lastName);
    }

    private void updateUserProfile(String firstName, String lastName) {
        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);

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

        userViewModel.updateUserData(username, firstName, lastName, updatedUser.getImage()).observe(this, user -> {
            if (user != null) {
                saveUserData(user);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
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

    private void saveUserData(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("image");

        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("image", user.getImage());
        editor.apply();
    }

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
            imageView.setImageResource(R.drawable.ic_profile_foreground); // Default profile image
        }
    }

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
