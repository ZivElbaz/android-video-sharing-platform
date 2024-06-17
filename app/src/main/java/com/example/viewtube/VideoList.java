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

import com.example.viewtube.R;
import com.example.viewtube.VideoItem;
import com.example.viewtube.managers.VideoDetailsManager;

import java.util.ArrayList;
import java.util.List;

public class VideoList extends RecyclerView.Adapter<VideoList.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private Context context;
    private VideoItemClickListener videoItemClickListener;
    private VideoDetailsManager videoDetailsManager;

    public VideoList(Context context, VideoItemClickListener listener) {
        this.context = context;
        this.videoItemClickListener = listener;
        this.videoItems = new ArrayList<>();
        this.videoDetailsManager = new VideoDetailsManager(context, null, null, null, null, null, null, null);
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItems.get(position);
        int videoId = videoItem.getId();
        holder.titleTextView.setText(videoItem.getTitle());
        holder.detailsTextView.setText(videoItem.getAuthor() + " - " + videoItem.getViews() + " views - " + videoItem.getDate());
        holder.durationTextView.setText(videoItem.getDuration());

        if (videoId >= 1 && videoId <= 10) {
            holder.thumbnailImageView.setImageResource(videoItem.getThumbnailResId());
        } else {
            String thumbnailPath = videoItem.getThumbnailPath();
            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(thumbnailPath);
            holder.thumbnailImageView.setImageBitmap(thumbnailBitmap);
        }

        // Use the refactored setUploaderImage method
        videoDetailsManager.setUploaderImage(videoId, holder.channelIconImageView);

        holder.itemView.setOnClickListener(v -> videoItemClickListener.onVideoItemClick(videoItem));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public interface VideoItemClickListener {
        void onVideoItemClick(VideoItem videoItem);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, detailsTextView, durationTextView;
        ImageView thumbnailImageView, channelIconImageView;

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
