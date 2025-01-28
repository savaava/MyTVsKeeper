package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.savaava.mytvskeeper.utility.FormatString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VideoKeeper {
    private static VideoKeeper instance;

    ObservableList<Movie> movies;
    ObservableList<TVSerie> tvSeries;
    ObservableList<TVSerie> animeSeries;

    private final SerializedPersistenceHandler persistenceHandler;

    private final String delim = ";";
    private final String delimAux = ",";


    private VideoKeeper() throws Exception {
        movies = FXCollections.observableArrayList();
        tvSeries = FXCollections.observableArrayList();
        animeSeries = FXCollections.observableArrayList();

        persistenceHandler = new SerializedPersistenceHandler();

        if(persistenceHandler.dataMoviesExists())
            persistenceHandler.loadMovies();

        if(persistenceHandler.dataTvExists())
            persistenceHandler.loadTVSeries();

        if(persistenceHandler.dataAnimeExists())
            persistenceHandler.loadAnimeSeries();
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
            throw new VideoAlreadyExistsException("The movie you are trying to add already exists");
        movies.add(movie);
        persistenceHandler.saveMovies();
    }
    public void addTVSerie(TVSerie tvSerie) throws IOException, VideoAlreadyExistsException {
        if(tvSeries.contains(tvSerie))
            throw new VideoAlreadyExistsException("The Serie you are trying to add already exists");
        tvSeries.add(tvSerie);
        persistenceHandler.saveTVSeries();
    }
    public void addAnimeSerie(TVSerie anime) throws IOException, VideoAlreadyExistsException {
        if(animeSeries.contains(anime))
            throw new VideoAlreadyExistsException("The Anime you are trying to add already exists");
        animeSeries.add(anime);
        persistenceHandler.saveAnimeSeries();
    }

    public void removeMovie(String id) throws IOException {
        if(! movies.remove(new Movie(id)))
            return ;
        if(movies.isEmpty())
            persistenceHandler.deleteDataMovies();
        else
            persistenceHandler.saveMovies();
    }
    public void removeTVSerie(String id) throws IOException {
        if(! tvSeries.remove(new TVSerie(id)))
            return ;
        if(tvSeries.isEmpty())
            persistenceHandler.deleteDataTVs();
        else
            persistenceHandler.saveTVSeries();
    }
    public void removeAnimeSerie(String id) throws IOException {
        if(! animeSeries.remove(new TVSerie(id)))
            return ;
        if(animeSeries.isEmpty())
            persistenceHandler.deleteDataAnimes();
        else
            persistenceHandler.saveAnimeSeries();
    }

    /* Inner class for the saving and loading operations, supporting the logic separation and information hiding. */
    private class SerializedPersistenceHandler {
        private static final String MOVIES_FILE_PATH = "./bin/DataMovies.bin";
        private static final String TVS_FILE_PATH = "./bin/DataTv.bin";
        private static final String ANIMES_FILE_PATH = "./bin/DataAnime.bin";

        private final File fileDataMovies = new File(MOVIES_FILE_PATH);
        private final File fileDataTvs = new File(TVS_FILE_PATH);
        private final File fileDataAnimes = new File(ANIMES_FILE_PATH);

        public boolean dataMoviesExists() {
            return fileDataMovies.exists();
        }
        public boolean dataTvExists() {
            return fileDataTvs.exists();
        }
        public boolean dataAnimeExists() {
            return fileDataAnimes.exists();
        }

        public void deleteDataMovies() {
            fileDataMovies.delete();
        }
        public void deleteDataTVs() {
            fileDataTvs.delete();
        }
        public void deleteDataAnimes() {
            fileDataAnimes.delete();
        }

        /**
         * Saves a collection of videos to the specified file for the data persistence.
         * @param collectionToWrite the collection to serialize
         * @param fileDest the destination file
         * @throws IOException if an I/O error occurs
         */
        private <T extends Video> void saveVideos(Collection<T> collectionToWrite, File fileDest) throws IOException {
            try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDest)))){
                oos.writeObject(new ArrayList<>(collectionToWrite));
            }
        }
        public void saveMovies() throws IOException {
            saveVideos(new ArrayList<>(movies), fileDataMovies);
        }
        public void saveTVSeries() throws IOException {
            saveVideos(new ArrayList<>(tvSeries), fileDataTvs);
        }
        public void saveAnimeSeries() throws IOException {
            saveVideos(new ArrayList<>(animeSeries), fileDataAnimes);
        }

        private <T extends Video> void loadVideos(ObservableList<T> targetList, File fileSource) throws Exception {
            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileSource)))) {
                targetList.setAll((Collection<T>) ois.readObject());
            }
        }
        public void loadMovies() throws Exception {
            loadVideos(movies, fileDataMovies);
        }
        public void loadTVSeries() throws Exception {
            loadVideos(tvSeries, fileDataTvs);
        }
        public void loadAnimeSeries() throws Exception {
            loadVideos(animeSeries, fileDataAnimes);
        }
    }

    /* Export methods are always updated to the latest version */
    public void csvExportMovies(File fileDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","DURATION","RELEASE DATE","DIRECTOR","STARTED","TERMINATED","RATING",genresStr,"PATH IMAGE","ID"));
            for(Movie mi : movies) {
                pw.append(FormatString.stringNormalize(mi.getTitle())).append(delim);
                pw.append(mi.getDuration()).append(delim);
                pw.append(mi.getReleaseDate()).append(delim);
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
                pw.append(mi.getPathImage()!=null ? mi.getPathImage():"").append(delim);
                pw.append(mi.getId()).append("\n");
            }
        }
    }
    private void csvExportTV(Collection<TVSerie> c, File fileDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","#SEASONS"+delimAux+"#EPISODES","RELEASE DATE","STARTED","TERMINATED","RATING",genresStr,"PATH IMAGE","ID"));

            for(TVSerie tvi : c) {
                pw.append(FormatString.stringNormalize(tvi.getTitle())).append(delim);
                pw.append(Integer.toString(tvi.getNumSeasons())).append(delimAux);
                pw.append(Integer.toString(tvi.getNumEpisodes())).append(delim);
                pw.append(tvi.getReleaseDate()).append(delim);
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
                pw.append(tvi.getPathImage()!=null ? tvi.getPathImage():"").append(delim);
                pw.append(tvi.getId()).append("\n");
            }
        }
    }
    public void csvExportTVSeries(File fileDest) throws IOException {
        csvExportTV(tvSeries, fileDest);
    }
    public void csvExportAnimeSeries(File fileDest) throws IOException {
        csvExportTV(animeSeries, fileDest);
    }

    public void csvImportMoviesCurrentVersion(File fileSource) throws IOException {
        try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){

        }
    }
    public void csvImportTVCurrentVersion() {

    }
    /* More Import methods to include the previous versions as well */
    public void csvImportMoviesVersion1() {

    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder("**** Stampa instanza video keeper ****\n");

        strb.append("** ").append(moviesNumber()).append(" Movies:\n");
        movies.forEach(m -> strb.append("* ").append(m.toString()));
        strb.append("** ").append(tvsNumber()).append(" TV Series:\n");
        tvSeries.forEach(t -> strb.append("* ").append(t.toString()));
        strb.append("** ").append(animesNumber()).append(" Anime Series:\n");
        animeSeries.forEach(a -> strb.append("* ").append(a.toString()));

        return strb.toString();
    }
}
