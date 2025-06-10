package com.example.moodify.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.response.GenreItem;
import com.example.moodify.response.SongItem;
import com.example.moodify.ui.GenreAdapter;
import com.example.moodify.ui.SongAdapter;
import com.example.moodify.util.DummyGenres;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Executor initialized

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnRefresh = view.findViewById(R.id.btnRefresh);


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

        RecyclerView rvGenre = view.findViewById(R.id.rv_genre);
        rvGenre.setLayoutManager(new LinearLayoutManager(getContext()));

        GenreAdapter genreAdapter = new GenreAdapter(
                DummyGenres.getGenres(),
                getContext(),
                genreItem -> {
                    GenreFragment fragment = GenreFragment.newInstance(genreItem.getName());

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
        );

        rvGenre.setAdapter(genreAdapter);

        EditText searchBar = view.findViewById(R.id.search_bar);
        RecyclerView recyclerViewResults = view.findViewById(R.id.recyclerViewResults);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));

        List<SongItem> searchResults = new ArrayList<>();
        SongAdapter adapter = new SongAdapter(searchResults, getContext());
        recyclerViewResults.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    rvGenre.setVisibility(View.GONE);
                    recyclerViewResults.setVisibility(View.VISIBLE);
                    searchSpotify(s.toString(), searchResults, adapter);
                } else {
                    rvGenre.setVisibility(View.VISIBLE);
                    recyclerViewResults.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (isNetworkAvailable()) {
            btnRefresh.setVisibility(View.GONE); // Hide button if network is available
        } else {
            btnRefresh.setVisibility(View.VISIBLE); // Show button if network is unavailable
        }

        btnRefresh.setOnClickListener(v -> {
            String query = searchBar.getText().toString();
            Toast.makeText(getContext(), "Refreshing data...", Toast.LENGTH_SHORT).show();
            if (!query.isEmpty()) {
                searchSpotify(query, searchResults, adapter);
            } else {
                Toast.makeText(getContext(), "Enter a search query to refresh.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setBottomNavVisibility(true);
    }


    private void searchSpotify(String query, List<SongItem> searchResults, SongAdapter adapter) {
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            SpotifyAuthUtil.getValidAccessToken(requireContext(), new SpotifyAuthUtil.TokenCallback() {
                @Override
                public void onTokenAvailable(String accessToken) {
                    Request request = new Request.Builder()
                            .url("https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=10")
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            Log.e("SpotifyAPI", "Search error: " + response.code());
                            return;
                        }

                        JSONObject json = new JSONObject(response.body().string());
                        JSONArray tracks = json.getJSONObject("tracks").getJSONArray("items");

                        searchResults.clear();
                        for (int i = 0; i < tracks.length(); i++) {
                            JSONObject track = tracks.getJSONObject(i);
                            String title = track.getString("name");
                            String artist = track.getJSONArray("artists").getJSONObject(0).getString("name");
                            String imageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                            String uri = track.getString("uri");

                            searchResults.add(new SongItem(title, artist, imageUrl, uri));
                        }

                        requireActivity().runOnUiThread(adapter::notifyDataSetChanged); // UI updates on the main thread
                    } catch (Exception e) {
                        Log.e("SpotifyAPI", "Error during search", e);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("SpotifyAuth", "Failed to get token", e);
                }
            });
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}