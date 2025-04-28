package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.Authentication;
import com.example.SpotifyWebAPI.Playlist;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class User_Request {
    private Authentication authConnect;
    private String token;

    public User_Request(Authentication authConnect) {
        this.authConnect = authConnect;
        this.token = authConnect.getToken();
    }

    public void setToken(String token) {
        this.token = token;
    }

    //Needs playlist-modify-public Scope
    public void setPlaylistDescription(String playlist_id, String description) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + token;
            HttpURLConnection http = authConnect.connectHTTP(fullURL, "PUT", "Authorization", Bearer, "Content-Type", "application/json");
            http.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(new Playlist(description));
            System.out.println("json: " + json);
            try (OutputStream os = http.getOutputStream()) {
                os.write(json.getBytes());
            }
            int responseCode = http.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Updated playlist description to " + description + ". HTTP Response Code " + responseCode);
            } else {
                JsonNode node = mapper.readTree(http.getInputStream());
                throw new Exception("Failed to update playlist description to " + description + ". HTTP Response Code " + responseCode + ", " + node.get("message").asText());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}