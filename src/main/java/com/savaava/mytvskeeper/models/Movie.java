package com.savaava.mytvskeeper.models;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class Movie extends Video {
    private final String duration;
    private final String director;
    private final Set<MovieGenres> genres;

    public Movie(String title, String description, String releaseDate, boolean started, boolean terminated, String rating, String id, String pathImage, String duration, String director) {
        super(title, description, releaseDate, started, terminated, rating, id, pathImage);
        this.duration = duration;
        this.director = director;
        genres = new HashSet<>();
    }
    public Movie(String title, String description, String releaseDate, String id, String pathImage, String duration, String director) {
        this(title, description, releaseDate, false, false, "", id, pathImage, duration, director);
    }
    /**
     * Constructor for https requests without id
     */
    public Movie(String title, String description, String releaseDate, String id, String pathImage) {
        this(title, description, releaseDate, false, false, "", id, pathImage, "", "");
    }
    public Movie(String id) {
        this("","","",id,"");
    }

    public String getDuration() {return duration;}
    public String getDirector() {return director;}
    public Collection<MovieGenres> getGenres(){return genres;}

    public void addGenre(int id){
        if(!MovieGenres.hasGenre(id)) return;
        genres.add(MovieGenres.getGenre(id));
    }
    public int numGenres(){return genres.size();}

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder("Movie -> ");

        strb.append(super.toString());
        strb.append(" | duration=").append(duration);
        strb.append(" | director=").append(director).append(" | ");
        genres.forEach(gi -> strb.append(gi).append(", "));
        strb.append("\n");

        return strb.toString();
    }
}
