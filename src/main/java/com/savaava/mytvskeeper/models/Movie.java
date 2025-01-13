package com.savaava.mytvskeeper.models;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class Movie extends Video {
    private final String duration;
    private final String director;
    private final Set<MovieGenres> genres;

    public Movie(String title, String description, String releaseDate, boolean started, boolean terminated, int rating, String id, String duration, String director) {
        super(title, description, releaseDate, started, terminated, rating, id);
        this.duration = duration;
        this.director = director;
        genres = new HashSet<>();
    }
    public Movie(String title, String description, String releaseDate, String id) {
        this(title, description, releaseDate, false, false, -1, id, "", "");
    }
    public Movie(String id) {
        this("","","",id);
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
