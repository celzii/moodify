package com.example.moodify.auth;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class SpotifyTokenExchange {

    private static final String TAG = "SpotifyTokenExchange";

    private static final String CLIENT_ID = "7944a6bf26b84e80ad4fc02309b352e9";
    private static final String CLIENT_SECRET = "ac60c32312c14f66ab58f5e624e0e06a";
    private static final String REDIRECT_URI = "moodify://callback";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    public interface TokenCallback {
        void onSuccess(String accessToken, String refreshToken);
        void onFailure(Exception e);
    }

    public static void exchangeCodeForToken(Context context, String code, TokenCallback callback) {
        Log.d("Spotify", "exchangeCodeForToken called with code: " + code);
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build();

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(formBody)
                .addHeader("Authorization", basicAuth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Log.d("Spotify", "Raw token response: " + body);

                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(body);
                    String accessToken = json.getString("access_token");
                    String refreshToken = json.getString("refresh_token");

                    Log.d("Spotify", "AccessToken: " + accessToken);
                    Log.d("Spotify", "RefreshToken: " + refreshToken);

                    // ✅ Simpan token!
                    TokenManager.saveTokens(context, accessToken, refreshToken);

                    callback.onSuccess(accessToken, refreshToken);
                } catch (Exception e) {
                    Log.e("Spotify", "Failed to parse token response", e);
                    callback.onFailure(e);
                }
            }
        });
    }
}
