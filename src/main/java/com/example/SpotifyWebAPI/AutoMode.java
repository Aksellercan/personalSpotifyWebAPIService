package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;

public class AutoMode {
    private SpotifySession spotify_session;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String playlist_id;
    private HTTPConnection httpConnection = new HTTPConnection();
    private final FileUtil fileUtil;
    private ConfigMaps configMaps;

    public AutoMode(FileUtil fileUtil, ConfigMaps configMaps) {
        this.fileUtil = fileUtil;
        this.configMaps = configMaps;
    }

    private void setConfigs() {
        client_id = configMaps.getClient_id();
        client_secret = configMaps.getClient_secret();
        redirect_uri = configMaps.getRedirect_uri();
        refresh_token = configMaps.getRefresh_token();
        playlist_id = configMaps.getPlaylist_id();
        httpConnection.setDebugOutput(configMaps.isOutputDebug());
    }

    public void runFunctions() {
        // Initialize Spotify session
        setConfigs();
        spotify_session = new SpotifySession(client_id, client_secret);
        spotify_session.setRedirect_uri(redirect_uri);
        spotify_session.setRefresh_token(refresh_token);

        try {
        // Perform authentication
        User_Access_Token userAccessToken = new User_Access_Token(httpConnection, spotify_session);
        userAccessToken.refresh_token_with_User_Token();

        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnection, spotify_session);
        int playlistSize = clientCredentialsRequest.getPlaylistSize(playlist_id);
        String playlistSizeString = String.valueOf(playlistSize);
        Logger.INFO.Log("Playlist size: " + playlistSizeString);

        User_Request userRequest = new User_Request(httpConnection, spotify_session);
        userRequest.setPlaylistDescription(playlist_id, playlistSizeString + "/120");
        } catch (Exception e) {
            Logger.ERROR.Log("Auto Mode Error: " + e.getMessage());
        }
    }
}