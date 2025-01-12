package com.savaava.mytvskeeper.models;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class MovieGenres {
    private final static Map<Integer, Genre> genres = new HashMap<>();

    static{
        Genre genresVett[] = {
                new Genre("Action", 28),
                new Genre("Adventure", 12),
                new Genre("Animation", 16),
                new Genre("Comedy", 35),
                new Genre("Crime", 80),
                new Genre("Documentary", 99),
                new Genre("Drama", 18),
                new Genre("Family", 10751),
                new Genre("Fantasy", 14),
                new Genre("History", 36),
                new Genre("Horror", 27),
                new Genre("Music", 10402),
                new Genre("Mystery", 9648),
                new Genre("Romance", 10749),
                new Genre("Science Fiction", 878),
                new Genre("TV Movie", 10770),
                new Genre("Thriller", 53),
                new Genre("War", 10752),
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
