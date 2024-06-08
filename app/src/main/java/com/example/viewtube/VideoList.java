package com.example.viewtube;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoList extends RecyclerView.Adapter<VideoList.VideoViewHolder> {

    private List<VideoItem> videoItems;

    public VideoList(HomeActivity homeActivity) {
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
        holder.titleTextView.setText(videoItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return videoItems != null ? videoItems.size() : 0;
    }

    public interface VideoItemClickListener {
        void onVideoItemClick(VideoItem videoItem);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.video_title_text_view);
        }
    }
}
