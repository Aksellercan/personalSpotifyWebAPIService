package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;

public class Client_Credentials_Request {
    private final HTTPConnection httpConnection;
    private final SpotifySession spotifySession;

    public Client_Credentials_Request(HTTPConnection httpConnection, SpotifySession spotifySession) {
        this.httpConnection = httpConnection;
        this.spotifySession = spotifySession;
    }

    public void getPlaylist(String playlist_id) {
        try {
            String playlistURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(playlistURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            String playlistName = node.get("name").asText();
            String playlistDescription = node.get("description").asText();
            System.out.println("SpotifyWebAPI Playlist Details\nName: " + playlistName + "\nDescription: " + playlistDescription);
        } catch (Exception e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }

    public int getPlaylistSize(String playlist_id) {
        try {
            String playlistURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(playlistURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            String playlistSize = node.get("tracks").get("total").asText();
            System.out.println("SpotifyWebAPI Playlist Details\nPlaylist Size: " + playlistSize);
            return Integer.parseInt(playlistSize);
        } catch (Exception e) {
            Logger.ERROR.Log(e.getMessage());
        }
        return 0;
    }
}