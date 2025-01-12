package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.net.http.HttpResponse;

import java.util.Collection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

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

    public Collection<Movie> getMoviesByName(String name){
        return null;
    }
    public Movie getMovieById(String id) throws Exception {
        String url = "https://api.themoviedb.org/3/movie/"+id+"?api_key="+apiKey;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

//        JSONObject
//        System.out.println(JSO);

        return null;
    }

    public Collection<TVSerie> getTVSeriesByName(String name) {
        return null;
    }
    public TVSerie getTVSerieById(String id) {
        return null;
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
