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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.viewtube.R;
import com.example.viewtube.adapters.VideoList;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private VideosViewModel videosViewModel;
    private ImageView profileImageView;
    private TextView usernameView;
    private BottomNavigationView bottomNavBar;
    private Boolean isLoggedIn;

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
        bottomNavBar = findViewById(R.id.bottomNavigationView);
        searchInputLayout = findViewById(R.id.search_input_layout);
        ImageView menuButton = findViewById(R.id.menu_btn);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        isLoggedIn = false;

        // Initialize side bar header views
        View headerView = sideBar.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.current_profile);
        usernameView = headerView.findViewById(R.id.current_user);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        // Setup RecyclerView
        videoList = new VideoList(this, this);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoRecyclerView.setAdapter(videoList);

        // Observe the VideosViewModel to update the video list
        videosViewModel.getVideoItemsLiveData().observe(this, videoItems -> {
            videoList.setVideoItems(videoItems);
            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation once the data is fetched
        });

        // Fetch all videos initially
        videosViewModel.fetchAllVideos();

        // Load logged-in user details from SharedPreferences
        checkLoggedInUser();

        // Set up SwipeRefreshLayout to trigger video refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            videosViewModel.fetchAllVideos();
            swipeRefreshLayout.setRefreshing(true);
        });

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

        // Handle profile image click
        profileImageView.setOnClickListener(view -> {
            if (isLoggedIn) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                String firstName = sharedPreferences.getString("firstName", "");
                String lastName = sharedPreferences.getString("lastName", "");
                String image = sharedPreferences.getString("image", "");

                Intent profileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("username", username);
                profileIntent.putExtra("firstName", firstName);
                profileIntent.putExtra("lastName", lastName);
                profileIntent.putExtra("profilePic", image);
                startActivity(profileIntent);
            } else {
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        // Handle logout menu item click
        MenuItem logOutItem = sideBar.getMenu().findItem(R.id.nav_logout);
        logOutItem.setOnMenuItemClickListener(item -> {
            if(isLoggedIn) {
                // Clear user data from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Clear only user-specific data
                editor.remove("username");
                editor.remove("firstName");
                editor.remove("lastName");
                editor.remove("image");

                // Apply the changes
                editor.apply();

                // Update the UI to guest mode
                isLoggedIn = false;
                showGuestUI();

            } else {
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
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
            videosViewModel.fetchAllVideos();
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
        videosViewModel.setSelectedVideoItem(videoItem); // Set the selected video in the ViewModel
        Intent intent = new Intent(this, VideoWatchActivity.class);
        intent.putExtra("video_id", videoItem.getId());
        startActivity(intent);
    }

    // Method to expand a view with animation
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

    // Update UI with user details if logged in
    private void updateUIWithUserDetails(User user) {
        setUserImage(user, profileImageView);
        usernameView.setText(user.getUsername());
        sideBar.getMenu().findItem(R.id.nav_logout).setTitle("Logout");
        bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(false);
        bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(true);
    }

    // Update UI to guest mode if no user is logged in
    private void showGuestUI() {
        profileImageView.setImageResource(R.drawable.ic_profile_foreground);
        usernameView.setText(R.string.guest);
        sideBar.getMenu().findItem(R.id.nav_logout).setTitle("Login");
        bottomNavBar.getMenu().findItem(R.id.nav_login).setVisible(true);
        bottomNavBar.getMenu().findItem(R.id.nav_upload).setVisible(false);
    }


    // Method to check if a user is logged in and update the UI accordingly
    private void checkLoggedInUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            // User is logged in
            String firstName = sharedPreferences.getString("firstName", "");
            String lastName = sharedPreferences.getString("lastName", "");
            String image = sharedPreferences.getString("image", "");

            User user = new User();
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setImage(image);
            isLoggedIn = true;
            updateUIWithUserDetails(user);
        } else {
            isLoggedIn = false;
            showGuestUI();
        }
    }

    // Method to set the user's profile image
    public void setUserImage(User user, ImageView imageView) {
        String base64Image = user.getImage();
        if (base64Image != null) {
            Log.d("HomeActivity", "Base64 Image String: " + base64Image);
            try {
                if (base64Image.startsWith("data:image/jpeg;base64,")) {
                    base64Image = base64Image.substring(23);  // Remove the prefix for JPEG
                } else if (base64Image.startsWith("data:image/png;base64,")) {
                    base64Image = base64Image.substring(22);  // Remove the prefix for PNG
                }
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Bitmap circularBitmap = getCircularBitmap(decodedByte);
                imageView.setImageBitmap(circularBitmap);
            } catch (IllegalArgumentException e) {
                Log.e("HomeActivity", "Failed to decode Base64 string: " + base64Image, e);
                imageView.setImageResource(R.drawable.ic_profile_foreground); // Set a default image on failure
            }
        } else {
            imageView.setImageResource(R.drawable.ic_profile_foreground); // Default profile image
        }
    }

    // Convert a Bitmap to a circular Bitmap
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
    protected void onResume() {
        super.onResume();
        // Check user status on activity resume
        checkLoggedInUser();
    }
}
