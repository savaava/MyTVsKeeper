package com.savaava.mytvskeeper.main;

import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;
import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.services.TMDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        VideoKeeper.CsvHandler vkCsv = vk.new CsvHandler();
        TMDatabase tmdb = new TMDatabase();

//        vkCsv.csvImportMovies(new File("C:/Users/andre/Desktop/csvFiles/MyTvsKeeper1.3_Movies.csv"));
    }

    private static void addVideosMarco(int index, String[] videos) throws Exception {
        VideoKeeper vk = VideoKeeper.getInstance();
        TMDatabase tmdb = new TMDatabase();

        int cont = 0;
        int contAdded = 0;
        Collection<String> videosNotFounded = new ArrayList<>();
        for(String videoName : videos){

            List<Video> videosFounded;
            if(index == 1)
                videosFounded = new ArrayList<>(tmdb.getMoviesByName(videoName));
            else
                videosFounded = new ArrayList<>(tmdb.getTVSeriesByName(videoName));

            cont++;
            if(videosFounded.isEmpty()){
                System.err.println("("+cont+") no tvs founded -> "+videoName);
                videosNotFounded.add(videoName);
                continue;
            }

            String videoIdToAdd = videosFounded.getFirst().getId();

            Movie movieToAdd;
            TVSerie tvToAdd;
            try {
                if (index == 1) {
                    movieToAdd = tmdb.getMovieById(videoIdToAdd);
                    vk.addMovie(movieToAdd);
                    System.out.println("(" + cont + ") " + videoName + " -> " + movieToAdd.getTitle() + " | Release Date=" + movieToAdd.getReleaseDate());
                } else {
                    tvToAdd = tmdb.getTVSerieById(videoIdToAdd);
                    vk.addTVSerie(tvToAdd);
                    System.out.println("(" + cont + ") " + videoName + " -> " + tvToAdd.getTitle() + " | Release Date=" + tvToAdd.getReleaseDate());
                }
                contAdded++;
            }catch(VideoAlreadyExistsException ex){
                System.err.println(videoName+" already exists");
                videosNotFounded.add(videoName);
            }

            if(cont%25 == 0){
                System.out.println("Riposo di 10 secondi...");
                Thread.sleep(10*1000);
            }
        }
        System.out.println("\nvideo added: "+contAdded+"\nMarco's video: "+cont+"\nvideo not added: "+(cont-contAdded)+"\n");
        videosNotFounded.forEach(vi -> System.out.println(vi));
    }
}
