package com.example.SpotifyWebAPI.Tokens;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;

public class Client_Credentials_Token {
    private final HTTPConnection httpConnection = HTTPConnection.getInstance();
    private final SpotifySession spotifySession = SpotifySession.getInstance();

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
            Logger.ERROR.LogException(e);
            return;
        }
        if (spotifySession.getAccess_token() == null) {
            Logger.WARN.Log("Access Token couldn't be acquired.");
        }
    }
}