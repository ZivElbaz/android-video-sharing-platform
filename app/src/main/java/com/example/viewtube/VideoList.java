package com.example.viewtube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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

        if (videoId >= 1 && videoId <= 10) {
            holder.thumbnailImageView.setImageResource(videoItem.getThumbnailResId());
        } else {
            String thumbnailPath = videoItem.getThumbnailPath();
            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(thumbnailPath);
            holder.thumbnailImageView.setImageBitmap(thumbnailBitmap);
        }



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

