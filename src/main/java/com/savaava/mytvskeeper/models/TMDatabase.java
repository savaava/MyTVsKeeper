package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;

import java.util.ArrayList;
import java.util.Collection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.json.JSONArray;

public class TMDatabase {
    private static TMDatabase instance;
    private final File fileConfig = new File("./bin/Config");
    private String apiKey;
    private final HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response;

    private TMDatabase() throws IOException {
        if(fileConfig.exists())
            loadConfig();

        client = HttpClient.newHttpClient();
    }

    public static TMDatabase getInstance() throws IOException {
        if(instance == null)
            instance = new TMDatabase();
        return instance;
    }

    public String getApiKey(){return apiKey;}
    public void setApiKey(String apiKey){this.apiKey = apiKey;}


    /* Movie's Main Constructor:
    title, description, releaseDate, started, terminated, rating, id, duration, director */
    public Collection<Movie> getMoviesByName(String name) throws ConfigNotExistsException, IOException, InterruptedException {
        if(apiKey == null)
            throw new ConfigNotExistsException();

        Collection<Movie> out = new ArrayList<>();

        /* to format the string written by the user for the https request: */
        name = name.trim();
        name = name.replaceAll("\\s+", " ");
        name = String.join("%20", name.split(" "));
        String url = "https://api.themoviedb.org/3/search/movie?query="+name+"&include_adult=true&language=en-EN&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonMovies = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonMovies.length(); i++){
            JSONObject currMovie = jsonMovies.getJSONObject(i);
            JSONArray genresVett = currMovie.getJSONArray("genre_ids");

            Movie movie = new Movie(
                    currMovie.getString("title"),
                    currMovie.getString("overview"),
                    currMovie.getString("release_date"),
                    Integer.toString(currMovie.getInt("id")));
            for(int j=0; j<genresVett.length(); j++)
                movie.addGenre(genresVett.getInt(j));

            out.add(movie);
        }

        return out;
    }

    public Movie getMovieById(String id) throws ConfigNotExistsException, IOException, InterruptedException {
        if(apiKey == null)
            throw new ConfigNotExistsException();

        String url = "https://api.themoviedb.org/3/movie/"+id+"?language=en-US&include_adult=true&api_key="+apiKey;
        String urlCredits = "https://api.themoviedb.org/3/movie/"+id+"/credits?language=en-US&include_adult=true&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonMovie = new JSONObject(response.body());
        JSONArray genresVett = jsonMovie.getJSONArray("genres");

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlCredits))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray crewArray = new JSONObject(response.body()).getJSONArray("crew");

        String director = "";
        for(int i=0; i<crewArray.length(); i++) {
            JSONObject crewMateVett = crewArray.getJSONObject(i);
            if(crewMateVett.getString("job").equals("Director"))
                director = crewMateVett.getString("name");
        }

        Movie out = new Movie(
                jsonMovie.getString("title"),
                jsonMovie.getString("overview"),
                jsonMovie.getString("release_date"),
                false,false,-1,
                Integer.toString(jsonMovie.getInt("id")),
                Integer.toString(jsonMovie.getInt("runtime")),
                director);
        for(int i=0; i<genresVett.length(); i++){
            out.addGenre(genresVett.getJSONObject(i).getInt("id"));
        }

        return out;
    }

    /* TVSerie's Main Constructor:
    * title, description, releaseDate, started, terminated, rating, id, numSeasons, numEpisodes */
    public Collection<TVSerie> getTVSeriesByName(String name) throws ConfigNotExistsException, IOException, InterruptedException {
        if(apiKey == null)
            throw new ConfigNotExistsException();

        Collection<TVSerie> out = new ArrayList<>();

        name = name.trim();
        name = name.replaceAll("\\s+", " ");
        name = String.join("%20", name.split(" "));
        String url = "https://api.themoviedb.org/3/search/tv?query="+name+"&include_adult=true&language=en-US&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonTVSeries = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonTVSeries.length(); i++){
            JSONObject currTVSerie = jsonTVSeries.getJSONObject(i);
            JSONArray genresVett = currTVSerie.getJSONArray("genre_ids");

            TVSerie tvSerie = new TVSerie(
                    currTVSerie.getString("name"),
                    currTVSerie.getString("overview"),
                    currTVSerie.getString("first_air_date"),
                    Integer.toString(currTVSerie.getInt("id")));
            for(int j=0; j<genresVett.length(); j++)
                tvSerie.addGenre(genresVett.getInt(j));

            out.add(tvSerie);
        }

        return out;
    }

    public TVSerie getTVSerieById(String id) throws ConfigNotExistsException, IOException, InterruptedException {
        if(apiKey == null)
            throw new ConfigNotExistsException();

        String url = "https://api.themoviedb.org/3/tv/"+id+"?language=en-US&include_adult=true&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonTVSerie = new JSONObject(response.body());
        JSONArray genresVett = jsonTVSerie.getJSONArray("genres");

        TVSerie out = new TVSerie(
                jsonTVSerie.getString("name"),
                jsonTVSerie.getString("overview"),
                jsonTVSerie.getString("first_air_date"),
                false,false,-1,
                Integer.toString(jsonTVSerie.getInt("id")),
                jsonTVSerie.getInt("number_of_seasons"),
                jsonTVSerie.getInt("number_of_episodes"));

        for(int i=0; i<genresVett.length(); i++){
            out.addGenre(genresVett.getJSONObject(i).getInt("id"));
        }

        return out;
    }


    public void saveConfig() throws IOException {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileConfig)))) {
            pw.println("YOUR API KEY");
            pw.print(apiKey);
        }
    }
    public void loadConfig() throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(fileConfig))) {
            br.readLine();
            apiKey = br.readLine();
        }
    }
}
