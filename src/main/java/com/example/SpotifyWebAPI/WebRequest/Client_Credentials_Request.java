package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.Authentication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;

public class Client_Credentials_Request {
    private String token;
    private Authentication authConnect;

    public Client_Credentials_Request(Authentication authConnect) {
        this.authConnect = authConnect;
        this.token = authConnect.getToken();
    }

    public void getPlaylist(String playlist_id) {
        try {
            String playlistURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + token;
            HttpURLConnection http = authConnect.connectHTTP(playlistURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            String playlistName = node.get("name").asText();
            String playlistDescription = node.get("description").asText();
            System.out.println("SpotifyWebAPI Playlist Details\nName: " + playlistName + "\nDescription: " + playlistDescription);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}