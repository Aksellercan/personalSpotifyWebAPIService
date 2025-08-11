package com.example.SpotifyWebAPI.Tools;

import java.util.HashMap;

/**
 * Maps read configuration HashMap to variables then makes them available with getters
 */
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

    /**
     * Returns the mapped variable launch_gui by default it is set to true.
     * Even if the config line launch_gui is malformed it will launch GUI
     * @return  boolean launch_gui
     */
    public boolean isLaunchGui() {
        if (launch_gui == null) {
            return true;
        }
        Logger.DEBUG.Log("launch_gui=" + launch_gui);
        if (launch_gui.equalsIgnoreCase("true") || launch_gui.equalsIgnoreCase("false")) {
            return launch_gui.equalsIgnoreCase("true");
        }
        return true;
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

    /**
     * Maps credentials to variables. Skips and logs with WARN severity if credential is not recognised or doesn't exist
     * @param credentials   Credentials to be mapped
     */
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