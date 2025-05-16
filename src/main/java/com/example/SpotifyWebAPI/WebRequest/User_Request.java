package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.Playlist;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class User_Request {
    private final HTTPConnection httpConnection;
    private final SpotifySession spotifySession;

    public User_Request(HTTPConnection httpConnection, SpotifySession spotifySession) {
        this.httpConnection = httpConnection;
        this.spotifySession = spotifySession;
    }

    //Needs playlist-modify-public Scope
    public void setPlaylistDescription(String playlist_id, String description) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "PUT", "Authorization", Bearer, "Content-Type", "application/json");
            http.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String postBody = mapper.writeValueAsString(new Playlist(description));
            if (httpConnection.getDebugOutput()) Logger.DEBUG.Log("postBody: " + postBody);
            httpConnection.postBody(http, postBody);
            int responseCode = http.getResponseCode();
            if (responseCode == 200) {
                Logger.INFO.Log("Updated playlist description to " + description + ". HTTP Response Code " + responseCode);
            } else {
                JsonNode node = mapper.readTree(http.getInputStream());
                throw new Exception("Failed to update playlist description to " + description + ". HTTP Response Code " + responseCode + ", " + node.get("message").asText());
            }
        } catch (Exception e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }

    private String buildURL(String fullURL, int offset, int limit) {
        StringBuilder url = new StringBuilder(fullURL);
        if (offset >= 0) {
            url.append("offset=").append(offset).append("&limit=").append(limit);
        } else {
            url.append("limit=").append(limit);
        }
        return url.toString();
    }

    //Needs playlist-read-private Scope
    public void getPlaylistItems(String playlist_id, int offset, int limit) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/"+ playlist_id + "/tracks?";
            fullURL = buildURL(fullURL, offset, limit);
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            HashMap<String, String> songs = new HashMap<>();
            for (JsonNode key : node.get("items")) {
                if (songs.containsKey(key.get("track").get("name").asText())) {
                    Logger.INFO.Log("Duplicate song found: " + key.get("track").get("name").asText());
                    songs.put(key.get("track").get("name").asText() + " duplicate ", key.get("track").get("uri").asText());
                    continue;
                }
                songs.put(key.get("track").get("name").asText(), key.get("track").get("uri").asText());
                Logger.INFO.Log("Song: " + key.get("track").get("name").asText() + " URI: " + key.get("track").get("uri").asText());
            }
        } catch (Exception e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }
}