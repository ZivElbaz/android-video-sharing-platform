package com.example.viewtube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.CurrentUserManager;
import com.example.viewtube.viewmodels.VideosViewModel;
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
    private VideosViewModel videosViewModel;




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

        // Setup RecyclerView
        videoList = new VideoList(this, this);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoRecyclerView.setAdapter(videoList);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        videosViewModel.getVideoItemsLiveData().observe(this, videoItems -> videoList.setVideoItems(videoItems));
        videosViewModel.fetchAllVideos();

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
                videoList.setVideoItems(videosViewModel.getVideoItemsLiveData().getValue());
                searchBar.setText("");
                searchBar.clearFocus();
                videoRecyclerView.smoothScrollToPosition(0);
                return true;
            } else if (itemId == R.id.nav_login) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else if (itemId == R.id.nav_upload && videosViewModel.getVideoItemsLiveData().getValue() != null) {
                Intent uploadIntent = new Intent(HomeActivity.this, UploadActivity.class);
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
        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the list of videos from the server
            videosViewModel.reload();
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
        for (VideoItem videoItem : videosViewModel.getVideoItemsLiveData().getValue()) {
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
        videosViewModel.setSelectedVideoItem(videoItem);
        Intent intent = new Intent(this, VideoWatchActivity.class);
        startActivity(intent);
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
