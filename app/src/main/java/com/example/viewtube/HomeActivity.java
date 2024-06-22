package com.example.viewtube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.managers.CurrentUserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

    // UI components
    private RecyclerView videoRecyclerView;
    private VideoList videoList;
    private TextInputEditText searchBar;
    private TextInputLayout searchInputLayout;
    private NavigationView sideBar;

    private SharedPreferences sharedPreferences;
    private VideoViewModel videoViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme preferences
        sharedPreferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI components
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        sideBar = findViewById(R.id.navigation_view);
        ImageView searchButton = findViewById(R.id.search_button);
        videoRecyclerView = findViewById(R.id.video_feed_recycler_view);
        searchBar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);
        searchInputLayout = findViewById(R.id.search_input_layout);
        ImageView menuButton = findViewById(R.id.menu_btn);


        // Initialize ViewModel
        videoViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(VideoViewModel.class);
        videoViewModel.getVideoItems().observe(this, videoItems -> {
            videoList.setVideoItems(videoItems);
        });

        // Initialize side bar header views
        View headerView = sideBar.getHeaderView(0);
        ImageView profileImageView = headerView.findViewById(R.id.current_profile);
        TextView usernameView = headerView.findViewById(R.id.current_user);

        // Check if there is a current user and update the UI accordingly
        if (CurrentUserManager.getInstance().getCurrentUser() == null) {
            bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(true);
            bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(false);
            profileImageView.setImageResource(R.drawable.ic_profile_foreground);
            usernameView.setText(R.string.guest);
        } else {
            bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(false);
            bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(true);
            String profilePictureUriString = CurrentUserManager.getInstance().getCurrentUser().getProfilePictureUri();
            if (profilePictureUriString != null && !profilePictureUriString.isEmpty()) {
                Uri profilePicture = Uri.parse(profilePictureUriString);
                profileImageView.setImageURI(profilePicture); // Set profile image using URI
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_foreground); // Set default profile image
            }
            usernameView.setText(CurrentUserManager.getInstance().getCurrentUser().getUsername());
        }

        // Setup RecyclerView
        videoList = new VideoList(this, this); // Pass the context and the listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(layoutManager);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.setAdapter(videoList);

        // Handle search button click to toggle search bar visibility
          searchButton.setOnClickListener(view -> {
            if (searchInputLayout.getVisibility() == View.GONE) {
                searchInputLayout.setVisibility(View.VISIBLE);
                expandView(searchInputLayout);
            } else {
                collapseView(searchInputLayout);
                hideKeyboard();
            }


        });

        // Handle bottom navigation bar item selection
        bottomNavBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Render all videos when the home button is clicked
                videoList.setVideoItems(videoViewModel.getVideoItems().getValue());
                searchBar.setText("");
                searchBar.clearFocus();
                videoRecyclerView.smoothScrollToPosition(0);
                return true;
            } else if (itemId == R.id.nav_login) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else if (itemId == R.id.nav_upload && videoViewModel.getVideoItems().getValue() != null) {
                Intent uploadIntent = new Intent(HomeActivity.this, UploadActivity.class);
                uploadIntent.putExtra("maxId", getMaxId(videoViewModel.getVideoItems().getValue()));
                startActivityForResult(uploadIntent, UPLOAD_REQUEST_CODE); // Start UploadActivity for result
            }
            return false;
        });

        // Handle search bar actions
        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(v.getText().toString());
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Handle text changes and clear button click
        searchInputLayout.setEndIconOnClickListener(view -> {
            searchBar.setText("");
            searchBar.clearFocus();
            filter("");
            hideKeyboard();
        });

        // Implement the dark mode toggle logic
        MenuItem darkModeItem = sideBar.getMenu().findItem(R.id.nav_dark_mode);
        SwitchCompat darkModeSwitch = (SwitchCompat) darkModeItem.getActionView();
        darkModeSwitch.setChecked(isDarkMode);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTheme(isChecked);
        });


        // Listener for opening or closing the side bar
        menuButton.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(sideBar)) {
                drawerLayout.closeDrawer(sideBar);
            } else {
                drawerLayout.openDrawer(sideBar);
            }
        });

        // Handle log out menu item click

        MenuItem logOutItem = sideBar.getMenu().findItem(R.id.nav_logout);
        logOutItem.setOnMenuItemClickListener(item -> {
            if (CurrentUserManager.getInstance().getCurrentUser() != null) {
                CurrentUserManager.getInstance().logout();
                startActivity(getIntent());
                finish();
            } else {
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            }
            return true;
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
                videoViewModel.addVideoItem(uploadedVideoItem);
                // Show a success message
                Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hide the keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        }
    }

    // Filter video items based on the query
    private void filter(String query) {
        List<VideoItem> filteredList = new ArrayList<>();
        for (VideoItem videoItem : videoViewModel.getVideoItems().getValue()) {
            if (videoItem.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(videoItem);
            }
        }
        videoList.setVideoItems(filteredList);
    }

    // Toggle theme and save preference
    private void toggleTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.apply();

        if (isDarkMode) {
            getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOutDark);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOutLight);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        // Handle video item click
        String videoResourceName = videoItem.getVideoURL();
        String videoTitle = videoItem.getTitle();
        String videoDescription = videoItem.getDescription();
        String author = videoItem.getAuthor();
        int videoLikes = videoItem.getLikes();
        int videoViews = videoItem.getViews();
        int videoId = videoItem.getId();
        String videoDate = videoItem.getDate();
        Intent moveToWatch = new Intent(this, VideoWatchActivity.class);
        moveToWatch.putExtra("video_resource_name", videoResourceName);
        moveToWatch.putExtra("video_title", videoTitle);
        moveToWatch.putExtra("video_description", videoDescription);
        moveToWatch.putExtra("video_likes", videoLikes);
        moveToWatch.putExtra("video_date", videoDate);
        moveToWatch.putExtra("video_views", videoViews);
        moveToWatch.putExtra("video_id", videoId);
        moveToWatch.putExtra("video_author", author);
        startActivity(moveToWatch);
    }

    // Get the maximum ID from the list of video items
    private int getMaxId(List<VideoItem> videoItems) {
        int max = 0;
        for (VideoItem v : videoItems) {
            max = Math.max(max, v.getId());
        }
        return max;
    }

    private void expandView(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    // Method to animate the collapse of a view
    private void collapseView(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

        };

        // 1dp/ms
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    public void onClearSearchClick(View view) {
    }
}
