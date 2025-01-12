package com.savaava.mytvskeeper.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import java.util.Map;
//import java.util.HashMap;

import java.io.*;

public class VideoKeeper {
    private static VideoKeeper instance;
    ObservableList<Movie> movies;
    ObservableList<TVSerie> tvSeries;
    ObservableList<Anime> animeSeries;
    private final String pathDataMovies = "./bin/DataMovies.bin";
    private final String pathDataTv = "./bin/DataTv.bin";
    private final String pathDataAnime = "./bin/DataAnime.bin";
    private final String pathConfig = "./bin/Config.bin";


    private VideoKeeper() {
        movies = FXCollections.observableArrayList();
        tvSeries = FXCollections.observableArrayList();
        animeSeries = FXCollections.observableArrayList();

    }


    public static VideoKeeper getInstance() {
        if(instance == null) {
            instance = new VideoKeeper();
        }
        return instance;
    }


    public ObservableList<Movie> getMovies() {
        return movies;
    }
    public ObservableList<TVSerie> getTvSeries() {
        return tvSeries;
    }
    public ObservableList<Anime> getAnimeSeries() {
        return animeSeries;
    }


    public void saveMovies() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pathDataMovies)))){
            oos.writeObject(movies);
        }
    }
    public void loadMovies() throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathDataMovies)))){
            movies.setAll((ObservableList<Movie>)ois.readObject());
        }
    }

    public void saveTVSeries() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pathDataTv)))){
            oos.writeObject(tvSeries);
        }
    }
    public void loadTVSeries() throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathDataTv)))){
            tvSeries.setAll((ObservableList<TVSerie>)ois.readObject());
        }
    }

    public void saveAnimeSeries() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pathDataAnime)))){
            oos.writeObject(animeSeries);
        }
    }
    public void loadAnimeSeries() throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathDataAnime)))){
            animeSeries.setAll((ObservableList<Anime>)ois.readObject());
        }
    }

    public void csvExportMovies(String pathDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pathDest)))){
            pw.println("TITLE|DURATION|RELEASE DATE|DIRECTOR|STARTED|TERMINATED|RATING|ID");
            for(Movie mi : movies){
                pw.append(mi.getTitle()).append("|");
                pw.append(mi.getDuration()).append("|");
                pw.append(mi.getReleaseDate().toString()).append("|");
                pw.append(mi.getDirector()).append("|");
                pw.append(mi.isStarted()?"Started":"Not Started").append("|");
                pw.append(mi.isTerminated()?"Terminated":"Not Terminated").append("|");
                pw.append(Integer.toString(mi.getRating())).append("|");
                pw.append(mi.getId()).append("\n");
            }
        }
    }
    public void csvExportTVSeries(String pathDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pathDest)))){
            pw.println("");
        }
    }
    public void csvExportAnimeSeries(String pathDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pathDest)))){
            pw.println("");
        }
    }


    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append("Movies:\n");
        movies.forEach(m -> strb.append(m.toString()).append("\n"));
        strb.append("TV Series:\n");
        tvSeries.forEach(t -> strb.append(t.toString()).append("\n"));
        strb.append("Anime Series:\n");
        animeSeries.forEach(a -> strb.append(a.toString()).append("\n"));

        return strb.toString();
    }
}
