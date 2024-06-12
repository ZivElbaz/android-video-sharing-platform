package com.example.viewtube;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoList extends RecyclerView.Adapter<VideoList.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private Context context;
    private VideoItemClickListener videoItemClickListener;



    public VideoList(Context context, VideoItemClickListener listener) {
        this.context = context;
        this.videoItemClickListener = listener;
        this.videoItems = new ArrayList<>();
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
        notifyDataSetChanged();
    }

    public void addVideoItem(VideoItem videoItem) {
        Toast.makeText(context, "New video added to the list", Toast.LENGTH_SHORT).show();
        videoItems.add(videoItem);
        notifyItemInserted(videoItems.size() - 1);
    }

    public void logVideoList() {
        for (VideoItem item : videoItems) {
            Log.d("VideoList", "Title: " + item.getTitle() + ", Author: " + item.getAuthor() + ", VideoURL: " + item.getVideoURL());
        }
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
        holder.titleTextView.setText(videoItem.getTitle());
        holder.thumbnailImageView.setImageResource(videoItem.getThumbnailResId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoItemClickListener.onVideoItemClick(videoItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public interface VideoItemClickListener {
        void onVideoItemClick(VideoItem videoItem);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView thumbnailImageView;

        public VideoViewHolder(@NonNull View itemView) {

            super(itemView);
            titleTextView = itemView.findViewById(R.id.video_title_text_view);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail_image_view);
        }
    }
    }

