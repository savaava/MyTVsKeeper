package com.savaava.mytvskeeper.models;

import java.time.LocalDate;

public class Anime extends Video {
    private final int numSeasons;
    private final int numEpisodes;

    public Anime(String title, String description, LocalDate releaseDate, boolean started, boolean terminated, int rating, String id, int numSeasons, int numEpisodes) {
        super(title, description, releaseDate, started, terminated, rating, id);
        this.numSeasons = numSeasons;
        this.numEpisodes = numEpisodes;
    }

    public int getNumSeasons() {
        return numSeasons;
    }
    public int getNumEpisodes() {
        return numEpisodes;
    }

    @Override
    public String toString(){
        return "Anime:"+super.toString()+
                "num Seasons="+numSeasons+
                "num Episodes="+numEpisodes;
    }
}
