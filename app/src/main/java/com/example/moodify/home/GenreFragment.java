package com.example.moodify.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.response.PlaylistItem;
import com.example.moodify.ui.PlaylistAdapter;
import com.example.moodify.util.DummyGenrePlaylists;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
public class GenreFragment extends Fragment {

    private static final String ARG_GENRE = "genre_name";
    private String genreName;
    private List<PlaylistItem> playlistItems = new ArrayList<>();
    private PlaylistAdapter adapter;

    public static GenreFragment newInstance(String genreName) {
        GenreFragment fragment = new GenreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genreName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genreName = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rv_genre_playlist);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new PlaylistAdapter(playlistItems, getContext(), item -> {
            PlaylistFragment fragment = PlaylistFragment.newInstance(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAuthor(),
                    item.getImageUrl(),
                    item.getImageResId(),
                    item.isFromDrawable()
            );
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        rv.setAdapter(adapter);

        playlistItems.clear();
        adapter.notifyDataSetChanged();

        SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                for (String id : DummyGenrePlaylists.getPlaylistIdsForGenre(genreName)) {
                    fetchPlaylistDetailById(id, accessToken, playlistItems, adapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Gagal token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchPlaylistDetailById(String id, String token, List<PlaylistItem> list, PlaylistAdapter adapter) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + id)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Gagal ambil playlist", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    String name = obj.getString("name");
                    String uri = obj.getString("uri");
                    String description = obj.optString("description", "");
                    String author = obj.getJSONObject("owner").optString("display_name", "Spotify");

                    JSONArray images = obj.optJSONArray("images");
                    String imageUrl = (images != null && images.length() > 0) ?
                            images.getJSONObject(0).getString("url") : null;

                    PlaylistItem item = new PlaylistItem(name, imageUrl, uri, id);
                    item.description = description;
                    item.author = author;

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        list.add(item);
                        adapter.notifyDataSetChanged();
                    });

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON error", e);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

}

