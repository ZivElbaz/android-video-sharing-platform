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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.R;
import com.example.viewtube.adapters.VideoList;
import com.example.viewtube.entities.User;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.viewmodels.UserViewModel;
import com.example.viewtube.viewmodels.VideosViewModel;

public class UserProfileActivity extends AppCompatActivity implements VideoList.VideoItemClickListener {

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
        profileImageView = findViewById(R.id.user_profile_image);
        usernameView = findViewById(R.id.user_username);
        videosRecyclerView = findViewById(R.id.user_videos_recycler_view);
        userFullNameView = findViewById(R.id.user_fullname);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        videoList = new VideoList(this, this);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videosRecyclerView.setAdapter(videoList);

        String username = getIntent().getStringExtra("username");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String profilePic = getIntent().getStringExtra("profilePic");

        updateUIWithUserDetails(username, firstName, lastName, profilePic);

    }

    private void updateUIWithUserDetails(String username, String firstName, String lastName, String profilePic) {
        setUserImage(profilePic, profileImageView);
        userFullNameView.setText(firstName + " " + lastName);
        usernameView.setText("@" + username);
        fetchUserVideos(username);
    }

    private void fetchUserVideos(String username) {
        userViewModel.getVideosByUsername(username).observe(this, videoItems -> {
            if (videoItems != null) {
                videoList.setVideoItems(videoItems);
            }
        });
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

    @Override
    public void onVideoItemClick(VideoItem videoItem) {
        Intent intent = new Intent(this, VideoWatchActivity.class);
        intent.putExtra("video_id", videoItem.getId());
        startActivity(intent);
    }

}