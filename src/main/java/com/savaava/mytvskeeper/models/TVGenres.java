package com.savaava.mytvskeeper.models;

public enum TVGenres {
    ACTION_ADVENTURE(10759, "Action & Adventure"),
    ANIMATION(16, "Animation"),
    COMEDY(35, "Comedy"),
    CRIME(80, "Crime"),
    DOCUMENTARY(99, "Documentary"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Family"),
    KIDS(10762, "Kids"),
    MYSTERY(9648, "Mystery"),
    NEWS(10763, "News"),
    REALITY(10764, "Reality"),
    SCIFI_FANTASY(10765, "Sci-Fi & Fantasy"),
    SOAP(10766, "Soap"),
    TALK(10767, "Talk"),
    WAR_POLITICS(10768, "War & Politics"),
    WESTERN(37, "Western");

    private final int id;
    private final String name;

    TVGenres(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}
    public String getName() {return name;}

    private static TVGenres getById(int id) {
        for (TVGenres genre : values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }

    public static boolean hasGenre(int id) {
        return getById(id) != null;
    }

    public static TVGenres getGenre(int id) {
        return TVGenres.getById(id);
    }

    @Override
    public String toString(){
        return "("+name+"-"+id+")";
    }
}
