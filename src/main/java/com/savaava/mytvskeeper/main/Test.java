package com.savaava.mytvskeeper.main;

import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.services.TMDatabase;

public class Test {
    private static final String[] MOVIE_ID = {};
    private static final String[] TV_ID = {};

    public static void main(String[] args) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        VideoKeeper.CsvHandler vkCsv = vk.new CsvHandler();
        TMDatabase tmdb = new TMDatabase();

        for(String id : TV_ID){
            vk.addTVSerie(
                    tmdb.getTVSerieById(id)
            );
        }

//        vk.addMovie(tmdb.getMovieById("496243"));
//        vk.addMovie(tmdb.getMovieById("19995"));
//        vk.addMovie(tmdb.getMovieById("1022789"));
//        vk.addTVSerie(tmdb.getTVSerieById("94605"));
//        vk.addTVSerie(tmdb.getTVSerieById("146176"));
//        vk.addTVSerie(tmdb.getTVSerieById("76479"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("62110"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("71024"));
//        vk.addAnimeSerie(tmdb.getTVSerieById("30991"));

//        tmdb.getMoviesByName("Star wars").forEach(mi -> {
//            try {
//                vk.addMovie(tmdb.getMovieById(mi.getId()));
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//        });
//        tmdb.getTVSeriesByName("outer banks").forEach(tvi ->{
//            try{
//                vk.addTVSerie(tmdb.getTVSerieById(tvi.getId()));
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//        });
//        tmdb.getTVSeriesByName("demon slayer").forEach(tvi -> {
//            try{
//                vk.addAnimeSerie(tmdb.getTVSerieById(tvi.getId()));
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//        });

//        vkCsv.csvImportMovies(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_Movies.csv"));
//        vkCsv.csvImportTVSerie(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_TVSeries.csv"));
//        vkCsv.csvImportAnimeSerie(new File("C:/Users/andre/Desktop/tmp/MyTvsKeeper1.0_AnimeSeries.csv"));

//        System.out.println(vk);
    }
}
