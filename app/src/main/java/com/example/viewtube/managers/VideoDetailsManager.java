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
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.viewtube.R;
import com.example.viewtube.entities.ProfilePictureResponse;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.viewmodels.UserViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailsManager {

    private TextView videoTitle, videoDescription, videoDate, videoViews, uploaderName;
    private Button btnLike;
    private ImageView uploaderProfilePic;
    private Context context;
    private UserViewModel userViewModel;
    private LifecycleOwner lifecycleOwner;

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
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setVideoDetails(VideoItem videoItem) {
        videoTitle.setText(videoItem.getTitle());
        videoDescription.setText(videoItem.getDescription());
        videoViews.setText(String.valueOf(videoItem.getViews()) + " Views");
        videoDate.setText(videoItem.getDate());
        btnLike.setText(String.valueOf(videoItem.getLikes()));
        uploaderName.setText(videoItem.getUploader());
        setUploaderImage(videoItem, uploaderProfilePic);
    }

    public void setUploaderImage(VideoItem videoItem, ImageView imageView) {
        String base64Image = videoItem.getProfilePicture();
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
