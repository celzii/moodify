package com.example.moodify.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.response.PlaylistItem;
import com.example.moodify.ui.PlaylistAdapter;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CollectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CollectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectionFragment newInstance(String param1, String param2) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setBottomNavVisibility(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                fetchSpotifyUserPhoto(accessToken);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SpotifyAuth", "Failed to get token", e);
            }
        });

        RecyclerView recyclerViewCollection = view.findViewById(R.id.recyclerViewCollection);
        recyclerViewCollection.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<PlaylistItem> likedPlaylists = new ArrayList<>();
        PlaylistAdapter adapter = new PlaylistAdapter(likedPlaylists, getContext(), item -> {
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

        recyclerViewCollection.setAdapter(adapter);

        SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                fetchLikedPlaylists(accessToken, likedPlaylists, adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SpotifyAuth", "Failed to get token", e);
            }
        });


        ImageButton lastPlayedButton = view.findViewById(R.id.last_played);
        lastPlayedButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LastPlayedActivity.class);
            intent.putExtra("title", "Last Played Song Title"); // Replace with actual data
            intent.putExtra("artist", "Last Played Artist");
            intent.putExtra("imageUrl", "Last Played Image URL");
            startActivity(intent);
        });
    }

    private void fetchLikedPlaylists(String accessToken, List<PlaylistItem> likedPlaylists, PlaylistAdapter adapter) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyAPI", "Failed to fetch liked playlists", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray items = json.getJSONArray("items");

                    likedPlaylists.clear();
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

                        likedPlaylists.add(p);
                    }

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(adapter::notifyDataSetChanged);

                } catch (JSONException e) {
                    Log.e("SpotifyAPI", "JSON parsing error", e);
                }
            }
        });
    }

    private void fetchSpotifyUserPhoto(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyProfile", "Failed to fetch profile photo", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray images = json.optJSONArray("images");
                    String imageUrl = (images != null && images.length() > 0) ? images.getJSONObject(0).getString("url") : null;

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile)
                                    .into((de.hdodenhof.circleimageview.CircleImageView) requireView().findViewById(R.id.profile));
                        }
                    });
                } catch (JSONException e) {
                    Log.e("SpotifyProfile", "JSON parsing error", e);
                }
            }
        });
    }
}