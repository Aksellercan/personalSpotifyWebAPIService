package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Run_Modes.AutoMode;
import com.example.SpotifyWebAPI.Run_Modes.CLI_Interface;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.ConsoleInterface.*;

public class Main {

    private static void Initialize(FileUtil fileUtil, ConfigMaps configMaps, ProgramOptions programOptions, SpotifySession spotifySession) {
        fileUtil.readConfig();
        configMaps.setCredentials("client_id", "client_secret", "redirect_uri", "refresh_token", "playlist_id", "output_debug", "auto_mode","user_id", "test_mode");
        programOptions.setAutoMode(configMaps.isAutoMode());
        programOptions.setDebugMode(configMaps.isOutputDebug());
        programOptions.setTestMode(configMaps.isTestMode());
        spotifySession.setClient_id(configMaps.getClient_id());
        spotifySession.setClient_secret(configMaps.getClient_secret());
        spotifySession.setRedirect_uri(configMaps.getRedirect_uri());
        spotifySession.setRefresh_token(configMaps.getRefresh_token());
        spotifySession.setUser_id(configMaps.getUser_id());
    }

    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil();
        ConfigMaps configMaps = new ConfigMaps(fileUtil.getConfigMap());
        ProgramOptions programOptions = new ProgramOptions();
        SpotifySession spotifySession = new SpotifySession();
        Initialize(fileUtil, configMaps, programOptions, spotifySession);

        if (programOptions.isTestMode()){
            MainMenu mainMenu = new MainMenu(programOptions,spotifySession);
            mainMenu.userInterface();
        } else if (configMaps.isAutoMode()) {
            AutoMode autoMode = new AutoMode(fileUtil, configMaps);
            autoMode.runFunctions();
        } else {
            CLI_Interface cli = new CLI_Interface(fileUtil, configMaps);
            cli.initSession();
        }
    }
}