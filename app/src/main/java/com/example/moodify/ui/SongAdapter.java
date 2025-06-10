package com.example.moodify.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.home.MusicPlayerActivity;
import com.example.moodify.response.SongItem;
import com.spotify.android.appremote.api.SpotifyAppRemote;


import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<SongItem> songs;
    private Context context;
    private SpotifyAppRemote spotifyAppRemote;

    public SongAdapter(List<SongItem> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongItem song = songs.get(position);

        holder.title.setText(song.title);
        holder.artist.setText(song.artist);

        holder.textCurrentTime.setText(song.currentTime);
        holder.textTotalTime.setText(song.totalTime);
        Glide.with(context)
                .load(song.imageUrl)
                .placeholder(R.drawable.imgsong)
                .into(holder.albumImage);

        // Set ikon play/pause sesuai status lagu
        if (song.isPlaying) {
            holder.btnPlayPause.setImageResource(R.drawable.ic_pause_pink);
        } else {
            holder.btnPlayPause.setImageResource(R.drawable.ic_play_pink);
        }

        // Aksi saat tombol ditekan
        holder.btnPlayPause.setOnClickListener(v -> {
            if (song.isPlaying) {
                if (spotifyAppRemote != null) {
                    spotifyAppRemote.getPlayerApi().pause();
                }
                song.isPlaying = false;
            } else {
                // Stop semua lagu lain
                for (SongItem s : songs) s.isPlaying = false;

                // Play lagu ini
                if (spotifyAppRemote != null) {
                    spotifyAppRemote.getPlayerApi().play(song.spotifyUri);
                }
                song.isPlaying = true;
            }

            notifyDataSetChanged(); // refresh semua icon
        });

        // Aksi saat item diklik
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("title", song.title);
            intent.putExtra("artist", song.artist);
            intent.putExtra("imageUrl", song.imageUrl);
            intent.putExtra("spotifyUri", song.spotifyUri);
            intent.putExtra("currentTime", song.currentTime);
            intent.putExtra("totalTime", song.totalTime);
            Log.d("SongAdapter", "Membuka MusicPlayerActivity untuk URI: " + song.spotifyUri);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    // In SongAdapter.java
    public void updateSpotifyRemote(SpotifyAppRemote remote) {
        this.spotifyAppRemote = remote; // Make sure this line uses "this.spotifyAppRemote"
    }
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, textCurrentTime, textTotalTime;
        ImageView albumImage;
        ImageButton btnPlayPause;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textSongTitle);
            artist = itemView.findViewById(R.id.textArtistName);
            albumImage = itemView.findViewById(R.id.imageAlbumCover);
            btnPlayPause = itemView.findViewById(R.id.btnPlayPause);
            textCurrentTime = itemView.findViewById(R.id.textCurrentTime);
            textTotalTime = itemView.findViewById(R.id.textTotalTime);
        }
    }


}
