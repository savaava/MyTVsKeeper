package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;

import java.util.ArrayList;
import java.util.Collection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;

import com.savaava.mytvskeeper.utility.FormatString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VideoKeeper {
    private static VideoKeeper instance;
    ObservableList<Movie> movies;
    ObservableList<TVSerie> tvSeries;
    ObservableList<TVSerie> animeSeries;
    private final File fileDataMovies = new File("./bin/DataMovies.bin");
    private final File fileDataTv = new File("./bin/DataTv.bin");
    private final File fileDataAnime = new File("./bin/DataAnime.bin");
    private final String delim = ";";
    private final String delimAux = ",";


    private VideoKeeper() throws Exception {
        movies = FXCollections.observableArrayList();
        tvSeries = FXCollections.observableArrayList();
        animeSeries = FXCollections.observableArrayList();

        if(fileDataMovies.exists())
            loadMovies();

        if(fileDataTv.exists())
            loadTVSeries();

        if(fileDataAnime.exists())
            loadAnimeSeries();
    }

    public static VideoKeeper getInstance() throws Exception {
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
    public ObservableList<TVSerie> getAnimeSeries() {
        return animeSeries;
    }

    public int moviesNumber() {
        return movies.size();
    }
    public int tvsNumber() { return tvSeries.size(); }
    public int animesNumber() { return animeSeries.size(); }

    public void addMovie(Movie movie) throws IOException, VideoAlreadyExistsException {
        if(movies.contains(movie))
            throw new VideoAlreadyExistsException();
        movies.add(movie);
        saveMovies();
    }
    public void addTVSerie(TVSerie tvSerie) throws IOException, VideoAlreadyExistsException {
        if(tvSeries.contains(tvSerie))
            throw new VideoAlreadyExistsException();
        tvSeries.add(tvSerie);
        saveTVSeries();
    }
    public void addAnimeSerie(TVSerie anime) throws IOException, VideoAlreadyExistsException {
        if(animeSeries.contains(anime))
            throw new VideoAlreadyExistsException();
        animeSeries.add(anime);
        saveAnimeSeries();
    }

    public void removeMovie(String id) throws IOException {
        if(! movies.remove(new Movie(id)))
            return ;
        if(movies.isEmpty())
            fileDataMovies.delete();
        else
            saveMovies();
    }
    public void removeTVSerie(String id) throws IOException {
        if(! tvSeries.remove(new TVSerie(id)))
            return ;
        if(tvSeries.isEmpty())
            fileDataTv.delete();
        else
            saveTVSeries();
    }
    public void removeAnimeSerie(String id) throws IOException {
        if(! animeSeries.remove(new TVSerie(id)))
            return ;
        if(animeSeries.isEmpty())
            fileDataAnime.delete();
        else
            saveAnimeSeries();
    }


    public void saveMovies() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataMovies)))){
            oos.writeObject(new ArrayList<>(movies));
        }
    }
    public void loadMovies() throws Exception {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileDataMovies)))){
            movies.setAll((Collection<Movie>)ois.readObject());
        }
    }

    public void saveTVSeries() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataTv)))){
            oos.writeObject(new ArrayList<>(tvSeries));
        }
    }
    public void loadTVSeries() throws Exception {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileDataTv)))){
            tvSeries.setAll((Collection<TVSerie>)ois.readObject());
        }
    }

    public void saveAnimeSeries() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataAnime)))){
            oos.writeObject(new ArrayList<>(animeSeries));
        }
    }
    public void loadAnimeSeries() throws Exception {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileDataAnime)))){
            animeSeries.setAll((Collection<TVSerie>)ois.readObject());
        }
    }

    public void csvExportMovies(String pathDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pathDest)))){
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","DURATION","RELEASE DATE","DIRECTOR","STARTED","TERMINATED","RATING",genresStr,"ID"));
            for(Movie mi : movies) {
                pw.append(FormatString.stringNormalize(mi.getTitle())).append(delim);
                pw.append(mi.getDuration()).append(delim);
                pw.append(mi.getReleaseDate().toString()).append(delim);
                pw.append(mi.getDirector()).append(delim);
                pw.append(mi.isStarted()?"Started":"Not Started").append(delim);
                pw.append(mi.isTerminated()?"Terminated":"Not Terminated").append(delim);
                pw.append(mi.getRating()).append(delim);
                int i = 0;
                for(MovieGenres gi : mi.getGenres()){
                    pw.append(gi.getName()); i++;
                    if(i!=mi.numGenres())
                        pw.append(delimAux);
                }
                pw.append(delim);
                pw.append(mi.getId()).append("\n");
            }
        }
    }
    private void csvExportTV(Collection<TVSerie> c, String pathFileDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(pathFileDest)))){
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","#SEASONS"+delimAux+"#EPISODES","RELEASE DATE","STARTED","TERMINATED","RATING",genresStr,"ID"));
            for(TVSerie tvi : c) {
                pw.append(FormatString.stringNormalize(tvi.getTitle())).append(delim);
                pw.append(Integer.toString(tvi.getNumSeasons())).append(delimAux);
                pw.append(Integer.toString(tvi.getNumEpisodes())).append(delim);
                pw.append(tvi.getReleaseDate().toString()).append(delim);
                pw.append(tvi.isStarted()?"Started":"Not Started").append(delim);
                pw.append(tvi.isTerminated()?"Terminated":"Not Terminated").append(delim);
                pw.append(tvi.getRating()).append(delim);
                int i = 0;
                for(TVGenres gi : tvi.getGenres()){
                    pw.append(gi.getName()); i++;
                    if(i!=tvi.numGenres())
                        pw.append(delimAux);
                }
                pw.append(delim);
                pw.append(tvi.getId()).append("\n");
            }
        }
    }
    public void csvExportTVSeries(String pathFileDest) throws IOException {
        csvExportTV(tvSeries, pathFileDest);
    }
    public void csvExportAnimeSeries(String pathFileDest) throws IOException {
        csvExportTV(animeSeries, pathFileDest);
    }


    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder("**** Stampa instanza video keeper ****\n");

        strb.append("** ").append(movies.size()).append(" Movies:\n");
        movies.forEach(m -> strb.append("* ").append(m.toString()));
        strb.append("** ").append(tvSeries.size()).append(" TV Series:\n");
        tvSeries.forEach(t -> strb.append("* ").append(t.toString()));
        strb.append("** ").append(animeSeries.size()).append(" Anime Series:\n");
        animeSeries.forEach(a -> strb.append("* ").append(a.toString()));

        return strb.toString();
    }
}
