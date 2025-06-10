package com.example.moodify.response;

public class SongItem {
    public String title;
    public String artist;
    public String imageUrl;
    public String spotifyUri;
    public boolean isPlaying = false;
    public String currentTime;
    public String totalTime;

    public SongItem(String title, String artist, String imageUrl, String spotifyUri) {
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.spotifyUri = spotifyUri;
    }

    public SongItem(String title, String artist, String imageUrl, String spotifyUri, String currentTime, String totalTime) {
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.spotifyUri = spotifyUri;
        this.currentTime = currentTime;
        this.totalTime = totalTime;
    }
}



