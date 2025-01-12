package com.savaava.mytvskeeper.models;

import java.time.LocalDate;

public class Movie extends Video {
    private final String duration;
    private final String director;

    public Movie(String title, String description, LocalDate releaseDate, boolean started, boolean terminated, int rating, String id, String duration, String director) {
        super(title, description, releaseDate, started, terminated, rating, id);
        this.duration = duration;
        this.director = director;
    }

    public String getDuration() {return duration;}
    public String getDirector() {return director;}

    @Override
    public String toString() {
        return "Movie:"+super.toString()+
                "duration="+duration+
                "director="+director;
    }
}
