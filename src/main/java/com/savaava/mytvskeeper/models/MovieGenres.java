package com.savaava.mytvskeeper.models;

public enum MovieGenres {
    ACTION(28, "Action"),
    ADVENTURE(12, "Adventure"),
    ANIMATION(16, "Animation"),
    COMEDY(35, "Comedy"),
    CRIME(80, "Crime"),
    DOCUMENTARY(99, "Documentary"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Family"),
    FANTASY(14, "Fantasy"),
    HISTORY(36, "History"),
    HORROR(27, "Horror"),
    MUSIC(10402, "Music"),
    MYSTERY(9648, "Mystery"),
    ROMANCE(10749, "Romance"),
    SCIENCE_FICTION(878, "Science Fiction"),
    TV_MOVIE(10770, "TV Movie"),
    THRILLER(53, "Thriller"),
    WAR(10752, "War"),
    WESTERN(37, "Western");

    private final int id;
    private final String name;

    MovieGenres(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}
    public String getName() {return name;}

    private static MovieGenres getById(int id) {
        for (MovieGenres genre : values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }
    public static boolean hasGenre(int id){
        return MovieGenres.getById(id) != null;
    }
    public static MovieGenres getGenre(int id) {
        return MovieGenres.getById(id);
    }

    private static MovieGenres getByName(String name) {
        for (MovieGenres genre : values()) {
            if (genre.getName().equalsIgnoreCase(name)) {
                return genre;
            }
        }
        return null;
    }
    public static boolean hasGenre(String name){
        return MovieGenres.getByName(name) != null;
    }
    public static MovieGenres getGenre(String name) {
        return getByName(name);
    }

    @Override
    public String toString(){
        return "("+name+"-"+id+")";
    }
}
