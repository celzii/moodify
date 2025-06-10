package com.example.moodify.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DummyGenrePlaylists {

    public static List<String> getPlaylistIdsForGenre(String genreName) {
        Map<String, List<String>> genreMap = new HashMap<>();

        // Isi genre → ID playlist
        List<String> pop = new ArrayList<>();
        pop.add("1WH6WVBwPBz35ZbWsgCpgr");
        pop.add("2xutOn4Ea4RyjuaRaD3jl3");
        genreMap.put("Pop", pop);

        List<String> rock = new ArrayList<>();
        rock.add("32bhmQAN0AZLopViEHn3zM");
        genreMap.put("Rock", rock);

        List<String> hipHop = new ArrayList<>();
        hipHop.add("2grvoqWRb6EIYX2OKmw9qa");
        hipHop.add("5Pwor0pemLMAv3ObWwY22m");
        hipHop.add("2gk0Z1b3d4a5e6f7g8h9i0j");
        hipHop.add("1oHdyFHvSujRwlbyISoEIW");
        genreMap.put("Hip-Hop", hipHop);

        List<String> jazz = new ArrayList<>();
        jazz.add("4cwnHX3jRNhFwpz8Ifa1DS");
        jazz.add("4J3uR1W1TDo3G8D97IN9Qu");
        jazz.add("3k2d4f5g6h7i8j9k0l1m2n3");
        jazz.add("6tYqtwayW2v918QJTXwJIV");
        genreMap.put("Jazz", jazz);

        return genreMap.getOrDefault(genreName, new ArrayList<>());
    }
}
