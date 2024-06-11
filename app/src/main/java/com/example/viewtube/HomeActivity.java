package com.example.viewtube;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.viewtube.managers.CurrentUserManager;
import com.example.viewtube.models.User;

public class HomeActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    private static final String TAG = "HomeActivity";

    private RecyclerView videoRecyclerView;
    private VideoList videoList;
    private Button uploadButton;
    private TextInputEditText searchBar;
    private BottomNavigationView bottomNavBar;
    private List<VideoItem> allVideoItems;

    private NavigationView sideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: Initializing components");

        sideBar = findViewById(R.id.navigation_view);
        uploadButton = findViewById(R.id.upload_button_home);
        videoRecyclerView = findViewById(R.id.video_feed_recycler_view);
        searchBar = findViewById(R.id.search_bar);
        bottomNavBar = findViewById(R.id.bottomNavigationView);

        // Initialize side bar header views
        View headerView = sideBar.getHeaderView(0);
        ImageView profileImageView = headerView.findViewById(R.id.current_profile);
        TextView usernameView = headerView.findViewById(R.id.current_user);

        if (CurrentUserManager.getInstance().getCurrentUser() == null) {
            // If no current user, show the login item
            bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(true);
            bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(false);
            profileImageView.setImageResource(R.drawable.ic_profile);
            usernameView.setText("Guest");
        } else {
            // If there is a current user, hide the login item
            bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(false);
            bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(true);
            String profilePictureUriString = CurrentUserManager.getInstance().getCurrentUser().getProfilePictureUri();
            if (profilePictureUriString != null && !profilePictureUriString.isEmpty()) {
                Uri profilePictureUri = Uri.parse(profilePictureUriString);
                profileImageView.setImageURI(profilePictureUri); // Set profile image using URI
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile); // Set default profile image
            }
            usernameView.setText(CurrentUserManager.getInstance().getCurrentUser().getUsername());
        }

        videoList = new VideoList(this, this); // Pass the context and the listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(layoutManager);
        videoRecyclerView.setAdapter(videoList);

        // Load video items from JSON file
        try (InputStream inputStream = getResources().openRawResource(R.raw.db)) {
            allVideoItems = VideoItemParser.parseVideoItems(inputStream, this); // Pass the context
            if (allVideoItems != null) {
                videoList.setVideoItems(allVideoItems);
            } else {
                // Handle case where videoItems is null
                Toast.makeText(this, "Failed to load video items", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // Handle IO or JSON exception
            // Show an error message or take appropriate action
            Toast.makeText(this, "Error loading video items", Toast.LENGTH_SHORT).show();
        }

        uploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        bottomNavBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Render all videos when the home button is clicked
                videoList.setVideoItems(allVideoItems);
                searchBar.setText("");
                searchBar.clearFocus();
                videoRecyclerView.smoothScrollToPosition(0);
                return true;
            } else if (itemId == R.id.nav_login) {
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            } else if (itemId == R.id.nav_upload) {
                Intent uploadIntent = new Intent(HomeActivity.this, UploadActivity.class);
                startActivity(uploadIntent);
            }
            return false;
        });

        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(v.getText().toString());
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Handle text changes and clear button click
        TextInputLayout searchInputLayout = findViewById(R.id.search_input_layout);
        searchInputLayout.setEndIconOnClickListener(view -> {
            searchBar.setText("");
            searchBar.clearFocus();
            filter("");
            hideKeyboard();
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            searchBar.clearFocus();
        }
    }

    private void filter(String text) {
        List<VideoItem> filteredList = new ArrayList<>();
        for (VideoItem item : allVideoItems) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        videoList.setVideoItems(filteredList);
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        String videoResourceName = videoItem.getVideoURL(); // Ensure this is the correct resource name
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        startActivity(moveToWatch);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Exit the app
        CurrentUserManager.getInstance().logout();
        finishAffinity();
    }
}
