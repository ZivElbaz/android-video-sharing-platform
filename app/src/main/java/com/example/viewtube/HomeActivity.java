package com.example.viewtube;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class HomeActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    private static final String TAG = "HomeActivity";

    private RecyclerView videoRecyclerView;
    private VideoList videoList;
    private List<VideoItem> uploadedVideoList;
    private ImageView searchButton;
    private TextInputEditText searchBar;
    private BottomNavigationView bottomNavBar;
    private List<VideoItem> allVideoItems;

    private ArrayList<VideoItem> mergedList;
    private TextInputLayout searchInputLayout;
    private NavigationView sideBar;
    private Uri videoUri;
    private TextInputEditText titleEditText;

    private Uri profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sideBar = findViewById(R.id.navigation_view);
        searchButton = findViewById(R.id.search_button);
        videoRecyclerView = findViewById(R.id.video_feed_recycler_view);
        searchBar = findViewById(R.id.search_bar);
        bottomNavBar = findViewById(R.id.bottomNavigationView);
        searchInputLayout = findViewById(R.id.search_input_layout);
        allVideoItems = new ArrayList<>();
        uploadedVideoList = new ArrayList<>();
        mergedList = new ArrayList<>();

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
                profilePicture = Uri.parse(profilePictureUriString);
                profileImageView.setImageURI(profilePicture); // Set profile image using URI
                profileImageView.setImageResource(R.drawable.circular_background);
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
                mergedList.addAll(allVideoItems);
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


        searchButton.setOnClickListener(view -> {
            // Toggle search bar visibility
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            if (searchInputLayout.getVisibility() == View.GONE) {
                searchInputLayout.setVisibility(View.VISIBLE);
                searchInputLayout.startAnimation(slideDown);
            } else {
                searchInputLayout.setVisibility(View.GONE);
                hideKeyboard();
                searchBar.setText("");
                filter("");
            }
        });

        bottomNavBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Render all videos when the home button is clicked
                videoList.setVideoItems(mergedList);
                searchBar.setText("");
                searchBar.clearFocus();
                videoRecyclerView.smoothScrollToPosition(0);
                return true;
            } else if (itemId == R.id.nav_login) {
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            } else if (itemId == R.id.nav_upload) {
                Intent uploadIntent = new Intent(HomeActivity.this, UploadActivity.class);
                uploadIntent.putExtra("maxId", getMaxId(mergedList));
                startActivityForResult(uploadIntent, UPLOAD_REQUEST_CODE); // Start UploadActivity for result
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
        searchInputLayout = findViewById(R.id.search_input_layout);
        searchInputLayout.setEndIconOnClickListener(view -> {
            searchBar.setText("");
            searchBar.clearFocus();
            filter("");
            hideKeyboard();
        });
    }

    private static final int UPLOAD_REQUEST_CODE = 1001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve the uploaded video item from the UploadActivity result
            VideoItem uploadedVideoItem = data.getParcelableExtra("uploadedVideoItem");
            if (uploadedVideoItem != null) {
                // Add the uploaded video item to the list and refresh the RecyclerView
                uploadedVideoList.add(uploadedVideoItem);
                mergedList.addAll(uploadedVideoList);
                videoList.setVideoItems(mergedList);
                // Show a success message
                Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }
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
        for (VideoItem item : mergedList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        videoList.setVideoItems(filteredList);
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        // Handle video item click
        String videoResourceName = videoItem.getVideoURL(); // Ensure this is the correct resource name
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        int videoId = videoItem.getId();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putParcelableArrayListExtra("video_items", mergedList);
        moveToWatch.putExtra("video_resource_name", videoResourceName); // Change to video_resource_name
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        moveToWatch.putExtra("video_id", videoId);
        startActivity(moveToWatch);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Exit the app
        CurrentUserManager.getInstance().logout();
        finishAffinity();
    }

    private int getMaxId (List<VideoItem> videoItems) {
        int max = 0;
        for (VideoItem v:videoItems) {
            max = Math.max(max, v.getId());
        }
        return max;
    }
}
