package com.example.moodify.response;

public class GenreItem {
    private String name;
    private int imageResId;

    public GenreItem(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
