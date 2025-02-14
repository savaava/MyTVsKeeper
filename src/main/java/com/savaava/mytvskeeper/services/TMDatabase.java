package com.savaava.mytvskeeper.services;

import com.savaava.mytvskeeper.models.Movie;
import com.savaava.mytvskeeper.models.TVSerie;
import com.savaava.mytvskeeper.utility.FormatString;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.json.JSONArray;

public class TMDatabase {
    private static final String CONFIG_PATH = "./bin/Config";

    private final File fileConfig = new File(CONFIG_PATH);
    private String apiKey;

    private final HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response;

    public TMDatabase() {
        if(hasConfiguration())
            loadConfig();

        client = HttpClient.newHttpClient();
    }

    public String getApiKey(){return apiKey;}
    public void setApiKey(String apiKey){this.apiKey = apiKey;}

    public boolean hasConfiguration() {
        return fileConfig.exists();
    }
    public boolean verifyConfig(String apiKey) throws IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/authentication?api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONObject(response.body()).getBoolean("success");
    }

    public Collection<Movie> getMoviesByName(String name) throws IOException, InterruptedException {
        Collection<Movie> out = new ArrayList<>();

        String url = "https://api.themoviedb.org/3/search/movie?query="+FormatString.nameForHTTP(name)+"&include_adult=false&language=en-EN&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonMovies = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonMovies.length(); i++){
            JSONObject currMovie = jsonMovies.getJSONObject(i);
            JSONArray genresVett = currMovie.getJSONArray("genre_ids");

            Object backdropPath = currMovie.get("poster_path");
            if(!(backdropPath instanceof String))
                backdropPath = null;

            Movie movie = new Movie(
                    FormatString.compactTitle(currMovie.getString("title")),
                    FormatString.compactDescription(currMovie.getString("overview")),
                    currMovie.getString("release_date"),
                    Integer.toString(currMovie.getInt("id")),
                    (String)backdropPath);
            for(int j=0; j<genresVett.length(); j++)
                movie.addGenre(genresVett.getInt(j));

            out.add(movie);
        }

        return out;
    }
    public Collection<TVSerie> getTVSeriesByName(String name) throws IOException, InterruptedException {
        Collection<TVSerie> out = new ArrayList<>();

        String url = "https://api.themoviedb.org/3/search/tv?query="+FormatString.nameForHTTP(name)+"&include_adult=false&language=en-US&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonTVSeries = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonTVSeries.length(); i++){
            JSONObject currTVSerie = jsonTVSeries.getJSONObject(i);
            JSONArray genresVett = currTVSerie.getJSONArray("genre_ids");

            Object backdropPath = currTVSerie.get("poster_path");
            if(!(backdropPath instanceof String))
                backdropPath = null;

            TVSerie tvSerie = new TVSerie(
                    FormatString.compactTitle(currTVSerie.getString("name")),
                    FormatString.compactDescription(currTVSerie.getString("overview")),
                    currTVSerie.getString("first_air_date"),
                    Integer.toString(currTVSerie.getInt("id")),
                    (String)backdropPath);
            for(int j=0; j<genresVett.length(); j++)
                tvSerie.addGenre(genresVett.getInt(j));

            out.add(tvSerie);
        }

        return out;
    }

    public Movie getMovieById(String id) throws IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/movie/"+id+"?language=en-US&include_adult=false&api_key="+apiKey;
        String urlCredits = "https://api.themoviedb.org/3/movie/"+id+"/credits?language=en-US&include_adult=false&api_key="+apiKey;

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

        Object backdropPath = jsonMovie.get("poster_path");
        if(!(backdropPath instanceof String))
            backdropPath = null;

        Movie out = new Movie(
                FormatString.compactTitle(jsonMovie.getString("title")),
                FormatString.stringNormalize(jsonMovie.getString("overview")),
                jsonMovie.getString("release_date"),
                Integer.toString(jsonMovie.getInt("id")),
                (String)backdropPath,
                jsonMovie.getInt("runtime"),
                director);
        for(int i=0; i<genresVett.length(); i++){
            out.addGenre(genresVett.getJSONObject(i).getInt("id"));
        }

        return out;
    }
    public TVSerie getTVSerieById(String id) throws IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/tv/"+id+"?language=en-US&include_adult=false&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonTVSerie = new JSONObject(response.body());
        JSONArray genresVett = jsonTVSerie.getJSONArray("genres");

        Object backdropPath = jsonTVSerie.get("poster_path");
        if(!(backdropPath instanceof String))
            backdropPath = null;

        TVSerie out = new TVSerie(
                FormatString.compactTitle(jsonTVSerie.getString("name")),
                FormatString.stringNormalize(jsonTVSerie.getString("overview")),
                jsonTVSerie.getString("first_air_date"),
                Integer.toString(jsonTVSerie.getInt("id")),
                (String)backdropPath,
                jsonTVSerie.getInt("number_of_seasons"),
                jsonTVSerie.getInt("number_of_episodes"));

        for(int i=0; i<genresVett.length(); i++){
            out.addGenre(genresVett.getJSONObject(i).getInt("id"));
        }

        return out;
    }

    public static byte[] getBackdrop(String path) throws IOException, InterruptedException {
        String urlBackdrop = "https://image.tmdb.org/t/p/original"+path;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlBackdrop))
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
    }

    public void saveConfig() throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileConfig)))){
            oos.writeObject(apiKey);
        }
    }
    public void loadConfig() {
        try(ObjectInputStream dis = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileConfig)))){
            setApiKey((String)dis.readObject());
        } catch (Exception ex) {System.err.println(ex.getMessage());}
    }
    public void deleteConfig() {
        apiKey = "";
        fileConfig.delete();
    }
}
