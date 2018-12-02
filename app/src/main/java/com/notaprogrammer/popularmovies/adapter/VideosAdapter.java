package com.notaprogrammer.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.notaprogrammer.popularmovies.R;
import com.notaprogrammer.popularmovies.object.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    final private VideosAdapter.ItemClickListener onListItemClick;
    private List<Video> videoList;

    public interface ItemClickListener {
        void onListItemClick(Video video);
    }

    public VideosAdapter(List<Video> videoList, VideosAdapter.ItemClickListener listener){
        this.videoList = videoList;
        this.onListItemClick = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.view_item_video;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder videoViewHolder, int position) {
        Video video = videoList.get(position);
        videoViewHolder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView videoImageView;
        final TextView videoTextView;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoImageView =  itemView.findViewById(R.id.iv_video);
            videoTextView = itemView.findViewById(R.id.tv_video);
            itemView.setOnClickListener(this);
        }

        void bind(Video video) {
            Picasso.get().load(video.getThumbnail()).error(R.drawable.movie_user_placeholder).into(videoImageView);
            videoImageView.setContentDescription(R.string.poster_content_description_image_prefix + video.getName());
            videoTextView.setText(video.getName());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Video selectedVideo = videoList.get(clickedPosition);
            onListItemClick.onListItemClick(selectedVideo);
        }
    }

    public void updateList(List<Video> videoList){
        this.videoList = videoList;
        notifyDataSetChanged();
    }

}
