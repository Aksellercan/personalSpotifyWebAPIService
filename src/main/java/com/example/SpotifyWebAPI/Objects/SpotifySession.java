package com.example.SpotifyWebAPI.Objects;

public class SpotifySession {
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String code;

    public SpotifySession(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    //getters
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

    //setters
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