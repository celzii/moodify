package com.example.moodify.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.response.PlaylistItem;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<PlaylistItem> playlistItems;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PlaylistItem item);
    }

    public PlaylistAdapter(List<PlaylistItem> playlistItems, Context context, OnItemClickListener listener) {
        this.playlistItems = playlistItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem item = playlistItems.get(position);
        holder.title.setText(item.name);
        if (item.isFromDrawable()) {
            holder.image.setImageResource(item.imageResId);
        } else {
            Glide.with(context).load(item.imageUrl).into(holder.image);
        }


        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgPlaylist);
            title = itemView.findViewById(R.id.namePlaylist);
        }
    }
}

