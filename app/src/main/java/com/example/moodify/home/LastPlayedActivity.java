package com.example.moodify.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moodify.R;

public class LastPlayedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_played);

        TextView title = findViewById(R.id.textSongTitle);
        TextView artist = findViewById(R.id.textArtistName);
        ImageView albumCover = findViewById(R.id.imageAlbumCover);

        // Get song details from Intent
        String songTitle = getIntent().getStringExtra("title");
        String songArtist = getIntent().getStringExtra("artist");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (songTitle != null && songArtist != null && imageUrl != null) {
            // Set song details
            title.setText(songTitle);
            artist.setText(songArtist);
            Glide.with(this).load(imageUrl).placeholder(R.drawable.imgsong).into(albumCover);

            // Save song details
            saveLastPlayedSong(songTitle, songArtist, imageUrl);
        } else {
            // Load last played song details if no Intent data
            loadLastPlayedSong();
        }
    }

    private void saveLastPlayedSong(String title, String artist, String imageUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences("MoodifyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_played_title", title);
        editor.putString("last_played_artist", artist);
        editor.putString("last_played_image_url", imageUrl);
        editor.apply();
    }

    private void loadLastPlayedSong() {
        SharedPreferences sharedPreferences = getSharedPreferences("MoodifyPrefs", MODE_PRIVATE);
        String title = sharedPreferences.getString("last_played_title", "No Title");
        String artist = sharedPreferences.getString("last_played_artist", "No Artist");
        String imageUrl = sharedPreferences.getString("last_played_image_url", null);

        TextView titleView = findViewById(R.id.textSongTitle);
        TextView artistView = findViewById(R.id.textArtistName);
        ImageView albumCover = findViewById(R.id.imageAlbumCover);

        titleView.setText(title);
        artistView.setText(artist);
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.imgsong).into(albumCover);
        }
    }
}