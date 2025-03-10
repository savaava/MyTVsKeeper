package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.NotMatchingVideoTypeException;
import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.savaava.mytvskeeper.utility.FormatString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for the persistence of videos, which can be of two types: Movie or TVSeries (TV Series or Anime Series)
 * @version {@value CURRENT_VERSION}
 */
public class VideoKeeper {
    public static final String CURRENT_VERSION = "1.31";

    private static VideoKeeper instance;

    ObservableList<Movie> movies;
    ObservableList<TVSerie> tvSeries;
    ObservableList<TVSerie> animeSeries;

    private final SerializedPersistenceHandler persistenceHandler;


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

    public void addMovie(Movie movie) throws Exception, VideoAlreadyExistsException {
        if(movies.contains(movie))
            throw new VideoAlreadyExistsException("The movie you are trying to add already exists");
        movies.add(movie);
        persistenceHandler.saveMovies();
    }
    public void addTVSerie(TVSerie tvSerie) throws Exception, VideoAlreadyExistsException {
        if(tvSeries.contains(tvSerie))
            throw new VideoAlreadyExistsException("The Serie you are trying to add already exists");
        tvSeries.add(tvSerie);
        persistenceHandler.saveTVSeries();
    }
    public void addAnimeSerie(TVSerie anime) throws Exception, VideoAlreadyExistsException {
        if(animeSeries.contains(anime))
            throw new VideoAlreadyExistsException("The Anime you are trying to add already exists");
        animeSeries.add(anime);
        persistenceHandler.saveAnimeSeries();
    }

    public void removeMovie(String id) throws Exception {
        if(! movies.remove(new Movie(id)))
            return ;
        if(movies.isEmpty())
            persistenceHandler.deleteDataMovies();
        else
            persistenceHandler.saveMovies();
    }
    public void removeTVSerie(String id) throws Exception {
        if(! tvSeries.remove(new TVSerie(id)))
            return ;
        if(tvSeries.isEmpty())
            persistenceHandler.deleteDataTVs();
        else
            persistenceHandler.saveTVSeries();
    }
    public void removeAnimeSerie(String id) throws Exception {
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
         * Savess a collection of videos in the specified file for the data persistence.
         * @param listToWrite the collection to serialize
         * @param fileDest the destination file
         * @throws Exception if an I/O error occurs
         */
        private void saveVideos(ObservableList<? extends Video> listToWrite, File fileDest) throws Exception {
            try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileDest)))){
                oos.writeObject(new ArrayList<>(listToWrite));
            }
        }
        public void saveMovies() throws Exception {
            saveVideos(movies, fileDataMovies);
        }
        public void saveTVSeries() throws Exception {
            saveVideos(tvSeries, fileDataTvs);
        }
        public void saveAnimeSeries() throws Exception {
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

    public class CsvHandler {
        private static final String CSV_DELIMITER = ";";
        private static final String CSV_AUXILIARY_DELIMITER = ",";

        private static final String MOVIE_TYPE = "Movie";
        private static final String TVSERIE_TYPE = "TV Serie";
        private static final String ANIME_TYPE = "Anime Serie";


        /* Export methods are always updated to the latest version */
        /**
         * Generates a file csv with all the movies on each line.
         * First line is dedicated to track the {@code CURRENT_VERSION} of the application.
         * Second line is dedicated to show the content of each column.
         * Other lines are dedicated to show the contents of each movie.
         * To generate the file is used the {@code CSV_DELIMITER} to separate the columns
         * and the {@code CSV_AUXILIARY_DELIMITER} to separate Genres in its column.
         * @param fileDest is the file path where it's created
         * @return the number of movies written in fileDest (#movies = #lines-2)
         * @throws Exception when an exception occurs
         */
        public int csvExportMovies(File fileDest) throws Exception {
            int movieCont = 0;

            try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
                String genresStr = String.join(CSV_AUXILIARY_DELIMITER,"GENRE 1","GENRE 2","...");

                pw.println("VERSION"+CSV_DELIMITER+"VIDEO TYPE");
                pw.println(CURRENT_VERSION+CSV_DELIMITER+MOVIE_TYPE);
                pw.println(String.join(CSV_DELIMITER,"TITLE","DURATION","RELEASE DATE","DIRECTOR","STARTED","TERMINATED","RATING","NOTES",genresStr,"DESCRIPTION","PATH IMAGE","ID"));

                for(Movie mi : movies) {
                    movieCont++;
                    pw.append(FormatString.stringNormalize(mi.getTitle())).append(CSV_DELIMITER);
                    pw.append(Integer.toString(mi.getDuration())).append("min").append(CSV_DELIMITER);
                    pw.append(mi.getReleaseDate()).append(CSV_DELIMITER);
                    pw.append(mi.getDirector()).append(CSV_DELIMITER);
                    pw.append(mi.isStarted()?"Started":"Not Started").append(CSV_DELIMITER);
                    pw.append(mi.isTerminated()?"Terminated":"Not Terminated").append(CSV_DELIMITER);
                    pw.append(mi.getRating()==null ? null:Double.toString(mi.getRating())).append(CSV_DELIMITER);
                    pw.append(FormatString.stringNormalize(mi.getNotes())).append(CSV_DELIMITER);

                    int i = 0;
                    for(MovieGenres gi : mi.getGenres()){
                        pw.append(gi.getName()).append("(").append(Integer.toString(gi.getId())).append(")"); i++;
                        if(i!=mi.numGenres())
                            pw.append(CSV_AUXILIARY_DELIMITER);
                    }
                    pw.append(CSV_DELIMITER);

                    pw.append(mi.getDescription()).append(CSV_DELIMITER);
                    pw.append(mi.getPathImage()!=null ? mi.getPathImage():"").append(CSV_DELIMITER);
                    pw.append(mi.getId()).append("\n");
                }
            }
            return movieCont;
        }

        private int csvExportTV(Collection<TVSerie> c, PrintWriter pw) {
            int tvCont = 0;
            String genresStr = String.join(CSV_AUXILIARY_DELIMITER,"GENRE 1","GENRE 2","...");

            pw.println(String.join(CSV_DELIMITER,"TITLE","#SEASONS"+CSV_AUXILIARY_DELIMITER+"#EPISODES","RELEASE DATE","STARTED","TERMINATED","RATING","NOTES",genresStr,"DESCRIPTION","PATH IMAGE","ID"));

            for(TVSerie tvi : c) {
                tvCont++;
                pw.append(FormatString.stringNormalize(tvi.getTitle())).append(CSV_DELIMITER);
                pw.append(Integer.toString(tvi.getNumSeasons())).append(CSV_AUXILIARY_DELIMITER);
                pw.append(Integer.toString(tvi.getNumEpisodes())).append(CSV_DELIMITER);
                pw.append(tvi.getReleaseDate()).append(CSV_DELIMITER);
                pw.append(tvi.isStarted()?"Started":"Not Started").append(CSV_DELIMITER);
                pw.append(tvi.isTerminated()?"Terminated":"Not Terminated").append(CSV_DELIMITER);
                pw.append(tvi.getRating()==null ? null:Double.toString(tvi.getRating())).append(CSV_DELIMITER);
                pw.append(FormatString.stringNormalize(tvi.getNotes())).append(CSV_DELIMITER);

                int i = 0;
                for(TVGenres gi : tvi.getGenres()){
                    pw.append(gi.getName()).append("(").append(Integer.toString(gi.getId())).append(")"); i++;
                    if(i!=tvi.numGenres())
                        pw.append(CSV_AUXILIARY_DELIMITER);
                }
                pw.append(CSV_DELIMITER);

                pw.append(tvi.getDescription()).append(CSV_DELIMITER);
                pw.append(tvi.getPathImage()!=null ? tvi.getPathImage():"").append(CSV_DELIMITER);
                pw.append(tvi.getId()).append("\n");
            }

            return tvCont;
        }
        public int csvExportTVSeries(File fileDest) throws Exception {
            try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
                pw.println("VERSION"+CSV_DELIMITER+"VIDEO TYPE");
                pw.println(CURRENT_VERSION+CSV_DELIMITER+TVSERIE_TYPE);

                return csvExportTV(tvSeries, pw);
            }
        }
        public int csvExportAnimeSeries(File fileDest) throws Exception {
            try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileDest)))){
                pw.println("VERSION"+CSV_DELIMITER+"VIDEO TYPE");
                pw.println(CURRENT_VERSION+CSV_DELIMITER+ANIME_TYPE);

                return csvExportTV(animeSeries, pw);
            }
        }


        private final String star = "🌟";
        /**
         * Import Method for file.csv generated in the previous version 1.2
         * @param scanner scanner of file with movies to import, the file is generated by the application 1.2
         */
        private int csvImportMoviesVersion12(Scanner scanner) throws Exception {
            /* TITLE;DURATION;RELEASE DATE;DIRECTOR;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

            int movieCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                String title = scanner.next();
                String duration = scanner.next();
                String releaseDate = scanner.next();
                String director = scanner.next();
                String started = scanner.next();
                String terminated = scanner.next();
                String rating = scanner.next();
                String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
                String description = scanner.next();
                String pathImage = scanner.next();
                String id = scanner.next();

                Double ratingDouble = null;
                if(!rating.isEmpty()){
                    ratingDouble = Double.parseDouble(
                            rating.replaceAll(star,"").trim()
                    );
                }

                Movie movieToAdd = new Movie(title, description, releaseDate,
                        started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                        pathImage.isEmpty() ? null:pathImage,"",
                        Integer.parseInt(duration.replace("min","")), director);

                for (String gi : genres) {
                    if (gi.isEmpty()) continue;

                    /* for a greater strength principle */
                    int openParenIndex = gi.indexOf("(");
                    int closeParenIndex = gi.indexOf(")");

                    if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                        String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                        movieToAdd.addGenre(Integer.parseInt(numberGenre));
                    }
                }

                /* to reduce the computational complexity -> the serialized saving only occurs at the end of the scan */
                if(! movies.contains(movieToAdd)){
                    movies.add(movieToAdd);
                    movieCont++;
                }
            }
            persistenceHandler.saveMovies();

            return movieCont;
        }
        private int csvImportMoviesVersion13(Scanner scanner) throws Exception {
            /* TITLE;DURATION;RELEASE DATE;DIRECTOR;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

            int movieCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                String title = scanner.next();
                String duration = scanner.next();
                String releaseDate = scanner.next();
                String director = scanner.next();
                String started = scanner.next();
                String terminated = scanner.next();
                String rating = scanner.next();
                String notes = scanner.next();
                String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
                String description = scanner.next();
                String pathImage = scanner.next();
                String id = scanner.next();

                Double ratingDouble = null;
                if(!rating.isEmpty()){
                    ratingDouble = Double.parseDouble(
                            rating.replaceAll(star,"").trim()
                    );
                }

                Movie movieToAdd = new Movie(title, description, releaseDate,
                        started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                        pathImage.isEmpty() ? null:pathImage,notes,
                        Integer.parseInt(duration.replace("min","")), director);

                for (String gi : genres) {
                    if (gi.isEmpty()) continue;

                    /* for a greater strength principle */
                    int openParenIndex = gi.indexOf("(");
                    int closeParenIndex = gi.indexOf(")");

                    if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                        String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                        movieToAdd.addGenre(Integer.parseInt(numberGenre));
                    }
                }

                /* to reduce the computational complexity -> the serialized saving only occurs at the end of the scan */
                if(! movies.contains(movieToAdd)){
                    movies.add(movieToAdd);
                    movieCont++;
                }
            }
            persistenceHandler.saveMovies();

            return movieCont;
        }
        private int csvImportMoviesCurrentVersion(Scanner scanner) throws Exception {
            /* TITLE;DURATION;RELEASE DATE;DIRECTOR;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

            int movieCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                String title = scanner.next();
                String duration = scanner.next();
                String releaseDate = scanner.next();
                String director = scanner.next();
                String started = scanner.next();
                String terminated = scanner.next();
                String rating = scanner.next();
                String notes = scanner.next();
                String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
                String description = scanner.next();
                String pathImage = scanner.next();
                String id = scanner.next();

                Double ratingDouble = rating.equals("null") ? null:Double.parseDouble(rating);

                Movie movieToAdd = new Movie(title, description, releaseDate,
                        started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                        pathImage.isEmpty() ? null:pathImage,notes,
                        Integer.parseInt(duration.replace("min","")), director);

                for (String gi : genres) {
                    if (gi.isEmpty()) continue;

                    /* for a greater strength principle */
                    int openParenIndex = gi.indexOf("(");
                    int closeParenIndex = gi.indexOf(")");

                    if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                        String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                        movieToAdd.addGenre(Integer.parseInt(numberGenre));
                    }
                }

                /* to reduce the computational complexity -> the serialized saving only occurs at the end of the scan */
                if(! movies.contains(movieToAdd)){
                    movies.add(movieToAdd);
                    movieCont++;
                }
            }
            persistenceHandler.saveMovies();

            return movieCont;
        }
        /**
         * It's the main Import method because it is the method effectively called to import videos whatever the version of fileSource,
         * checking the version first at the first line
         * @param fileSource is .csv
         * @return number of Movies inserted to the list, hence only the new movies
         * @throws NotMatchingVideoTypeException when the client called this method provided a file for
         * a video type different from {@code MOVIE_TYPE}
         * @throws Exception or others when an exception occurs finding or reading the file
         */
        public int csvImportMovies(File fileSource) throws Exception {
            try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
                scanner.nextLine();
                String[] versionAndType = scanner.nextLine().split(CSV_DELIMITER);

                /* It's a reachable block when in import phase the user specifies a different video type against MOVIE_TYPE of file */
                if(! versionAndType[1].equals(MOVIE_TYPE))
                    throw new NotMatchingVideoTypeException("Video type expected: "+MOVIE_TYPE+
                            "\nVideo type of file:  "+versionAndType[1]);

                String version = versionAndType[0];
                return switch(version){
                    case CURRENT_VERSION -> csvImportMoviesCurrentVersion(scanner);
                    case "1.3" -> csvImportMoviesVersion13(scanner);
                    case "1.2" -> csvImportMoviesVersion12(scanner);
                    default -> {
                        System.err.println("Version not found");
                        yield 0;
                    }
                };
            }
        }


        private TVSerie csvReadSingleTvVersion12(Scanner scanner) {
            String title = scanner.next();
            String[] numSeasonsEpisods = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String releaseDate = scanner.next();
            String started = scanner.next();
            String terminated = scanner.next();
            String rating = scanner.next();
            String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String description = scanner.next();
            String pathImage = scanner.next();
            String id = scanner.next();

            /* for a greater strength principle */
            int numEpisodes = 0;
            String regexInt = "^\\d+$";
            int numSeasons = numSeasonsEpisods[0].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[0]) : 0;
            if(numSeasonsEpisods.length == 2)
                numEpisodes = numSeasonsEpisods[1].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[1]) : 0;

            Double ratingDouble = null;
            if(!rating.isEmpty()){
                ratingDouble = Double.parseDouble(
                        rating.replaceAll(star,"").trim()
                );
            }

            TVSerie tvToAdd = new TVSerie(title, description, releaseDate,
                    started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                    pathImage.isEmpty()?null:pathImage, "", numSeasons, numEpisodes);

            for(String gi : genres){
                if (gi.isEmpty()) continue;

                int openParenIndex = gi.indexOf("(");
                int closeParenIndex = gi.indexOf(")");

                if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                    String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                    tvToAdd.addGenre(Integer.parseInt(numberGenre));
                }
            }

            return tvToAdd;
        }
        private TVSerie csvReadSingleTvVersion13(Scanner scanner) {
            String title = scanner.next();
            String[] numSeasonsEpisods = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String releaseDate = scanner.next();
            String started = scanner.next();
            String terminated = scanner.next();
            String rating = scanner.next();
            String notes = scanner.next();
            String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String description = scanner.next();
            String pathImage = scanner.next();
            String id = scanner.next();

            /* for a greater strength principle */
            int numEpisodes = 0;
            String regexInt = "^\\d+$";
            int numSeasons = numSeasonsEpisods[0].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[0]) : 0;
            if(numSeasonsEpisods.length == 2)
                numEpisodes = numSeasonsEpisods[1].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[1]) : 0;

            Double ratingDouble = null;
            if(!rating.isEmpty()){
                ratingDouble = Double.parseDouble(
                        rating.replaceAll(star,"").trim()
                );
            }

            TVSerie tvToAdd = new TVSerie(title, description, releaseDate,
                    started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                    pathImage.isEmpty()?null:pathImage, notes, numSeasons, numEpisodes);

            for(String gi : genres){
                if (gi.isEmpty()) continue;

                int openParenIndex = gi.indexOf("(");
                int closeParenIndex = gi.indexOf(")");

                if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                    String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                    tvToAdd.addGenre(Integer.parseInt(numberGenre));
                }
            }

            return tvToAdd;
        }
        private TVSerie csvReadSingleTvCurrentVersion(Scanner scanner) {
            String title = scanner.next();
            String[] numSeasonsEpisods = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String releaseDate = scanner.next();
            String started = scanner.next();
            String terminated = scanner.next();
            String rating = scanner.next();
            String notes = scanner.next();
            String[] genres = scanner.next().split(CSV_AUXILIARY_DELIMITER);
            String description = scanner.next();
            String pathImage = scanner.next();
            String id = scanner.next();

            /* for a greater strength principle */
            int numEpisodes = 0;
            String regexInt = "^\\d+$";
            int numSeasons = numSeasonsEpisods[0].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[0]) : 0;
            if(numSeasonsEpisods.length == 2)
                numEpisodes = numSeasonsEpisods[1].matches(regexInt) ? Integer.parseInt(numSeasonsEpisods[1]) : 0;

            Double ratingDouble = rating.equals("null") ? null:Double.parseDouble(rating);

            TVSerie tvToAdd = new TVSerie(title, description, releaseDate,
                    started.equals("Started"), terminated.equals("Terminated"), ratingDouble, id,
                    pathImage.isEmpty()?null:pathImage, notes, numSeasons, numEpisodes);

            for(String gi : genres){
                if (gi.isEmpty()) continue;

                int openParenIndex = gi.indexOf("(");
                int closeParenIndex = gi.indexOf(")");

                if (openParenIndex!=-1 && closeParenIndex!=-1 && openParenIndex<closeParenIndex) {
                    String numberGenre = gi.substring(openParenIndex+1, closeParenIndex);
                    tvToAdd.addGenre(Integer.parseInt(numberGenre));
                }
            }

            return tvToAdd;
        }

        private int csvImportTVSerieVersion12(Scanner scanner) throws Exception {
            /* TITLE;#SEASONS,#EPISODES;RELEASE DATE;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

            int tvSerieCont = 0;

            scanner.nextLine();
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvVersion12(scanner);

                if(! tvSeries.contains(tvToAdd)){
                    tvSeries.add(tvToAdd);
                    tvSerieCont++;
                }
            }
            persistenceHandler.saveTVSeries();

            return tvSerieCont;
        }
        private int csvImportTVSerieVersion13(Scanner scanner) throws Exception {
            int tvSerieCont = 0;

            scanner.nextLine();
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvVersion13(scanner);

                if(! tvSeries.contains(tvToAdd)){
                    tvSeries.add(tvToAdd);
                    tvSerieCont++;
                }
            }
            persistenceHandler.saveTVSeries();

            return tvSerieCont;
        }
        private int csvImportTVSerieCurrentVersion(Scanner scanner) throws Exception {
            int tvSerieCont = 0;

            scanner.nextLine();
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvCurrentVersion(scanner);

                if(! tvSeries.contains(tvToAdd)){
                    tvSeries.add(tvToAdd);
                    tvSerieCont++;
                }
            }
            persistenceHandler.saveTVSeries();

            return tvSerieCont;
        }
        public int csvImportTVSerie(File fileSource) throws Exception {
            try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
                scanner.nextLine();
                String[] versionAndType = scanner.nextLine().split(CSV_DELIMITER);

                if(! versionAndType[1].equals(TVSERIE_TYPE))
                    throw new NotMatchingVideoTypeException("Video type expected: "+TVSERIE_TYPE+
                            "\nVideo type of file: "+versionAndType[1]);

                String version = versionAndType[0];
                return switch(version){
                    case CURRENT_VERSION -> csvImportTVSerieCurrentVersion(scanner);
                    case "1.3" -> csvImportTVSerieVersion13(scanner);
                    case "1.2" -> csvImportTVSerieVersion12(scanner);
                    default -> {
                        System.err.println("Version not found");
                        yield 0;
                    }
                };
            }
        }

        private int csvImportAnimeSerieVersion12(Scanner scanner) throws Exception {
            /* TITLE;#SEASONS,#EPISODES;RELEASE DATE;STARTED;TERMINATED;RATING;GENRE 1,GENRE 2,...;DESCRIPTION;PATH IMAGE;ID */

            int animeCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvVersion12(scanner);

                if(! animeSeries.contains(tvToAdd)){
                    animeSeries.add(tvToAdd);
                    animeCont++;
                }
            }
            persistenceHandler.saveAnimeSeries();

            return animeCont;
        }
        private int csvImportAnimeSerieVersion13(Scanner scanner) throws Exception {
            int animeCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvVersion13(scanner);

                if(! animeSeries.contains(tvToAdd)){
                    animeSeries.add(tvToAdd);
                    animeCont++;
                }
            }
            persistenceHandler.saveAnimeSeries();

            return animeCont;
        }
        private int csvImportAnimeSerieCurrentVersion(Scanner scanner) throws Exception {
            int animeCont = 0;

            scanner.nextLine(); /* read the first prompt line */
            scanner.useDelimiter("[;\n]");

            while(scanner.hasNext()) {
                TVSerie tvToAdd = csvReadSingleTvCurrentVersion(scanner);

                if(! animeSeries.contains(tvToAdd)){
                    animeSeries.add(tvToAdd);
                    animeCont++;
                }
            }
            persistenceHandler.saveAnimeSeries();

            return animeCont;
        }
        public int csvImportAnimeSerie(File fileSource) throws Exception {
            try(Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileSource)))){
                scanner.nextLine();
                String[] versionAndType = scanner.nextLine().split(CSV_DELIMITER);

                if(! versionAndType[1].equals(ANIME_TYPE))
                    throw new NotMatchingVideoTypeException("Video type expected: "+ANIME_TYPE+
                            "\nVideo type of file: "+versionAndType[1]);

                String version = versionAndType[0];
                return switch(version){
                    case CURRENT_VERSION -> csvImportAnimeSerieCurrentVersion(scanner);
                    case "1.3" -> csvImportAnimeSerieVersion13(scanner);
                    case "1.2" -> csvImportAnimeSerieVersion12(scanner);
                    default -> {
                        System.err.println("Version not found");
                        yield 0;
                    }
                };
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder("**** Video Keeper instance ****\n");

        strb.append("** ").append(moviesNumber()).append(" Movies:\n");
        movies.forEach(m -> strb.append("* ").append(m.toString()));
        strb.append("** ").append(tvsNumber()).append(" TV Series:\n");
        tvSeries.forEach(t -> strb.append("* ").append(t.toString()));
        strb.append("** ").append(animesNumber()).append(" Anime Series:\n");
        animeSeries.forEach(a -> strb.append("* ").append(a.toString()));

        return strb.toString();
    }
}
