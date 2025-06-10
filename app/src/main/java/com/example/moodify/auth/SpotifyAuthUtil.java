package com.example.moodify.auth;

import android.content.Context;
import android.util.Log;

public class SpotifyAuthUtil {

    public interface TokenCallback {
        void onTokenAvailable(String accessToken);
        void onFailure(Exception e);
    }

    public static void getValidAccessToken(Context context, TokenCallback callback) {
        String accessToken = TokenManager.getAccessToken(context);
        String refreshToken = TokenManager.getRefreshToken(context);
        long savedTime = TokenManager.getTokenSavedTime(context);
        long elapsed = (System.currentTimeMillis() - savedTime) / 1000;

        if (accessToken != null && elapsed < 3600) {
            Log.d("SpotifyAuthUtil", "✅ Token masih berlaku, pakai langsung.");
            callback.onTokenAvailable(accessToken);
        } else if (refreshToken != null) {
            Log.d("SpotifyAuthUtil", "🔁 Token expired, refresh dengan refreshToken.");
            SpotifyTokenRefresher.refreshAccessToken(context, refreshToken, new SpotifyTokenRefresher.RefreshCallback() {
                @Override
                public void onTokenRefreshed(String newAccessToken) {
                    Log.d("SpotifyAuthUtil", "✅ Token berhasil di-refresh.");
                    callback.onTokenAvailable(newAccessToken);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("SpotifyAuthUtil", "❌ Gagal refresh token", e);
                    callback.onFailure(e);
                }
            });
        } else {
            Log.w("SpotifyAuthUtil", "⚠️ Tidak ada token valid, user harus login.");
            callback.onFailure(new Exception("No valid token found."));
        }
    }
}
