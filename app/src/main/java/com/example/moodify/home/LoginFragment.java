package com.example.moodify.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moodify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginFragment extends Fragment {

    public static final String CLIENT_ID = "7944a6bf26b84e80ad4fc02309b352e9";
    public static final String REDIRECT_URI = "moodify://callback";
    public static final int REQUEST_CODE = 1337;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.btnLoginSpotify);
        loginButton.setOnClickListener(v -> openSpotifyLogin());

        return view;
    }

    private void openSpotifyLogin() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(
                        CLIENT_ID,
                        AuthorizationResponse.Type.CODE,
                        REDIRECT_URI);

        builder.setScopes(new String[]{
                "playlist-read-private",
                "user-read-private",
                "user-read-email"
        });
        builder.setShowDialog(false); // ❗false = biarkan buka Spotify app jika tersedia
        AuthorizationRequest request = builder.build();
        // Buka login activity dan minta result dikirim ke MainActivity.onActivityResult()
        AuthorizationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentName", "onResume");
        ((MainActivity) getActivity()).setBottomNavVisibility(false); // or true
    }

}
