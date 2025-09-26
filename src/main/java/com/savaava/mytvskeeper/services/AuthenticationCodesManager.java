package com.savaava.mytvskeeper.services;

import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * E' una classe che gestisce il salvataggio e il caricamento dei tre codici di autenticazione per Supabase:
 * - accessToken: token di accesso che permette di fare richieste autenticati (è quello effettivamente usato per autenticarsi)
 * - refreshToken: token che permette di ottenere un nuovo accessToken quando quello attuale scade
 * - userId: è il supabase_id univoco dell'utente autenticato (usato per associare i dati all'utente corretto nel database) 
 */
public class AuthenticationCodesManager {
    private static final String CODES_FILE_PATH = "./bin/Codes.json";

    private String accessToken;
    private String refreshToken;
    private String userId;

    private AuthenticationCodesManager(){}

    public String getAccessToken() {
        return accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public String getUserId() {
        return userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAllCodes(String token, String refreshToken) {
        setAccessToken(token);
        setRefreshToken(refreshToken);

        // Decodifica userId dal JWT access_token (header.payload.signature)
        if (token != null) {
            try {
                String[] parts = token.split("\\.");
                if (parts.length >= 2) {
                    String payload = new String(java.util.Base64.getDecoder().decode(parts[1]));
                    JSONObject json = new JSONObject(payload);
                    userId = json.optString("sub"); // sub è l'user ID in JWT
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFiles() {
        try {
            JSONObject json = new JSONObject();
            json.put("accessToken", this.accessToken);
            json.put("refreshToken", this.refreshToken);
            json.put("userId", this.userId);
            Path path = Paths.get(CODES_FILE_PATH);
            Files.writeString(path, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AuthenticationCodesManager loadFromFile() {
        AuthenticationCodesManager out = new AuthenticationCodesManager();

        try {
            Path path = Paths.get(CODES_FILE_PATH);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                JSONObject json = new JSONObject(content);
                out.accessToken = json.optString("accessToken", null);
                out.refreshToken = json.optString("refreshToken", null);
                out.userId = json.optString("userId", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
}
