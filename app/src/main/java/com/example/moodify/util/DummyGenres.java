package com.example.moodify.util;

import com.example.moodify.R;
import com.example.moodify.response.GenreItem;

import java.util.ArrayList;
import java.util.List;

public class DummyGenres {

    public static List<GenreItem> getGenres() {
        List<GenreItem> genres = new ArrayList<>();
        genres.add(new GenreItem("Pop", R.drawable.spotify));
        genres.add(new GenreItem("Rock", R.drawable.spotify));
        genres.add(new GenreItem("Hip-Hop", R.drawable.spotify));
        genres.add(new GenreItem("Jazz", R.drawable.spotify));
        return genres;
    }
}
