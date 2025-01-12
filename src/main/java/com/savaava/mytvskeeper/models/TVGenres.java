package com.savaava.mytvskeeper.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TVGenres {
    private final static Map<Integer, Genre> genres = new HashMap<>();

    static {
        Genre genresVett[] = {
                new Genre("Action & Adventure", 10759),
                new Genre("Animation", 16),
                new Genre("Comedy", 35),
                new Genre("Crime", 80),
                new Genre("Documentary", 99),
                new Genre("Drama", 18),
                new Genre("Family", 10751),
                new Genre("Kids", 10762),
                new Genre("Mystery", 9648),
                new Genre("News", 10763),
                new Genre("Reality", 10764),
                new Genre("Sci-Fi & Fantasy", 10765),
                new Genre("Soap", 10766),
                new Genre("Talk", 10767),
                new Genre("War & Politics", 10768),
                new Genre("Western", 37)
        };

        for(Genre genre : genresVett)
            genres.put(genre.id(), genre);
    }

    public static Collection<Genre> getAllGenres(){
        return genres.values();
    }

    public static boolean hasGenre(int id){
        return genres.containsKey(id);
    }

    public static Genre getGenre(int id){
        return genres.get(id);
    }

    public static String getGenreName(int id){
        return genres.get(id).name();
    }
}
