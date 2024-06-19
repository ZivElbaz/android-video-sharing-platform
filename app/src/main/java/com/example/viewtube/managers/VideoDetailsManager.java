package com.example.viewtube.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.example.viewtube.managers.CurrentUserManager;
import com.example.viewtube.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class VideoDetailsManager {

    private TextView videoTitle, videoDescription, videoDate, videoViews, uploaderName;
    private Button btnLike;
    private ImageView uploaderProfilePic;
    private Context context;
    private String author;

    // Constructor to initialize the VideoDetailsManager with UI components and context
    public VideoDetailsManager(Context context, TextView videoTitle, TextView videoDescription, TextView videoDate,
                               TextView videoViews, Button btnLike, TextView uploaderName, ImageView uploaderProfilePic) {
        this.context = context;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDate = videoDate;
        this.videoViews = videoViews;
        this.btnLike = btnLike;
        this.uploaderName = uploaderName;
        this.uploaderProfilePic = uploaderProfilePic;
    }

    // Method to set video details on the UI components
    public void setVideoDetails(String title, String description, String date, int views, int likes, String author) {
        videoTitle.setText(title);
        videoDescription.setText(description);
        videoViews.setText(String.valueOf(views) + " Views");
        videoDate.setText(date);
        btnLike.setText(String.valueOf(likes));
        uploaderName.setText(author);
    }

    // Method to set the uploader's profile image
    public void setUploaderImage(int id, ImageView imageView) {
        if (id >= 1 && id <= 10) {
            // Set a default image for predefined video IDs
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lahav);
            Bitmap circularBitmap = getCircularBitmap(bitmap);
            imageView.setImageBitmap(circularBitmap);
        } else {
            // Set the current user's profile image for video IDs 11 and above
            if (CurrentUserManager.getInstance().getCurrentUser() != null) {
                String profilePictureUriString = CurrentUserManager.getInstance().getCurrentUser().getProfilePictureUri();
                if (profilePictureUriString != null && !profilePictureUriString.isEmpty()) {
                    Uri profilePicture = Uri.parse(profilePictureUriString);
                    imageView.setImageURI(profilePicture); // Set profile image using URI

                    // Load the bitmap from Uri and crop into a circle
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(profilePicture);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Bitmap circularBitmap = getCircularBitmap(bitmap);
                        imageView.setImageBitmap(circularBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        imageView.setImageResource(R.drawable.ic_profile_foreground); // Fallback image
                    }

                } else {
                    imageView.setImageResource(R.drawable.ic_profile_foreground); // Default profile image
                }
            } else {
                imageView.setImageResource(R.drawable.ic_profile_foreground); // Default profile image for guest
            }
        }
    }

    // Method to create a circular bitmap from a given bitmap
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
