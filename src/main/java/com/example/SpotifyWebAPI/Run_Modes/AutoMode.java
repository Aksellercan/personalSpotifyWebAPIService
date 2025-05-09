package com.example.SpotifyWebAPI.Run_Modes;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;

public class AutoMode {
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String playlist_id;
    private boolean auto_mode;
    private final HTTPConnection httpConnection = new HTTPConnection();
    private final ConfigMaps configMaps;
    private final FileUtil fileUtil;

    public AutoMode(FileUtil fileUtil, ConfigMaps configMaps) {
        this.configMaps = configMaps;
        this.fileUtil = fileUtil;
    }

    private void setConfigs() {
        client_id = configMaps.getClient_id();
        client_secret = configMaps.getClient_secret();
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
        SpotifySession spotify_session = new SpotifySession(client_id, client_secret);
        spotify_session.setRedirect_uri(redirect_uri);
        spotify_session.setRefresh_token(refresh_token);

        try {
            // Perform authentication
            User_Access_Token userAccessToken = new User_Access_Token(httpConnection, spotify_session);
            userAccessToken.refresh_token_with_User_Token();
            Logger.INFO.Log("Received the access token");
            //Get the size of the playlist
            Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnection, spotify_session);
            clientCredentialsRequest.getPlaylist(playlist_id);
            int playlistSize = clientCredentialsRequest.getplaylistSize();
            int readDescCount = Integer.parseInt(readString(clientCredentialsRequest.getplaylistDescription()));
            if (playlistSize == readDescCount) {
                Logger.INFO.Log("Playlist size is already set to " + playlistSize);
                Logger.INFO.Log("Completed the automated run, no changes made");
                return;
            } else if (playlistSize == 120) {
                Logger.INFO.Log("Maximum playlist size reached");
                auto_mode = false;
                fileUtil.writeConfig("client_id", client_id, "client_secret", client_secret, "redirect_uri",
                        redirect_uri, "refresh_token", refresh_token, "playlist_id", playlist_id, "auto_mode",
                        Boolean.toString(auto_mode), "output_debug", Boolean.toString(httpConnection.getDebugOutput()));
                return;
            }
            String playlistSizeString = String.valueOf(playlistSize);
            Logger.INFO.Log("Playlist size: " + playlistSizeString);
            // Set the playlist description
            User_Request userRequest = new User_Request(httpConnection, spotify_session);
            userRequest.setPlaylistDescription(playlist_id, playlistSizeString + "/120");
            Logger.INFO.Log("Completed the automated run");
        } catch (Exception e) {
            Logger.ERROR.Log("Auto Mode Error: " + e.getMessage());
        }
    }

    private String readString(String str) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '&') { // assuming '/' is encoded to '&#x2F;'
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}