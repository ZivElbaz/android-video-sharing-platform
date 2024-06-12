package com.example.viewtube.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;

    public static void checkAndRequestPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void copyVideoToExternalStorage(Activity activity, int resourceId, String fileName) {
        File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        try (InputStream inputStream = activity.getResources().openRawResource(resourceId);
             FileOutputStream outputStream = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            Toast.makeText(activity, "Download Completed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "Download Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareVideo(Activity activity, int resourceId, String fileName, String title) {
        File outFile = new File(activity.getFilesDir(), fileName);

        // Copy the file to the app's private storage if not already present
        if (!outFile.exists()) {
            try (InputStream inputStream = activity.getResources().openRawResource(resourceId);
                 FileOutputStream outputStream = new FileOutputStream(outFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                Toast.makeText(activity, "Share Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Uri contentUri = FileProvider.getUriForFile(activity, "com.example.viewtube.provider", outFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(shareIntent, "Share Video"));
    }

    public static void handlePermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults, String videoFileName) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                int videoResourceId = activity.getResources().getIdentifier(videoFileName.substring(0, videoFileName.lastIndexOf('.')), "raw", activity.getPackageName());
                copyVideoToExternalStorage(activity, videoResourceId, videoFileName);
            } else {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}