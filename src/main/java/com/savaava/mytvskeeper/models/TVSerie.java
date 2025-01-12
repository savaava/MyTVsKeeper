package com.savaava.mytvskeeper.models;

import java.time.LocalDate;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class TVSerie extends Video {
    private final int numSeasons;
    private final int numEpisodes;
    private final Set<Genre> genres;

    public TVSerie(String title, String description, LocalDate releaseDate, boolean started, boolean terminated, int rating, String id, int numSeasons, int numEpisodes) {
        super(title, description, releaseDate, started, terminated, rating, id);
        this.numSeasons = numSeasons;
        this.numEpisodes = numEpisodes;
        genres = new HashSet<>();
    }

    public TVSerie(String id) {
        this("","",LocalDate.of(2000,1,1),false,false,10,id,0,0);
    }

    public int getNumSeasons() {return numSeasons;}
    public int getNumEpisodes() {return numEpisodes;}
    public Collection<Genre> getGenres(){return genres;}

    public void addGenre(int id){
        if(!TVGenres.hasGenre(id)) return;
        genres.add(TVGenres.getGenre(id));
    }
    public int numGenres(){return genres.size();}

    @Override
    public String toString(){
        StringBuilder strb = new StringBuilder("TVSerie -> ");

        strb.append(super.toString());
        strb.append(" | num Seasons=").append(numSeasons);
        strb.append(" | num Episodes=").append(numEpisodes);
        genres.forEach(gi -> strb.append(" | ").append(gi));

        return strb.toString();
    }
}
