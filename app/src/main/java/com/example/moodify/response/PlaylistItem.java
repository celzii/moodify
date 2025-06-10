package com.example.moodify.response;

public class PlaylistItem {
    public String name;
    public String imageUrl;
    public String uri;
    public String id;
    public int imageResId;
    public String description;
    public String author;
    public boolean isFeatured = false;


    // Constructor for URL images
    public PlaylistItem(String name, String imageUrl, String uri, String id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.uri = uri;
        this.id = id;
        this.imageResId = 0; // default
        this.description = description;
        this.author = author;
    }

    public PlaylistItem(String name, int imageResId, String uri, String id) {
        this.name = name;
        this.imageResId = imageResId;
        this.uri = uri;
        this.id = id;
        this.imageUrl = null;
        this.isFeatured = true; // karena dari drawable
    }

    public boolean isFromDrawable() {
        return imageUrl == null;
    }
    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }
    public int getImageResId() {
        return imageResId;
    }
    public String getDescription() {
        return description;
    }
    public String getAuthor() {
        return author;
    }
}

