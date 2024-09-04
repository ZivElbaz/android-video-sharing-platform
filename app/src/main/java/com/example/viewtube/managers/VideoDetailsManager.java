package com.example.viewtube.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.example.viewtube.R;
import com.example.viewtube.entities.VideoItem;

public class VideoDetailsManager {

    // UI components for displaying video details
    private TextView videoTitle, videoDescription, videoDate, videoViews, uploaderName;
    private Button btnLike;
    private ImageView uploaderProfilePic;
    private Context context;

    // Constructor initializes the manager with context and UI components
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

    // Sets the video details in the UI components
    public void setVideoDetails(VideoItem videoItem) {
        videoTitle.setText(videoItem.getTitle()); // Set video title
        videoDescription.setText(videoItem.getDescription()); // Set video description
        videoViews.setText(String.valueOf(videoItem.getViews()) + " Views"); // Set number of views
        videoDate.setText(videoItem.getDate()); // Set video upload date
        btnLike.setText(String.valueOf(videoItem.getLikes())); // Set number of likes on the like button
        uploaderName.setText(videoItem.getUploader()); // Set uploader's username
        setUploaderImage(videoItem, uploaderProfilePic); // Set uploader's profile picture
    }

    // Sets the uploader's profile image based on the base64-encoded string
    public void setUploaderImage(VideoItem videoItem, ImageView imageView) {
        String base64Image = videoItem.getProfilePicture(); // Get base64-encoded image string
        if (base64Image != null) {
            if (base64Image.startsWith("data:image/jpeg;base64,")) {
                base64Image = base64Image.substring(23);  // Remove the base64 image type prefix
            }
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT); // Decode base64 string into a byte array
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // Convert byte array into Bitmap
            Bitmap circularBitmap = getCircularBitmap(decodedByte); // Convert Bitmap into circular format
            imageView.setImageBitmap(circularBitmap); // Set the circular image to the ImageView
        } else {
            imageView.setImageResource(R.drawable.ic_profile_foreground); // Set default profile image if no picture is available
        }
    }

    // Creates a circular bitmap from the provided bitmap
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight()); // Determine the smaller dimension for the circular crop

        // Create a square output bitmap
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);

        float r = size / 2f; // Calculate the radius for the circular crop

        paint.setAntiAlias(true); // Enable anti-aliasing for smooth edges
        canvas.drawARGB(0, 0, 0, 0); // Draw a transparent background
        paint.setColor(color); // Set paint color
        canvas.drawCircle(r, r, r, paint); // Draw the circular mask
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // Set the transfer mode to crop the image
        canvas.drawBitmap(bitmap, rect, rect, paint); // Draw the bitmap inside the circular mask
        return output; // Return the circular cropped bitmap
    }
}
