package com.example.moodify.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.moodify.R;
import com.example.moodify.auth.SpotifyAuthUtil;
import com.example.moodify.auth.SpotifyTokenExchange;
import com.example.moodify.auth.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TokenManager.clearTokens(this);

        SpotifyAuthUtil.getValidAccessToken(this, new SpotifyAuthUtil.TokenCallback() {
            @Override
            public void onTokenAvailable(String accessToken) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }

            @Override
            public void onFailure(Exception e) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_favorite) {
                selectedFragment = new CollectionFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginFragment.REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                case CODE:
                    String code = response.getCode();
                    Log.d("Spotify", "Authorization code: " + code);

                    SpotifyTokenExchange.exchangeCodeForToken(MainActivity.this, code, new SpotifyTokenExchange.TokenCallback() {
                        @Override
                        public void onSuccess(String accessToken, String refreshToken) {
                            TokenManager.saveTokens(MainActivity.this, accessToken, refreshToken);

                            runOnUiThread(() -> {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new HomeFragment())
                                        .commit();
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("LoginSpotify", "Token gagal di-exchange", e);
                        }
                    });
                    break;

                case ERROR:
                    Log.e("SpotifyLogin", "Login error: " + response.getError());
                    Toast.makeText(this, "Login error: " + response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.d("SpotifyLogin", "Login dibatalkan.");
                    Toast.makeText(this, "Login dibatalkan.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        if (uri != null && uri.toString().startsWith("moodify://callback")) {
            String fragment = uri.getFragment();
            if (fragment != null && fragment.contains("access_token")) {
                String[] parts = fragment.split("&");
                for (String part : parts) {
                    if (part.startsWith("access_token=")) {
                        String token = part.replace("access_token=", "");

                        SharedPreferences prefs = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE);
                        prefs.edit().putString("access_token", token).apply();

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                        return;
                    }
                }
            } else {
                Toast.makeText(this, "Login gagal: token tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // In MainActivity.java
    public void setBottomNavVisibility(boolean visible) {
        Log.d("MainActivity", "setBottomNavVisibility: " + visible);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}