package com.savaava.mytvskeeper.services;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * La classe è un client Supabase che si interfaccia col progetto Supabase MyTVsKeeper.
 * - SUPABASE_URL: l'URL del progetto
 * - SUPABASE_ANON_KEY: la chiave anonima per accedere al progetto
 * - Implementa le interfacce AccessManager e QueryManager per gestire l'autenticazione e le operazioni DQL e DML sul database.
 */
public class SupabaseClient implements AccessManager, QueryManager {
    private final HttpClient httpClient;

    private static final String SUPABASE_URL = "https://qozkgaygxjyeufubhruu.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFvemtnYXlneGp5ZXVmdWJocnV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg3MTc1MzIsImV4cCI6MjA3NDI5MzUzMn0.FVUOIXEab8SAUvSLycV4CHxp8CVtuaTHOkjhkKexHYo"; // Sostituisci con la tua anon key

    //TODO: usare AuthenticationCodesManager anzichè i tre valori separati
    private AuthenticationCodesManager authenticationCodesManager;

    private HttpServer callbackServer;
    private CompletableFuture<String[]> tokenFuture; // [accessToken, refreshToken]

    public SupabaseClient() {
        this.authenticationCodesManager = AuthenticationCodesManager.loadFromFile();
        this.httpClient = HttpClient.newHttpClient();
    }

    public AuthenticationCodesManager getAuthenticationCodesManager(){
        return this.authenticationCodesManager;
    }

    public boolean refreshAccessToken() {
        if (this.authenticationCodesManager.getRefreshToken() == null) return false;
        try {
            JSONObject body = new JSONObject();
            body.put("refresh_token", this.authenticationCodesManager.getRefreshToken());
            HttpResponse<String> response = sendRequest("POST", "/auth/v1/token?grant_type=refresh_token", body.toString(), false);
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                this.authenticationCodesManager.setAccessToken(json.optString("access_token"));
                this.authenticationCodesManager.setRefreshToken(json.optString("refresh_token")); // Aggiorna se fornito
                this.authenticationCodesManager.saveToFiles();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper per richieste HTTP
    private HttpResponse<String> sendRequest(String method, String endpoint, String body, boolean authRequired) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("apikey", SUPABASE_ANON_KEY);

        if (authRequired && this.authenticationCodesManager.getAccessToken() != null) {
            builder.header("Authorization", "Bearer " + this.authenticationCodesManager.getAccessToken());
        }

        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body != null ? body : "{}"));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    // AccessManager implementation
    @Override
    public boolean signIn(String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            HttpResponse<String> response = sendRequest("POST", "/auth/v1/token?grant_type=password", body.toString(), false);
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                this.authenticationCodesManager.setAccessToken(json.optString("access_token"));
                this.authenticationCodesManager.setRefreshToken(json.optString("refresh_token"));
                this.authenticationCodesManager.setUserId(json.optJSONObject("user").optString("id"));
                this.authenticationCodesManager.saveToFiles();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean signInOAuth(AccessProvider provider) {
        try {
            // Avvia server HTTP locale per gestire il callback OAuth
            callbackServer = HttpServer.create(new InetSocketAddress(8080), 0);
            tokenFuture = new CompletableFuture<>();
            callbackServer.createContext("/callback", new OAuthCallbackHandler());
            callbackServer.start();

            // Genera URL di autorizzazione OAuth
            String providerName = provider.name().toLowerCase();
            String authUrl = SUPABASE_URL + "/auth/v1/authorize?provider=" + providerName + "&redirect_to=http://localhost:8080/callback";

            // Apri URL nel browser predefinito
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(authUrl));

            // Aspetta che i token vengano catturati dal callback (blocca fino al completamento)
            String[] tokens = tokenFuture.get();
            if (tokens != null && tokens[0] != null) {
                this.authenticationCodesManager.setAllCodes(tokens[0], tokens[1]);
                this.authenticationCodesManager.saveToFiles();
                callbackServer.stop(0); // Ferma il server
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (callbackServer != null) {
                callbackServer.stop(0);
            }
        }
        return false;
    }

    // Handler per il callback OAuth
    private class OAuthCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String responseMessage;
            if (query != null && query.contains("access_token")) {
                String[] tokens = extractTokensFromQuery(query);
                tokenFuture.complete(tokens);
                responseMessage = "Autenticazione riuscita! Puoi chiudere questa finestra.";
                exchange.sendResponseHeaders(200, responseMessage.length());
            } else {
                tokenFuture.complete(null);
                responseMessage = "Errore nell'autenticazione.";
                exchange.sendResponseHeaders(400, responseMessage.length());
            }
            exchange.getResponseBody().write(responseMessage.getBytes());
            exchange.close();
        }

        private String[] extractTokensFromQuery(String query) {
            String accessToken = null;
            String refreshToken = null;
            for (String param : query.split("&")) {
                if (param.startsWith("access_token=")) {
                    accessToken = param.substring(13);
                } else if (param.startsWith("refresh_token=")) {
                    refreshToken = param.substring(14);
                }
            }
            return new String[]{accessToken, refreshToken};
        }
    }

    @Override
    public boolean signOut() {
        try {
            HttpResponse<String> response = sendRequest("POST", "/auth/v1/logout", null, true);
            if (response.statusCode() == 204) {
                this.authenticationCodesManager.setRefreshToken(null);
                this.authenticationCodesManager.setAccessToken(null);
                this.authenticationCodesManager.setUserId(null);
                this.authenticationCodesManager.saveToFiles();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticationCodesManager.getAccessToken() != null && this.authenticationCodesManager.getUserId() != null;
    }

    // QueryManager implementation
    /**
     * Questo metodo deve invocare la FUNCTION del DB public.insert_my_profile, per registrare l'utente corrente nella tabella profiles
     * con il nickname passato come parametro (se non nullo).
     */
    @Override
    public JSONObject insertProfile(String nickname) {
        if (!isAuthenticated()) return null;
        try {
            JSONObject body = new JSONObject();
            if (nickname != null) body.put("p_nickname", nickname);
            HttpResponse<String> response = sendRequest("POST", "/rest/v1/rpc/insert_my_profile", body.toString(), true);
            if (response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                return jsonArray.optJSONObject(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray getVideos() {
        if (!isAuthenticated()) return null;
        try {
            HttpResponse<String> response = sendRequest("GET", "/rest/v1/videos", null, true);
            if (response.statusCode() == 200) {
                return new JSONArray(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject insertVideo(int tmdbId, int typeVideo, boolean isStarted, boolean isFinished, Integer rating, String notes) {
        if (!isAuthenticated()) return null;
        try {
            JSONObject body = new JSONObject();
            body.put("owner_id", this.authenticationCodesManager.getUserId());
            body.put("tmdb_id", tmdbId);
            body.put("type_video", typeVideo);
            body.put("is_started", isStarted);
            body.put("is_finished", isFinished);
            if (rating != null) body.put("rating", rating);
            if (notes != null) body.put("notes", notes);
            HttpResponse<String> response = sendRequest("POST", "/rest/v1/videos", body.toString(), true);
            if (response.statusCode() == 201) {
                return new JSONObject(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateVideo(int id, boolean isStarted, boolean isFinished, Integer rating, String notes) {
        if (!isAuthenticated()) return false;
        try {
            JSONObject body = new JSONObject();
            body.put("is_started", isStarted);
            body.put("is_finished", isFinished);
            if (rating != null) body.put("rating", rating);
            if (notes != null) body.put("notes", notes);
            HttpResponse<String> response = sendRequest("PATCH", "/rest/v1/videos?id=eq." + id, body.toString(), true);
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteVideo(int id) {
        if (!isAuthenticated()) return false;
        try {
            HttpResponse<String> response = sendRequest("DELETE", "/rest/v1/videos?id=eq." + id, null, true);
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
