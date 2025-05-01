package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.Playlist;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;

public class User_Request {
    private HTTPConnection httpConnection;
    private SpotifySession spotifySession;

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
            System.out.println("postBody: " + postBody);
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
}