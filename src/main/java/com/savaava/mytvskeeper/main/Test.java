package com.savaava.mytvskeeper.main;

import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.utility.Converter;

import java.util.Random;

public class Test {
    private static final Random n = new Random(333);
    private static final int vMovie[] = {10759,16,35,80,99,18,10751,10762,9648,10763,10764,10765,10766,10767,10768,37};
    private static final int vTVSerie[] = {28,12,16,35,80,99,18,10751,14,36,27,10402,9648,10749,878,10770,53,10752,37};

    public static void main(String[] args) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        TMDatabase tmdb = TMDatabase.getInstance();

//        System.out.println(vk);

//        System.out.println(tmdb.getMoviesByName(" \nCowboy bebop \n  \n  "));
//        System.out.println(tmdb.getTVSeriesByName(" \nCowboy bebop \n  \n  "));

//        vk.addMovie(tmdb.getMovieById("496243"));
//        vk.addMovie(tmdb.getMovieById("19995"));
//        vk.addMovie(tmdb.getMovieById("1022789"));
//        vk.addTVSerie(tmdb.getTVSerieById("94605"));
//        vk.addTVSerie(tmdb.getTVSerieById("146176"));
//        vk.addTVSerie(tmdb.getTVSerieById("76479"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("62110"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("71024"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("30991"));

        byte[] rawImage = tmdb.getTVSerieBackdropById("13916");
        System.out.println(Converter.bytesToImage(rawImage));



//        System.out.println(vk);
    }


    private static Movie getRandomMovie(){
        String title = "Movie "+(n.nextInt(1000) + 1);
        String description = "Description for "+title;
        String releaseDate = n.nextInt(30)+1990+"-"+n.nextInt(12)+1+"-"+n.nextInt(28)+1;
        boolean started = n.nextBoolean();
        boolean terminated = n.nextBoolean();
        String rating = n.nextInt(10) + 1+"";
        String id = Integer.toString(n.nextInt(100000)+1);
        String duration = (n.nextInt(120)+60)+" min";
        String director = "Director "+(n.nextInt(100)+1);

        Movie movie = new Movie(title, description, releaseDate, started, terminated, rating, id, duration, director);

        int numberOfGenres = n.nextInt(4)+1;
        for (int i = 0; i < numberOfGenres; i++) {
            movie.addGenre(vMovie[n.nextInt(vMovie.length)]);
        }

        return movie;
    }
    private static TVSerie getRandomTVSerie(){
        String title = "TVSerie "+(n.nextInt(1000) + 1);
        String description = "Description for "+title;
        String releaseDate = n.nextInt(30)+1990+"-"+n.nextInt(12)+1+"-"+n.nextInt(28)+1;
        boolean started = n.nextBoolean();
        boolean terminated = n.nextBoolean();
        String rating = n.nextInt(10) + 1+"";
        String id = Integer.toString(n.nextInt(100000)+1);
        int numSeasons = n.nextInt(10)+1;
        int numEpisodes = n.nextInt(10)*numSeasons;

        TVSerie tv = new TVSerie(title, description, releaseDate, started, terminated, rating, id, numSeasons, numEpisodes);

        int numberOfGenres = n.nextInt(4)+1;
        for (int i = 0; i < numberOfGenres; i++) {
            tv.addGenre(vTVSerie[n.nextInt(vTVSerie.length)]);
        }

        return tv;
    }
}
