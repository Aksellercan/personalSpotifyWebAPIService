package com.example.SpotifyWebAPI.Run_Modes;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoMode {
    private String user_id;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String playlist_id;
    private boolean auto_mode;
    private final HTTPConnection httpConnection = HTTPConnection.getInstance();
    private final ConfigMaps configMaps;
    private final FileUtil fileUtil;

    public AutoMode(FileUtil fileUtil, ConfigMaps configMaps) {
        this.configMaps = configMaps;
        this.fileUtil = fileUtil;
    }

    private void setConfigs() {
        client_id = configMaps.getClient_id();
        client_secret = configMaps.getClient_secret();
        user_id = configMaps.getUser_id();
        redirect_uri = configMaps.getRedirect_uri();
        refresh_token = configMaps.getRefresh_token();
        playlist_id = configMaps.getPlaylist_id();
        auto_mode = configMaps.isAutoMode();
        httpConnection.setDebugOutput(configMaps.isOutputDebug());
    }

    public void runFunctions() {
        // Initialize Spotify session
        Logger.INFO.Log("Starting AutoMode.runFunctions()");
        setConfigs();
        SpotifySession spotify_session = SpotifySession.getInstance();
        spotify_session.setClient_id(client_id);
        spotify_session.setClient_secret(client_secret);
        spotify_session.setRedirect_uri(redirect_uri);
        spotify_session.setRefresh_token(refresh_token);

        try {
            // Perform authentication
            User_Access_Token userAccessToken = new User_Access_Token();
            userAccessToken.refresh_token_with_User_Token();
            Logger.INFO.Log("Received the access token");
            User_Request userRequest = new User_Request();
            //Get the size of the playlist
            Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
            clientCredentialsRequest.getPlaylist(playlist_id);
            int playlistSize = clientCredentialsRequest.getplaylistSize();
            String stringDescCount = readString(clientCredentialsRequest.getplaylistDescription());
            int readDescCount;
            Pattern pattern= Pattern.compile("[a-zA-Z/]");
            Matcher matcher = pattern.matcher(stringDescCount);
            if (matcher.find()) {
                Logger.INFO.Log("Playlist size: " + playlistSize);
                // Set the playlist description
                userRequest.setPlaylistDetails(playlist_id, clientCredentialsRequest.getplaylistName(),playlistSize + "/120",true,false);
                Logger.INFO.Log("Completed the automated run");
                return;
            } else {
                readDescCount = Integer.parseInt(stringDescCount);
            }
            if (playlistSize == 120) {
                String prevPlaylistName = clientCredentialsRequest.getplaylistName();
                int nextNumber = nextNumber(prevPlaylistName);
                userRequest.createPlaylist(user_id, "Favorites " + nextNumber, "0/120");
                Logger.INFO.Log("New playlist created with playlist_id: " + playlist_id + " and Name: " + "Favorites " + nextNumber);
                auto_mode = false;
                fileUtil.writeConfig("client_id", client_id, "client_secret", client_secret, "redirect_uri",
                        redirect_uri, "refresh_token", refresh_token, "playlist_id", userRequest.getPlaylist().getPlaylist_id(), "auto_mode",
                        Boolean.toString(auto_mode), "output_debug", Boolean.toString(httpConnection.getDebugOutput()), "user_id",user_id);
                return;
            } else if (playlistSize == readDescCount) {
                Logger.INFO.Log("Playlist size is already set to " + playlistSize);
                Logger.INFO.Log("Completed the automated run, no changes made");
                return;
            }
            Logger.INFO.Log("Playlist size: " + playlistSize);
            // Set the playlist description
            userRequest.setPlaylistDetails(playlist_id, clientCredentialsRequest.getplaylistName(),playlistSize + "/120",true,false);
            Logger.INFO.Log("Completed the automated run");
        } catch (Exception e) {
            Logger.ERROR.LogException(e,"Auto Mode");
        }
    }

    private int nextNumber(String str) {
        int number = 0;
        String[] splitLine;
        if (str.isEmpty()) {
            return 0;
        }
        splitLine = str.split(" ");
        if (splitLine.length == 2) {
            number = Integer.parseInt(splitLine[1]);
        }
        number++;
        Logger.DEBUG.Log("Next number: " + number);
        return number;
    }

    private String readString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c == '&') && (str.charAt(i+1) == '#')) { // assuming '/' is encoded to '&#x2F;'
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}