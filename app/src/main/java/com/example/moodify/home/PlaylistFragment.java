package com.example.moodify.home;

import static okhttp3.internal.concurrent.TaskLoggerKt.formatDuration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import static okhttp3.internal.concurrent.TaskLoggerKt.formatDuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.response.SongItem;
import com.example.moodify.ui.SongAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaylistFragment extends Fragment {

    private static final String CLIENT_ID = "7944a6bf26b84e80ad4fc02309b352e9";
    private static final String REDIRECT_URI = "moodify://callback";

    private SpotifyAppRemote spotifyAppRemote;
    private RecyclerView recyclerView;
    private String playlistId;
    private String playlistTitle;
    private String playlistDesc;
    private String playlistAuthor;
    private String playlistImageUrl;
    private boolean isFeatured = false;
    private int playlistImageResId = 0;
    private long currentTimeMs; // Variabel untuk menyimpan waktu saat ini
    private String currentTime; // Variabel untuk menyimpan waktu dalam format menit:detik

    public static PlaylistFragment newInstance(String id, String title, String description, String author, String imageUrl, int imageResId, boolean isFeatured) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString("playlist_id", id);
        args.putString("playlist_title", title);
        args.putString("playlist_desc", description);
        args.putString("playlist_author", author);
        args.putString("playlist_image", imageUrl);
        args.putInt("playlist_image_res", imageResId);
        args.putBoolean("is_featured", isFeatured);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getString("playlist_id");
            playlistTitle = getArguments().getString("playlist_title");
            playlistDesc = getArguments().getString("playlist_desc");
            playlistAuthor = getArguments().getString("playlist_author");
            playlistImageUrl = getArguments().getString("playlist_image");
            playlistImageResId = getArguments().getInt("playlist_image_res", 0);
            isFeatured = getArguments().getBoolean("is_featured", false);
        }
        Log.d("PlaylistFragment", "playlistId: " + playlistId + ", isFeatured: " + isFeatured);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleText = view.findViewById(R.id.textPlaylistTitle);
        TextView descText = view.findViewById(R.id.textPlaylistCaption);
        TextView authorText = view.findViewById(R.id.textPlaylistAuthor);
        ImageView imageCover = view.findViewById(R.id.imagePlaylistCover);
        ImageView authorImage = view.findViewById(R.id.profile);

        titleText.setText(playlistTitle);
        descText.setText(playlistDesc);
        authorText.setText(playlistAuthor);

        // Set cover image
        if (playlistImageUrl != null && !playlistImageUrl.isEmpty()) {
            Glide.with(this).load(playlistImageUrl).placeholder(R.drawable.spotify).into(imageCover);
        } else if (playlistImageResId != 0) {
            imageCover.setImageResource(playlistImageResId);
        } else {
            imageCover.setImageResource(R.drawable.spotify);
        }

        // Tombol kembali
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Ambil lagu
        if (playlistId == null || playlistId.isEmpty()) {
            Log.e("PlaylistFragment", "playlistId kosong");
            return;
        }


        SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                fetchTracksFromSpotify(playlistId, accessToken);
                fetchPlaylistDetailsFromSpotify(playlistId, accessToken);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SpotifyAuth", "Gagal ambil token", e);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(requireContext(), connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote remote) {
                spotifyAppRemote = remote;
                Log.d("SpotifyRemote", "Connected to Spotify");



                // ***** MOVE THE PLAYER STATE LOGIC HERE *****
                if (spotifyAppRemote != null && spotifyAppRemote.isConnected()) {
                    spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                        currentTimeMs = playerState.playbackPosition;
                        // Assuming formatDuration(long ms) is your utility method.
                        // If it's the one from okhttp3, ensure it's the correct one you intend to use.
                        // For clarity, you might want to call your own formatDuration directly.
                        currentTime = formatDuration(currentTimeMs); // Simpan waktu dalam format menit:detik
                        Log.d("CurrentTime", "Current time: " + currentTime);
                    });
                    if (recyclerView.getAdapter() instanceof SongAdapter) {
                        ((SongAdapter) recyclerView.getAdapter()).updateSpotifyRemote(spotifyAppRemote);
                    }
                } else {
                    Log.e("SpotifyRemote", "Connected, but spotifyAppRemote is null or not connected.");
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("SpotifyRemote", "Failed to connect", error);
                // Handle connection failure (e.g., show a message to the user)
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
        }
    }

    private void fetchTracksFromSpotify(String playlistId, String token) {
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Gagal ambil tracks", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("SpotifyAPI", "Gagal ambil tracks: " + response.code());
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray items = json.getJSONArray("items");

                    List<SongItem> songList = new ArrayList<>();
                    int totalTracks = items.length();
                    long totalDurationMs = 0;

                    for (int i = 0; i < totalTracks; i++) {
                        JSONObject trackObj = items.getJSONObject(i).getJSONObject("track");

                        String title = trackObj.getString("name");
                        String uri = trackObj.getString("uri");
                        String albumName = trackObj.getJSONObject("album").getString("name");

                        JSONArray images = trackObj.getJSONObject("album").getJSONArray("images");
                        String imageUrl = images.length() > 0 ? images.getJSONObject(0).getString("url") : null;

                        String artist = trackObj.getJSONArray("artists")
                                .getJSONObject(0)
                                .getString("name");

                        long duration = trackObj.getLong("duration_ms");
                        totalDurationMs += duration;



                        songList.add(new SongItem(title, artist, imageUrl, uri));
                    }


                    String formattedDuration = formatDuration(totalDurationMs);
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        TextView songCount = requireView().findViewById(R.id.textSongCount);
                        TextView durationText = requireView().findViewById(R.id.textPlaylistDuration);

                        songCount.setText(totalTracks + " lagu");
                        durationText.setText(formattedDuration);

                        recyclerView.setAdapter(new SongAdapter(songList, getContext()));
                    });

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON parsing error", e);
                }
            }
        });
    }

    private void fetchPlaylistDetailsFromSpotify(String playlistId, String accessToken) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + playlistId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Gagal ambil detail playlist", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("SpotifyAPI", "Gagal ambil detail, code: " + response.code());
                    return;
                }

                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONObject owner = obj.getJSONObject("owner");
                    String author = owner.optString("display_name", "Spotify");
                    String userId = owner.getString("id");

                    fetchUserProfileImage(userId, author, accessToken);
                    String desc = obj.optString("description", "");

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        TextView authorText = requireView().findViewById(R.id.textPlaylistAuthor);
                        TextView descText = requireView().findViewById(R.id.textPlaylistCaption);
                        authorText.setText(author);
                        descText.setText(desc);
                    });

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON parsing error", e);
                }
            }

            private void fetchUserProfileImage(String userId, String displayName, String token) {
                OkHttpClient client = new OkHttpClient();

                Request userRequest = new Request.Builder()
                        .url("https://api.spotify.com/v1/users/" + userId)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                client.newCall(userRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("SpotifyAPI", "Gagal ambil profil user", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("SpotifyAPI", "User fetch gagal: " + response.code());
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray images = json.optJSONArray("images");
                            String photoUrl = null;

                            if (images != null && images.length() > 0) {
                                photoUrl = images.getJSONObject(0).getString("url");
                            }

                            String finalPhotoUrl = photoUrl;
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                TextView authorText = requireView().findViewById(R.id.textPlaylistAuthor);
                                ImageView authorImage = requireView().findViewById(R.id.profile);

                                authorText.setText(displayName);

                                if (finalPhotoUrl != null) {
                                    Glide.with(requireContext())
                                            .load(finalPhotoUrl)
                                            .placeholder(R.drawable.spotify)
                                            .circleCrop()
                                            .into(authorImage);
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("SpotifyAPI", "Gagal parsing user", e);
                        }
                    }
                });
            }
        });
    }
    private String formatDuration(long ms) {
        long totalSec = ms / 1000;
        long hours = totalSec / 3600;
        long minutes = (totalSec % 3600) / 60;

        if (hours > 0) return hours + " jam " + minutes + " mnt";
        else return minutes + " menit";
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentName", "onResume");
        ((MainActivity) getActivity()).setBottomNavVisibility(false);
    }

}
