package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Run_Modes.AutoMode;
import com.example.SpotifyWebAPI.Run_Modes.CLI_Interface;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;

public class Main {
    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil();
        fileUtil.readConfig();
        ConfigMaps configMaps = new ConfigMaps(fileUtil.getConfigMap());
        configMaps.setCredentials("client_id", "client_secret", "redirect_uri", "refresh_token", "playlist_id", "output_debug", "auto_mode","user_id");
        if (configMaps.isAutoMode()) {
            AutoMode autoMode = new AutoMode(fileUtil, configMaps);
            autoMode.runFunctions();
        } else {
            CLI_Interface cli = new CLI_Interface(fileUtil, configMaps);
//            cli.setClient_id("client_id");
//            cli.setClient_secret("client_secret");
//            cli.setPlaylist_id("playlist_id");
//            cli.setredirect_uri("redirect_uri");
//            cli.setRefresh_token("refresh_token");
            cli.initSession();
        }
    }
}