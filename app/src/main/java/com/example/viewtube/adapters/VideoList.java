package com.example.viewtube.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viewtube.R;
import com.example.viewtube.api.UserAPI;
import com.example.viewtube.data.AppDB;
import com.example.viewtube.entities.VideoItem;
import com.example.viewtube.managers.VideoDetailsManager;
import com.example.viewtube.viewmodels.UserViewModel;

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
    public void setVideoItems(List<VideoItem> newVideoItems) {
        if (newVideoItems != null){
            videoItems.clear();
            videoItems.addAll(newVideoItems);
        }
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

        // Set the title, details, and duration text views
        holder.titleTextView.setText(videoItem.getTitle());
        holder.detailsTextView.setText(videoItem.getUploader() + " - " + videoItem.getViews() + " views - " + videoItem.getDate());
        holder.durationTextView.setText(videoItem.getDuration());

        // Load thumbnail image using Glide with placeholders
        Glide.with(holder.itemView.getContext())
                .load(videoItem.getThumbnail())
                .placeholder(android.R.drawable.screen_background_light_transparent)
                .error(R.drawable.ic_home_background)  // Error image if thumbnail fails to load
                .centerCrop()  // Adjust image scaling
                .into(holder.thumbnailImageView);

        // Load the uploader's profile picture
        videoDetailsManager.setUploaderImage(videoItem, holder.channelIconImageView);

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(v -> videoItemClickListener.onVideoItemClick(videoItem));
    }


    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return videoItems != null ? videoItems.size() : 0; // Null check for videoItems
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
