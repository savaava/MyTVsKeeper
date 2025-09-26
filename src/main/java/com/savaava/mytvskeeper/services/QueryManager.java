package com.savaava.mytvskeeper.services;

import org.json.JSONArray;
import org.json.JSONObject;

public interface QueryManager {
    JSONObject insertProfile(String nickname);
    JSONArray getVideos();
    JSONObject insertVideo(int tmdbId, int typeVideo, boolean isStarted, boolean isFinished, Integer rating, String notes);
    boolean updateVideo(int id, boolean isStarted, boolean isFinished, Integer rating, String notes);
    boolean deleteVideo(int id);
}
