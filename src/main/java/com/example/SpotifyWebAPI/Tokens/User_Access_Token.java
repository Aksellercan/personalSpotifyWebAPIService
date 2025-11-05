package com.example.SpotifyWebAPI.Tokens;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Gets refresh token with scopes set in Fallback page
 */
public class User_Access_Token {
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private final HTTPConnection httpConnection = new HTTPConnection();

    /**
     * Encodes Authorization Bearer with Base64
     *
     * @return Base64 encoded Authorization Bearer
     */
    private String getEncodedString() {
        return Base64.getEncoder().encodeToString((spotifySession.getClient_id() + ":" + spotifySession.getClient_secret()).getBytes());
    }

    /**
     * Posts request to get refresh token. Requires "code" from spotify to get refresh token with set privileges
     */
    public boolean get_Refresh_Token() {
        try {
            if (spotifySession.getCode() == null) {
                throw new NullPointerException("Spotify Code is null");
            }
            String encodeString = getEncodedString(); //Base64 encoding as required by Spotify
            String Basic = "Basic " + encodeString;
            HttpURLConnection http = httpConnection.connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type", "application/x-www-form-urlencoded", "Authorization", Basic);
            http.setDoOutput(true);
            http.connect();
            String postBody = "grant_type=authorization_code" + "&code=" + URLEncoder.encode(spotifySession.getCode(), "UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode(spotifySession.getRedirect_uri(), "UTF-8");
            if (Logger.getDebugOutput()) System.out.println("Full PostBody: " + postBody);
            httpConnection.postBody(http, postBody);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            spotifySession.setAccess_token(node.get("access_token").asText()); //exchange token
            Logger.INFO.Log("new Access Token: " + spotifySession.getAccess_token(), false);
            spotifySession.setRefresh_token(node.get("refresh_token").asText()); //get refresh token
            Logger.INFO.Log("Refresh Token to take note: " + spotifySession.getRefresh_token(), false);
            Logger.INFO.Log("Refreshed token: " + spotifySession.getAccess_token(), false);
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Failed to request refresh token", true);
            return false;
        }
        if (spotifySession.getRefresh_token() == null) {
            Logger.WARN.Log("Refresh token couldn't be acquired.");
            return false;
        }
        return true;
    }

    /**
     * Prints the saved User data
     */
    public void printData() {
        System.out.println("Debug Output: " + Logger.getDebugOutput());
        System.out.println("Refresh Token: " + spotifySession.getRefresh_token());
        System.out.println("Client id: " + spotifySession.getClient_id());
        System.out.println("Client secret: " + spotifySession.getClient_secret());
        System.out.println("Redirect uri: " + spotifySession.getRedirect_uri());
    }

    /**
     * Refreshes Token using refresh token from the configuration
     */
    public void refresh_token_with_User_Token() {
        try {
            String encodeString = getEncodedString(); //Base64 encoding as required by Spotify
            String Basic = "Basic " + encodeString;
            HttpURLConnection http = httpConnection.connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type", "application/x-www-form-urlencoded", "Authorization", Basic);
            http.setDoOutput(true);
            http.connect();
            String postBody = "grant_type=refresh_token" + "&refresh_token=" + spotifySession.getRefresh_token()
                    + "&client_id=" + spotifySession.getClient_id();
            httpConnection.postBody(http, postBody);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            spotifySession.setAccess_token(node.get("access_token").asText()); //exchange token
            Logger.INFO.Log("new Access Token: " + spotifySession.getAccess_token(), false);
            if (node.get("refresh_token") == null) {
                Logger.INFO.Log("No refresh Token returned");
            } else {
                spotifySession.setRefresh_token(node.get("refresh_token").asText()); //debug
                Logger.INFO.Log("Refresh Token to take note: " + spotifySession.getRefresh_token(), false);
            }
        } catch (IOException e) {
            Logger.ERROR.LogException(e);
        }
    }
}