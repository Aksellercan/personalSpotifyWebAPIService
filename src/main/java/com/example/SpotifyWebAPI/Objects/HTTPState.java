package com.example.SpotifyWebAPI.Objects;

import com.example.SpotifyWebAPI.HTTP.HTTPServer;

public class HTTPState {
    private String serverName;
    private HTTPServer server;

    public HTTPState(String serverName, HTTPServer server) {
        this.serverName = serverName;
        this.server = server;
    }

    public String getServerName() {
        return this.serverName;
    }

    public HTTPServer getServer() {
        return this.server;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServer(HTTPServer server) {
        this.server = server;
    }
}
