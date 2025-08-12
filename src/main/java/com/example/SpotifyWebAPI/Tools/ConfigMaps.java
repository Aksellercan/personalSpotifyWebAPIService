package com.example.SpotifyWebAPI.Tools;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;

import java.util.HashMap;

/**
 * Maps read configuration HashMap to variables then makes them available with getters
 */
public class ConfigMaps {
    private final HashMap<String,String> configMap;

    public ConfigMaps(HashMap<String,String> configMap) {
        this.configMap = configMap;
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
                        SpotifySession.getInstance().setUser_id(value);
                        break;
                    case "client_id":
                        SpotifySession.getInstance().setClient_id(value);
                        break;
                    case "client_secret":
                        SpotifySession.getInstance().setClient_secret(value);
                        break;
                    case "redirect_uri":
                        SpotifySession.getInstance().setRedirect_uri(value);
                        break;
                    case "refresh_token":
                        SpotifySession.getInstance().setRefresh_token(value);
                        break;
                    case "playlist_id":
                        SpotifySession.getInstance().setPlaylist_id(value);
                        break;
                    case "output_debug":
                        Logger.setDebugOutput(Boolean.parseBoolean(value));
                        break;
                    case "auto_mode":
                        ProgramOptions.getInstance().setAutoMode(Boolean.parseBoolean(value));
                        break;
                    case "launch_gui":
                        if (value == null) {
                            ProgramOptions.getInstance().setLAUNCH_GUI(true);
                            break;
                        }
                        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                            ProgramOptions.getInstance().setLAUNCH_GUI(value.equalsIgnoreCase("true"));
                            break;
                        }
                        ProgramOptions.getInstance().setLAUNCH_GUI(true);
                        break;
                    case "verbose_log_file":
                        Logger.setVerboseLogFile(Boolean.parseBoolean(value));
                        break;
                }
            } else {
                Logger.WARN.LogSilently("Credential " + credential + " not found in config map");
            }
        }
    }
}