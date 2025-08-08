package com.example.SpotifyWebAPI.Objects;

/**
 * Object to hold session data which will be used to construct requests
 */
public final class SpotifySession {
    private static SpotifySession instance;
    private String user_id;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String code;
    private String playlist_id;

    private SpotifySession() {}

    //Instance Check
    public static SpotifySession getInstance() {
        if (instance == null) {
            instance = new SpotifySession();
        }
        return instance;
    }

    //getters
    public String getUser_id() {
        return user_id;
    }
    public String getClient_id() {
        return client_id;
    }
    public String getClient_secret() {
        return client_secret;
    }
    public String getRedirect_uri() {
        return redirect_uri;
    }
    public String getAccess_token() {
        return access_token;
    }
    public String getToken_type() {
        return token_type;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public String getCode() {
        return code;
    }
    public String getPlaylist_id() {
        return playlist_id;
    }

    //setters
    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public void setCode(String code) {
        this.code = code;
    }
}