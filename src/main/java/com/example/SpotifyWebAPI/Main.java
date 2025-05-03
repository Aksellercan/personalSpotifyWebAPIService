package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;

public class Main {
    public static void main(String[] args){
        FileUtil fileUtil = new FileUtil();
        fileUtil.readConfig();
        ConfigMaps configMaps = new ConfigMaps(fileUtil.getConfigMap());
        configMaps.setCredentials("client_id", "client_secret", "redirect_uri", "refresh_token", "playlist_id", "output_debug", "auto_mode");
        if (configMaps.isAutoMode()) {
            AutoMode autoMode = new AutoMode(fileUtil, configMaps);
            autoMode.runFunctions();
        } else {
            CLI_Interface cli = new CLI_Interface(fileUtil, configMaps);
            cli.initSession();
        }
    }
}