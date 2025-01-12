package com.savaava.mytvskeeper.main;

import com.savaava.mytvskeeper.models.*;

import java.time.LocalDate;
import java.util.Random;

public class Test {
    private static final Random n = new Random(333);
    private static final int vMovie[] = new int[19];
    private static final int vTVSerie[] = new int[16];

    static {
        int i = 0;
        for(Genre gi : MovieGenres.getAllGenres()){
            vMovie[i] = gi.id(); i++;
        }

        i = 0;
        for(Genre gi : TVGenres.getAllGenres()){
            vTVSerie[i] = gi.id(); i++;
        }
    }

    public static void main(String[] args) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        System.out.println(vk);

//        for(int i=0; i<2; i++){
//            vk.addMovie(getRandomMovie());
//            vk.addTVSerie(getRandomTVSerie());
//            vk.addAnimeSerie(getRandomTVSerie());
//        }
//        System.out.println(vk);

//        vk.csvExportMovies("./bin/Movies.csv");
//        vk.csvExportTVSeries("./bin/TVSeries.csv");
//        vk.csvExportAnimeSeries("./bin/AnimeSeries.csv");

        TMDatabase tmdb = TMDatabase.getInstance();
        tmdb.getMovieById("1893");
    }

    private static Movie getRandomMovie(){
        String title = "Movie "+(n.nextInt(1000) + 1);
        String description = "Description for "+title;
        LocalDate releaseDate = LocalDate.of(
                n.nextInt(30) + 1990,
                n.nextInt(12) + 1,
                n.nextInt(28) + 1
        );
        boolean started = n.nextBoolean();
        boolean terminated = n.nextBoolean();
        int rating = n.nextInt(10) + 1;
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
        LocalDate releaseDate = LocalDate.of(
                n.nextInt(30) + 1990,
                n.nextInt(12) + 1,
                n.nextInt(28) + 1
        );
        boolean started = n.nextBoolean();
        boolean terminated = n.nextBoolean();
        int rating = n.nextInt(10) + 1;
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
