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
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.R;
import com.example.viewtube.adapters.VideoList;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.viewmodels.UserViewModel;

public class UserProfileActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    // UI components
    private ImageView profileImageView;
    private TextView usernameView;
    private TextView userFullNameView;
    private RecyclerView videosRecyclerView;
    private VideoList videoList;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI components
        Button btnUpdateProfile = findViewById(R.id.btn_update_profile);
        Button btnChangePassword = findViewById(R.id.btn_change_password);
        Button btnDeleteAccount = findViewById(R.id.btn_delete_account);
        profileImageView = findViewById(R.id.user_profile_image);
        usernameView = findViewById(R.id.user_username);
        videosRecyclerView = findViewById(R.id.user_videos_recycler_view);
        userFullNameView = findViewById(R.id.user_fullname);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize RecyclerView for user's videos
        videoList = new VideoList(this, this);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videosRecyclerView.setAdapter(videoList);

        // Get user data passed via intent
        String username = getIntent().getStringExtra("username");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String profilePic = getIntent().getStringExtra("profilePic");

        // Update UI with the user's details
        updateUIWithUserDetails(username, firstName, lastName, profilePic);

        // Get the current logged-in user from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("username", null);

        // Show or hide profile update options based on whether the user is viewing their own profile
        if (currentUser != null && currentUser.equals(username)) {
            btnUpdateProfile.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.VISIBLE);
            btnDeleteAccount.setVisibility(View.VISIBLE);
        } else {
            btnUpdateProfile.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
            btnDeleteAccount.setVisibility(View.GONE);
        }

        // Handle profile update button click
        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("profilePic", profilePic);
            startActivity(intent);
            finish();
        });

        // Handle password change button click
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Handle account deletion button click
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog(username));
    }

    // Update UI with user details
    private void updateUIWithUserDetails(String username, String firstName, String lastName, String profilePic) {
        setUserImage(profilePic, profileImageView);
        userFullNameView.setText(firstName + " " + lastName);
        usernameView.setText("@" + username);
        fetchUserVideos(username);
    }

    // Fetch and display user's videos
    private void fetchUserVideos(String username) {
        userViewModel.getVideosByUsername(username).observe(this, videoItems -> {
            if (videoItems != null) {
                videoList.setVideoItems(videoItems);
            }
        });
    }

    // Set user profile image from Base64 string
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

    // Create a circular bitmap from a rectangular bitmap
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

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        Intent intent = new Intent(this, VideoWatchActivity.class);
        intent.putExtra("video_id", videoItem.getId());
        startActivity(intent);
    }

    // Show a dialog to confirm account deletion
    private void showDeleteAccountDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_account, null);
        builder.setView(dialogView);

        EditText passwordInput = dialogView.findViewById(R.id.delete_password_input);
        Button confirmDeleteButton = dialogView.findViewById(R.id.confirm_delete_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_delete_button);

        AlertDialog dialog = builder.create();

        // Handle confirm account deletion
        confirmDeleteButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            if (!password.isEmpty()) {
                userViewModel.deleteUser(username, password).observe(this, success -> {
                    if (Boolean.TRUE.equals(success)) {
                        Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();

                        // Clear user data from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.remove("username");
                        editor.remove("firstName");
                        editor.remove("lastName");
                        editor.remove("image");
                        editor.apply();

                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete account. Check your password and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            } else {
                passwordInput.setError("Password is required");
            }
        });

        // Handle cancel account deletion
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
