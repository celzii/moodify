package com.example.moodify.auth;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class SpotifyTokenRefresher {

    private static final String CLIENT_ID = "7944a6bf26b84e80ad4fc02309b352e9";
    private static final String CLIENT_SECRET = "ac60c32312c14f66ab58f5e624e0e06a";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    public interface RefreshCallback {
        void onTokenRefreshed(String newAccessToken);
        void onFailure(Exception e);
    }

    public static void refreshAccessToken(Context context, String refreshToken, RefreshCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(body)
                .header("Authorization", basicAuth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String newAccessToken = json.getString("access_token");
                    TokenManager.saveTokens(context, newAccessToken, refreshToken);
                    callback.onTokenRefreshed(newAccessToken);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }
}
