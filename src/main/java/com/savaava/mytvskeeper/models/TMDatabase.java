package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.utility.FormatString;

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
import java.util.Objects;

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
        if(hasConfiguration())
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

        String url = "https://api.themoviedb.org/3/search/movie?query="+FormatString.nameForHTTP(name)+"&include_adult=true&language=en-EN&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonMovies = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonMovies.length(); i++){
            JSONObject currMovie = jsonMovies.getJSONObject(i);
            JSONArray genresVett = currMovie.getJSONArray("genre_ids");

            Object backdropPath = currMovie.get("backdrop_path");
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

        String url = "https://api.themoviedb.org/3/search/tv?query="+FormatString.nameForHTTP(name)+"&include_adult=true&language=en-US&page=1'&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonTVSeries = new JSONObject(response.body()).getJSONArray("results");

        for(int i=0; i<jsonTVSeries.length(); i++){
            JSONObject currTVSerie = jsonTVSeries.getJSONObject(i);
            JSONArray genresVett = currTVSerie.getJSONArray("genre_ids");

            Object backdropPath = currTVSerie.get("backdrop_path");
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

        int min = jsonMovie.getInt("runtime");
        String duration;
        if(min != 0) {
            int h = 0;
            while (min >= 60) {
                h++;
                min -= 60;
            }
            duration = h==0 ? min+"min" : h+"h "+min+"min";
        }else {
            duration = "";
        }

        Object backdropPath = jsonMovie.get("backdrop_path");
        if(!(backdropPath instanceof String))
            backdropPath = null;

        Movie out = new Movie(
                FormatString.compactTitle(jsonMovie.getString("title")),
                FormatString.stringNormalize(jsonMovie.getString("overview")),
                jsonMovie.getString("release_date"),
                Integer.toString(jsonMovie.getInt("id")),
                (String)backdropPath,
                duration,
                director);
        for(int i=0; i<genresVett.length(); i++){
            out.addGenre(genresVett.getJSONObject(i).getInt("id"));
        }

        return out;
    }
    public TVSerie getTVSerieById(String id) throws IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/tv/"+id+"?language=en-US&include_adult=true&api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonTVSerie = new JSONObject(response.body());
        JSONArray genresVett = jsonTVSerie.getJSONArray("genres");

        Object backdropPath = jsonTVSerie.get("backdrop_path");
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

    public byte[] getBackdrop(String path) throws IOException, InterruptedException {
        String urlBackdrop = "https://image.tmdb.org/t/p/original"+path;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlBackdrop))
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
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
    public void deleteConfig() {
        apiKey = "";
        fileConfig.delete();
    }
}
