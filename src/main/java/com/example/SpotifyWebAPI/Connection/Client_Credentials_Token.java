package com.example.SpotifyWebAPI.Connection;

import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;

public class Client_Credentials_Token {
    private final HTTPConnection httpConnection;
    private final SpotifySession spotifySession;

    public Client_Credentials_Token(HTTPConnection httpConnection, SpotifySession spotifySession) {
        this.httpConnection = httpConnection;
        this.spotifySession = spotifySession;
    }

    public void post_Access_Request() {
        try {
            HttpURLConnection http = httpConnection.connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.connect();
            String postBody = "grant_type=client_credentials&client_id=" + spotifySession.getClient_id() + "&client_secret=" + spotifySession.getClient_secret();
            httpConnection.postBody(http, postBody);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            spotifySession.setAccess_token(node.get("access_token").asText());
            Logger.INFO.Log("Token: " + spotifySession.getAccess_token(), false);
        } catch (Exception e) {
            Logger.ERROR.Log(e);
            return;
        }
        if (spotifySession.getAccess_token() == null) {
            Logger.WARN.Log("Access Token couldn't be acquired.");
        }
    }
}