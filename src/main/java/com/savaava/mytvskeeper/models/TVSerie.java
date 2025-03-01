package com.savaava.mytvskeeper.models;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class TVSerie extends Video {
    private final int numSeasons;
    private final int numEpisodes;
    private final Set<TVGenres> genres;

    public TVSerie(String title, String description, String releaseDate, boolean started, boolean terminated, Double rating, String id, String pathImage, String notes, int numSeasons, int numEpisodes) {
        super(title, description, releaseDate, started, terminated, rating, id, pathImage, notes);
        this.numSeasons = numSeasons;
        this.numEpisodes = numEpisodes;
        genres = new HashSet<>();
    }
    public TVSerie(String title, String description, String releaseDate, String id, String pathImage, int numSeasons, int numEpisodes) {
        this(title,description,releaseDate,false,false,null,id,pathImage,"",numSeasons,numEpisodes);
    }
    /**
     * Constructor for https requests without id
     */
    public TVSerie(String title, String description, String releaseDate, String id, String pathImage) {
        this(title,description,releaseDate,false,false,null,id,pathImage,"",0,0);
    }
    public TVSerie(String id) {
        this("","","",id,"");
    }

    public int getNumSeasons() {return numSeasons;}
    public int getNumEpisodes() {return numEpisodes;}
    public Collection<TVGenres> getGenres(){return genres;}

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
        strb.append(" | num Episodes=").append(numEpisodes).append(" | ");
        genres.forEach(gi -> strb.append(gi).append(", "));
        strb.append("\n");

        return strb.toString();
    }
}
