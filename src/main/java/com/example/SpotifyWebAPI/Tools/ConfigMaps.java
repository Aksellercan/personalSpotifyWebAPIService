package com.example.SpotifyWebAPI.Tools;

import java.util.HashMap;

public class ConfigMaps {
    private final HashMap<String,String> configMap;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String playlist_id;
    private boolean output_debug;
    private boolean auto_mode;
    private String user_id;
    private String launch_gui;

    public ConfigMaps(HashMap<String,String> configMap) {
        this.configMap = configMap;
    }

    public String isLaunchGui() {
        if (launch_gui == null) {
            return null;
        }
        return launch_gui;
    }
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
    public String getRefresh_token() {
        return refresh_token;
    }
    public String getPlaylist_id() {
        return playlist_id;
    }
    public boolean isOutputDebug() {
        return output_debug;
    }
    public boolean isAutoMode() {
        return auto_mode;
    }

    public void setCredentials(String... credentials) {
        if (credentials.length == 0) {
            Logger.INFO.LogSilently("No credentials provided");
            return;
        }
        for (String credential : credentials) {
            if (configMap.containsKey(credential)) {
                String value = configMap.get(credential);
                switch (credential) {
                    case "user_id":
                        this.user_id = value;
                        break;
                    case "client_id":
                        this.client_id = value;
                        break;
                    case "client_secret":
                        this.client_secret = value;
                        break;
                    case "redirect_uri":
                        this.redirect_uri = value;
                        break;
                    case "refresh_token":
                        this.refresh_token = value;
                        break;
                    case "playlist_id":
                        this.playlist_id = value;
                        break;
                    case "output_debug":
                        this.output_debug = Boolean.parseBoolean(value);
                        break;
                    case "auto_mode":
                        this.auto_mode = Boolean.parseBoolean(value);
                        break;
                    case "launch_gui":
                        this.launch_gui = value;
                        break;
                }
            } else {
                Logger.WARN.LogSilently("Credential " + credential + " not found in config map");
            }
        }
    }
}