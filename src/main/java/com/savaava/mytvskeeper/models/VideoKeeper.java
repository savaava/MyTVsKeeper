package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.savaava.mytvskeeper.utility.FormatString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for the persistence of videos, which can be of two types: Movie or TVSerie (TV Serie or Anime)
 * @version 1.0
 */
public class VideoKeeper {
    private static final String CURRENT_VERSION = "1.0";

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

    /**
     * Inner class for the saving and loading operations, supporting the logic separation and information hiding.
     */
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
         * @param listToWrite the collection to serialize
         * @param fileDest the destination file
         * @throws IOException if an I/O error occurs
         */
        private <T extends Video> void saveVideos(ObservableList<T> listToWrite, File fileDest) throws IOException {
            try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDest)))){
                oos.writeObject(new ArrayList<>(listToWrite));
            }
        }
        public void saveMovies() throws IOException {
            saveVideos(movies, fileDataMovies);
        }
        public void saveTVSeries() throws IOException {
            saveVideos(tvSeries, fileDataTvs);
        }
        public void saveAnimeSeries() throws IOException {
            saveVideos(animeSeries, fileDataAnimes);
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
            pw.println("VERSION"+delim+CURRENT_VERSION);
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","DURATION","RELEASE DATE","DIRECTOR","STARTED","TERMINATED","RATING",genresStr,"DESCRIPTION","PATH IMAGE","ID"));

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
                    pw.append(gi.getName()).append("(").append(Integer.toString(gi.getId())).append(")"); i++;
                    if(i!=mi.numGenres())
                        pw.append(delimAux);
                }
                pw.append(delim);
                pw.append(mi.getDescription()).append(delim);
                pw.append(mi.getPathImage()!=null ? mi.getPathImage():"").append(delim);
                pw.append(mi.getId()).append("\n");
            }
        }
    }
    private void csvExportTV(Collection<TVSerie> c, File fileDest) throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
            pw.println("VERSION"+delim+CURRENT_VERSION);
            String genresStr = String.join(delimAux,"GENRE 1","GENRE 2","...");
            pw.println(String.join(delim,"TITLE","#SEASONS"+delimAux+"#EPISODES","RELEASE DATE","STARTED","TERMINATED","RATING",genresStr,"DESCRIPTION","PATH IMAGE","ID"));

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
                    pw.append(gi.getName()).append("(").append(Integer.toString(gi.getId())).append(")"); i++;
                    if(i!=tvi.numGenres())
                        pw.append(delimAux);
                }
                pw.append(delim);
                pw.append(tvi.getDescription()).append(delim);
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


    /**
     * Import Method for file.csv generated in the previous version 1.0
     * @param scanner scanner of file with movies to import, the file is generated by the application 1.0
     */
    private void csvImportMoviesVersion1(Scanner scanner) throws IOException {
        /* Currently the version 1.0 is the current one */
        csvImportMoviesCurrentVersion(scanner);
    }
    /**
     * Import Method for file.csv generated in the current version
     * @param scanner scanner of file with movies to import, the file is generated by the current application
     */
    private void csvImportMoviesCurrentVersion(Scanner scanner) throws IOException {
        /* TITLE;DURATION;RELEASE DATE;DIRECTOR;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

        scanner.useDelimiter("\n");
        scanner.next(); /* read the first prompt line */
        scanner.useDelimiter("[;\n]");

        while(scanner.hasNext()) {
            String title = scanner.next();
            String duration = scanner.next();
            String releaseDate = scanner.next();
            String director = scanner.next();
            String started = scanner.next();
            String terminated = scanner.next();
            String rating = scanner.next();
            String[] genres = scanner.next().split(delimAux);
            String description = scanner.next();
            String pathImage = scanner.next();
            String id = scanner.next();

            Movie movieToAdd = new Movie(title, description, releaseDate,
                    started.equals("Started"), terminated.equals("Terminated"), rating, id,
                    pathImage.isEmpty() ? null:pathImage, duration, director);
            for(String gi : genres){
                if(! gi.isEmpty())
                    movieToAdd.addGenre(Integer.parseInt(gi.split("[(]")[1].replace(")","")));
            }

            /* to reduce the computational complexity -> the saving only occurs at the end of the scan */
            if(! movies.contains(movieToAdd))
                movies.add(movieToAdd);
        }
        persistenceHandler.saveMovies();
    }
    /**
     * It's the main Import method because it is the method effectively called to import videos whatever the version of fileSource,
     * checking the version first at the first line
     */
    public void csvImportMovies(File fileSource) throws IOException {
        try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
            String version = scanner.nextLine().split(delim)[1];

            if(version.equals(CURRENT_VERSION))
                csvImportMoviesCurrentVersion(scanner);
            else if(version.equals("1.0"))
                csvImportMoviesVersion1(scanner);
            else
                System.err.println("Version not found");
        }
    }


    private TVSerie csvReadSingleTv(Scanner scanner) {
        String title = scanner.next();
        String[] numSeasonsEpisods = scanner.next().split(delimAux);
        String releaseDate = scanner.next();
        String started = scanner.next();
        String terminated = scanner.next();
        String rating = scanner.next();
        String[] genres = scanner.next().split(delimAux);
        String description = scanner.next();
        String pathImage = scanner.next();
        String id = scanner.next();

        TVSerie tvToAdd = new TVSerie(title, description, releaseDate,
                started.equals("Started"), terminated.equals("Terminated"), rating, id,
                pathImage.isEmpty()?null:pathImage,
                Integer.parseInt(numSeasonsEpisods[0]), Integer.parseInt(numSeasonsEpisods[1]));

        for(String gi : genres){
            if(! gi.isEmpty())
                tvToAdd.addGenre(Integer.parseInt(gi.split("[(]")[1].replace(")","")));
        }

        return tvToAdd;
    }

    private void csvImportTVSerieVersion1(Scanner scanner) throws IOException {
        csvImportTVSerieCurrentVersion(scanner);
    }
    private void csvImportTVSerieCurrentVersion(Scanner scanner) throws IOException {
        /* TITLE;#SEASONS,#EPISODES;RELEASE DATE;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

        scanner.useDelimiter("\n");
        scanner.next();
        scanner.useDelimiter("[;\n]");

        while(scanner.hasNext()) {
            TVSerie tvToAdd = csvReadSingleTv(scanner);

            if(! tvSeries.contains(tvToAdd))
                tvSeries.add(tvToAdd);
        }
        persistenceHandler.saveTVSeries();
    }
    public void csvImportTVSerie(File fileSource) throws IOException {
        try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
            String version = scanner.next().split(delim)[1];

            if(version.equals(CURRENT_VERSION))
                csvImportTVSerieCurrentVersion(scanner);
            else if(version.equals("1.0"))
                csvImportTVSerieVersion1(scanner);
            else
                System.err.println("Version not found");
        }
    }

    private void csvImportAnimeSerieVersion1(Scanner scanner) throws IOException {
        csvImportAnimeSerieCurrentVersion(scanner);
    }
    private void csvImportAnimeSerieCurrentVersion(Scanner scanner) throws IOException {
        /* TITLE;#SEASONS,#EPISODES;RELEASE DATE;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

        scanner.useDelimiter("\n");
        scanner.next(); /* read the first prompt line */
        scanner.useDelimiter("[;\n]");

        while(scanner.hasNext()) {
            TVSerie tvToAdd = csvReadSingleTv(scanner);

            if(! animeSeries.contains(tvToAdd))
                animeSeries.add(tvToAdd);
        }
        persistenceHandler.saveAnimeSeries();
    }
    public void csvImportAnimeSerie(File fileSource) throws IOException {
        try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
            String version = scanner.next().split(delim)[1];

            if(version.equals(CURRENT_VERSION))
                csvImportAnimeSerieCurrentVersion(scanner);
            else if(version.equals("1.0"))
                csvImportAnimeSerieVersion1(scanner);
            else
                System.err.println("Version not found");
        }
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
