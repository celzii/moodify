package com.example.moodify.auth;

import android.content.Context;
import android.content.SharedPreferences;

// Java
public class LastPlayedManager {

    private static final String PREF_NAME = "LastPlayedPrefs";
    private static final String KEY_TITLE = "last_played_title";
    private static final String KEY_ARTIST = "last_played_artist";
    private static final String KEY_IMAGE_URL = "last_played_image_url";
    private static final String KEY_URI = "last_played_uri";

    public static void saveLastPlayed(Context context, String title, String artist, String imageUrl, String uri) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_TITLE, title)
                .putString(KEY_ARTIST, artist)
                .putString(KEY_IMAGE_URL, imageUrl)
                .putString(KEY_URI, uri)
                .apply();
    }

    public static String[] getLastPlayed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String title = prefs.getString(KEY_TITLE, null);
        String artist = prefs.getString(KEY_ARTIST, null);
        String imageUrl = prefs.getString(KEY_IMAGE_URL, null);
        String uri = prefs.getString(KEY_URI, null);
        return new String[]{title, artist, imageUrl, uri};
    }
}