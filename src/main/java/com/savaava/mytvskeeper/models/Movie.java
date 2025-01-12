package com.savaava.mytvskeeper.models;

import java.time.LocalDate;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class Movie extends Video {
    private final String duration;
    private final String director;
    private final Set<Genre> genres;

    public Movie(String title, String description, LocalDate releaseDate, boolean started, boolean terminated, int rating, String id, String duration, String director) {
        super(title, description, releaseDate, started, terminated, rating, id);
        this.duration = duration;
        this.director = director;
        genres = new HashSet<>();
    }

    public Movie(String id){
        this("","",LocalDate.of(2000,1,1),false,false,10,id,"","");
    }

    public String getDuration() {return duration;}
    public String getDirector() {return director;}
    public Collection<Genre> getGenres(){return genres;}

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
        strb.append(" | director=").append(director);
        genres.forEach(gi -> strb.append(" | ").append(gi));

        return strb.toString();
    }
}
