package com.example.moodify.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView; // Pastikan TextView diimpor

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription; // Impor Subscription
import com.spotify.protocol.types.PlayerState; // Impor PlayerState

public class MusicPlayerActivity extends AppCompatActivity {

    private SpotifyAppRemote spotifyAppRemote;
    private static final String CLIENT_ID = "7944a6bf26b84e80ad4fc02309b352e9";
    private static final String REDIRECT_URI = "moodify://callback";

    private TextView textTitle;
    private TextView textArtist;
    private ImageView imageCover;
    private TextView textCurrentTime;
    private TextView textTotalTime;
    private SeekBar seekBar; // Tambahkan deklarasi SeekBar

    private String spotifyUriToPlay;
    private Subscription<PlayerState> playerStateSubscription; // Untuk unsubscribe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_music_player), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textTitle = findViewById(R.id.textTitle);
        textArtist = findViewById(R.id.textArtist);
        imageCover = findViewById(R.id.imageCover);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);
        seekBar = findViewById(R.id.seekBar); // Inisialisasi SeekBar

        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        spotifyUriToPlay = getIntent().getStringExtra("spotifyUri"); // Simpan URI untuk diputar nanti
        String initialCurrentTime = getIntent().getStringExtra("currentTime");
        String initialTotalTime = getIntent().getStringExtra("totalTime");

        Log.d("MusicPlayerActivity", "Initial CurrentTime: " + initialCurrentTime);
        Log.d("MusicPlayerActivity", "Initial TotalTime: " + initialTotalTime);
        Log.d("MusicPlayerActivity", "Spotify URI to play: " + spotifyUriToPlay);

        textTitle.setText(title);
        textArtist.setText(artist);
        Glide.with(this).load(imageUrl).placeholder(R.drawable.imgsong).into(imageCover);

        textCurrentTime.setText(initialCurrentTime != null ? initialCurrentTime : "0:00");
        textTotalTime.setText(initialTotalTime != null ? initialTotalTime : "0:00");
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote remote) {
                spotifyAppRemote = remote;
                Log.d("MusicPlayerActivity", "Connected to Spotify");

                if (spotifyUriToPlay != null && spotifyAppRemote != null && spotifyAppRemote.isConnected()) {
                    spotifyAppRemote.getPlayerApi().play(spotifyUriToPlay);
                    subscribeToPlayerState();
                } else {
                    Log.w("MusicPlayerActivity", "Spotify URI is null or remote not connected properly after connect.");
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("MusicPlayerActivity", "Failed to connect to Spotify", error);
            }
        });
    }

    private void subscribeToPlayerState() {
        if (spotifyAppRemote == null || !spotifyAppRemote.isConnected()) {
            Log.w("MusicPlayerActivity", "Cannot subscribe to player state: SpotifyAppRemote not connected.");
            return;
        }

        playerStateSubscription = spotifyAppRemote.getPlayerApi().subscribeToPlayerState();
        if (playerStateSubscription == null) {
            Log.e("MusicPlayerActivity", "Failed to subscribe to player state, subscription is null.");
            return;
        }

        playerStateSubscription.setEventCallback(playerState -> {
            if (playerState.track == null) {
                Log.w("MusicPlayerActivity", "PlayerState received, but track is null.");
                // Mungkin set UI ke keadaan default atau loading
                textCurrentTime.setText("0:00");
                textTotalTime.setText("0:00");
                seekBar.setMax(0);
                seekBar.setProgress(0);
                return;
            }

            final long currentPosition = playerState.playbackPosition;
            final long totalDuration = playerState.track.duration;

            textCurrentTime.setText(formatTrackDuration(currentPosition));
            textTotalTime.setText(formatTrackDuration(totalDuration));

            seekBar.setMax((int) totalDuration);
            seekBar.setProgress((int) currentPosition);

            // Opsional: Update judul, artis, gambar jika ada perubahan dari player state
            // (jika Anda ingin info selalu sinkron dengan apa yang diputar Spotify)
            // textTitle.setText(playerState.track.name);
            // textArtist.setText(playerState.track.artist.name);
            // if (playerState.track.imageUri != null) {
            //     Glide.with(MusicPlayerActivity.this).load(playerState.track.imageUri.raw).into(imageCover);
            // }
        });

        playerStateSubscription.setErrorCallback(throwable -> {
            Log.e("MusicPlayerActivity", "Error in player state subscription", throwable);
        });
    }

    private String formatTrackDuration(long ms) {
        if (ms < 0) ms = 0; // Handle negatif jika ada
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerStateSubscription != null && !playerStateSubscription.isCanceled()) {
            playerStateSubscription.cancel();
            playerStateSubscription = null;
        }
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            spotifyAppRemote = null; // Set ke null setelah disconnect
        }
    }
}