package com.savaava.mytvskeeper.main;

import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.services.TMDatabase;

import java.util.Random;

public class Test {
    private static final Random n = new Random(333);
    private static final int[] vMovie = {10759,16,35,80,99,18,10751,10762,9648,10763,10764,10765,10766,10767,10768,37};
    private static final int[] vTVSerie = {28,12,16,35,80,99,18,10751,14,36,27,10402,9648,10749,878,10770,53,10752,37};

    private static final String[] VIDEO_ID = {
            "496243",
    };

    public static void main(String[] args) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        VideoKeeper.CsvHandler vkCsv = vk.new CsvHandler();
        TMDatabase tmdb = TMDatabase.getInstance();

//        vk.addMovie(tmdb.getMovieById("496243"));
//        vk.addMovie(tmdb.getMovieById("19995"));
//        vk.addMovie(tmdb.getMovieById("1022789"));
//        vk.addTVSerie(tmdb.getTVSerieById("94605"));
//        vk.addTVSerie(tmdb.getTVSerieById("146176"));
//        vk.addTVSerie(tmdb.getTVSerieById("76479"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("62110"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("71024"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("30991"));

//        tmdb.getMoviesByName("toy story").forEach(mi -> {
//            try {
//                vk.addMovie(tmdb.getMovieById(mi.getId()));
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//        });
        tmdb.getTVSeriesByName("gli anelli del potere").forEach(tvi ->{
            try{
                vk.addTVSerie(tmdb.getTVSerieById(tvi.getId()));
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
//        tmdb.getTVSeriesByName("castlevania").forEach(tvi -> {
//            try{
//                vk.addAnimeSerie(tmdb.getTVSerieById(tvi.getId()));
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//        });

//        vkCsv.csvImportMovies(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_Movies.csv"));
//        vkCsv.csvImportTVSerie(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_TVSeries.csv"));
//        vkCsv.csvImportAnimeSerie(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_AnimeSeries.csv"));

        System.out.println(vk);
    }
}
