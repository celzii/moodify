package com.example.moodify.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.response.PlaylistItem;
import com.example.moodify.ui.PlaylistAdapter;
import com.example.moodify.util.DummyFeaturedPlaylists;
import com.example.moodify.util.DummyTopCharts;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private List<PlaylistItem> featuredItems = new ArrayList<>();
    private List<PlaylistItem> topChartItems = new ArrayList<>();
    private List<PlaylistItem> favoriteItems = new ArrayList<>();
    private PlaylistAdapter favoriteAdapter;
    private PlaylistAdapter featuredAdapter;
    private PlaylistAdapter topChartAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvFeatured = view.findViewById(R.id.rv_featured);
        RecyclerView rvTopChart = view.findViewById(R.id.rv_top_chart);

        rvFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTopChart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        featuredAdapter = new PlaylistAdapter(featuredItems, getContext(), this::openPlaylist);
        rvFeatured.setAdapter(featuredAdapter);

        topChartAdapter = new PlaylistAdapter(topChartItems, getContext(), this::openPlaylist);
        rvTopChart.setAdapter(topChartAdapter);

        RecyclerView rvFavorite = view.findViewById(R.id.rv_favorite);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        favoriteAdapter = new PlaylistAdapter(favoriteItems, getContext(), this::openPlaylist);
        rvFavorite.setAdapter(favoriteAdapter);

        SwitchMaterial switchBtn = view.findViewById(R.id.switchBtn);

        switchBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            MainActivity mainActivity = (MainActivity) requireActivity();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                fetchSpotifyUserProfile(accessToken);
                fetchPlaylistsByIds(DummyFeaturedPlaylists.getDummyPlaylistIds(), featuredItems, featuredAdapter, accessToken);
                fetchUserPlaylists(accessToken, favoriteItems, favoriteAdapter);
                fetchPlaylistsByIds(DummyTopCharts.getDummyTopChartIds(), topChartItems, topChartAdapter, accessToken);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Gagal ambil token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPlaylist(PlaylistItem item) {
        Toast.makeText(getContext(), "Klik: " + item.name, Toast.LENGTH_SHORT).show();
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
    }

    private void fetchSpotifyUserProfile(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyProfile", "Gagal ambil profil", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String displayName = json.getString("display_name");
                    JSONArray images = json.optJSONArray("images");
                    String imageUrl = (images != null && images.length() > 0) ? images.getJSONObject(0).getString("url") : null;

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        TextView usernameText = requireView().findViewById(R.id.username);
                        usernameText.setText(displayName);
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile)
                                    .into((de.hdodenhof.circleimageview.CircleImageView) requireView().findViewById(R.id.profile));
                        }
                    });
                } catch (JSONException e) {
                    Log.e("SpotifyProfile", "JSON error", e);
                }
            }
        });
    }

    private void fetchPlaylistsByIds(List<String> ids, List<PlaylistItem> targetList, PlaylistAdapter targetAdapter, String token) {
        targetList.clear();
        for (String id : ids) {
            fetchPlaylistDetailById(id, token, targetList, targetAdapter);
        }
    }

    private void fetchPlaylistDetailById(String id, String token, List<PlaylistItem> targetList, PlaylistAdapter targetAdapter) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + id)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Gagal ambil detail playlist", e);
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
                    String imageUrl = (images != null && images.length() > 0) ? images.getJSONObject(0).getString("url") : null;

                    PlaylistItem p = new PlaylistItem(name, imageUrl, uri, id);
                    p.description = description;
                    p.author = author;

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        targetList.add(p);
                        targetAdapter.notifyDataSetChanged();
                    });

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON error", e);
                }
            }
        });
    }

    private void fetchUserPlaylists(String token, List<PlaylistItem> targetList, PlaylistAdapter targetAdapter) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Gagal ambil playlist user", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray items = json.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);
                        String id = obj.getString("id");
                        String name = obj.getString("name");
                        String uri = obj.getString("uri");
                        String description = obj.optString("description", "");
                        String author = obj.getJSONObject("owner").optString("display_name", "Spotify");

                        JSONArray images = obj.optJSONArray("images");
                        String imageUrl = (images != null && images.length() > 0) ? images.getJSONObject(0).getString("url") : null;

                        PlaylistItem p = new PlaylistItem(name, imageUrl, uri, id);
                        p.description = description;
                        p.author = author;

                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            targetList.add(p);
                            targetAdapter.notifyDataSetChanged();
                        });
                    }

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON parsing error", e);
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