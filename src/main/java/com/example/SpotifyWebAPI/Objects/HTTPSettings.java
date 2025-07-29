package com.example.SpotifyWebAPI.Objects;

public class HTTPSettings {
    private int serverPort = 8080;
    private int backlog_limit = 10;
    private String serverName = "Spotify Web API HTTP 1.0 Server";

    public int getServerPort() {
        return serverPort;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public int getBacklog_limit() {
        return backlog_limit;
    }
    public void setBacklog_limit(int backlog_limit) {
        this.backlog_limit = backlog_limit;
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
