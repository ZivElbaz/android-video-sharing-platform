package com.example.viewtube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewtube.managers.VideoDetailsManager;

import java.util.ArrayList;
import java.util.List;

public class VideoList extends RecyclerView.Adapter<VideoList.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private final VideoItemClickListener videoItemClickListener;
    private final VideoDetailsManager videoDetailsManager;

    // Constructor
    public VideoList(Context context, VideoItemClickListener listener) {
        this.videoItemClickListener = listener;
        this.videoItems = new ArrayList<>();
        this.videoDetailsManager = new VideoDetailsManager(context, null, null, null, null, null, null, null);
    }

    // Method to set the list of video items and notify the adapter of data changes
    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
        notifyDataSetChanged();
    }

    // Method to create a new ViewHolder
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_video layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    // Method to bind data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // Get the current video item
        VideoItem videoItem = videoItems.get(position);
        int videoId = videoItem.getId();

        // Set the title, details, and duration text views
        holder.titleTextView.setText(videoItem.getTitle());
        holder.detailsTextView.setText(videoItem.getAuthor() + " - " + videoItem.getViews() + " views - " + videoItem.getDate());
        holder.durationTextView.setText(videoItem.getDuration());

        // Set the thumbnail image view based on the video ID
        if (videoId >= 1 && videoId <= 10) {
            holder.thumbnailImageView.setImageResource(videoItem.getThumbnailResId());
        } else {
            String thumbnailPath = videoItem.getThumbnailPath();
            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(thumbnailPath);
            holder.thumbnailImageView.setImageBitmap(thumbnailBitmap);
        }

        // Use the refactored setUploaderImage method to set the channel icon image view
        videoDetailsManager.setUploaderImage(videoId, holder.channelIconImageView);

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(v -> videoItemClickListener.onVideoItemClick(videoItem));
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    // Interface for handling video item clicks
    public interface VideoItemClickListener {
        void onVideoItemClick(VideoItem videoItem);
    }

    // ViewHolder class to hold references to each item's views
    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, detailsTextView, durationTextView;
        ImageView thumbnailImageView, channelIconImageView;

        // Constructor to initialize the item views
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.video_title_text_view);
            detailsTextView = itemView.findViewById(R.id.video_details_text_view);
            durationTextView = itemView.findViewById(R.id.video_duration_text_view);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail_image_view);
            channelIconImageView = itemView.findViewById(R.id.channel_icon_image_view);
        }
    }
}
