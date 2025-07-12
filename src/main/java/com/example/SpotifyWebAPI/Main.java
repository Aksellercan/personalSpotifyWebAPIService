package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.JavaFXInterface.GUI;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Run_Modes.AutoMode;
import com.example.SpotifyWebAPI.Run_Modes.CLI_Interface;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.ConsoleInterface.*;

public class Main {

    private static void AutoModeRequirementCheck(ProgramOptions programOptions, SpotifySession spotifySession) {
        System.out.println("Auto Mode Requirement Check");
        System.out.println((spotifySession.getUser_id() == null ? "User ID not set!" : "Set User ID: " + spotifySession.getUser_id()));
        System.out.println((spotifySession.getClient_id() == null ? "Client ID not set!" : "Set Client ID: " + spotifySession.getClient_id()));
        System.out.println((spotifySession.getClient_secret() == null ? "Client Secret not set!" : "Set Client Secret: " + spotifySession.getClient_secret()));
        System.out.println((spotifySession.getRedirect_uri() == null ? "Redirect URI not set!" : "Set Redirect URI: " + spotifySession.getRedirect_uri()));
        System.out.println((spotifySession.getRefresh_token() == null ? "Refresh Token not set!" : "Set Refresh Token: " + spotifySession.getRefresh_token()));
        System.out.println((programOptions.getPlaylist_id() == null) ? "Playlist ID not set!" : "Set Playlist ID: " + programOptions.getPlaylist_id());
    }

    private static void HelpMenu() {
        System.out.println("Usage: program [OPTION]");
        System.out.println("\nOptions:");
        System.out.println("  --req                                         Run requirement checks and exit");
        System.out.println("  --auto-mode                                   Run auto mode functions");
        System.out.println("  --gui                                         Launch GUI interface");
        System.out.println("  --cli                                         Launch CLI interface normal mode");
        System.out.println("  --cli-test                                    Launch CLI interface in test mode");
        System.out.println("\nUsage: program --set [CONFIGURATION] <value>");
        System.out.println("\nConfiguration Options:");
        System.out.println("  --playlist-id <id>                            Set default playlist ID");
        System.out.println("  --userid <id>                                 Set Spotify user ID");
        System.out.println("  --client-id <id>                              Set Spotify client ID");
        System.out.println("  --client-secret <secret>                      Set Spotify client secret");
        System.out.println("  --redirect-uri <uri>                          Set Spotify redirect URI");
        System.out.println("  --refresh-token <token>                       Set refresh token for session");
        System.out.println("\nOther:");
        System.out.println("  --help                                        Display this help menu");
        System.out.println("  --get-refresh-token <code> <redirect uri>     get refresh token");
    }

    private static void SaveConfig(FileUtil fileUtil, SpotifySession spotifySession, ProgramOptions programOptions) {
        fileUtil.writeConfig("client_id", spotifySession.getClient_id(), "client_secret", spotifySession.getClient_secret(), "redirect_uri",
                spotifySession.getRedirect_uri(), "refresh_token", spotifySession.getRefresh_token(), "playlist_id", programOptions.getPlaylist_id(), "auto_mode",
                Boolean.toString(programOptions.isAutoMode()), "output_debug", Boolean.toString(programOptions.isDebugMode()), "user_id", spotifySession.getUser_id());
    }

    private static void Initialize(FileUtil fileUtil, ConfigMaps configMaps, ProgramOptions programOptions, SpotifySession spotifySession) {
        fileUtil.readConfig();
        configMaps.setCredentials("client_id", "client_secret", "redirect_uri", "refresh_token", "playlist_id", "output_debug", "auto_mode", "user_id");
        programOptions.setAutoMode(configMaps.isAutoMode());
        programOptions.setDebugMode(configMaps.isOutputDebug());
        //Temporary
        HTTPConnection httpConnection = HTTPConnection.getInstance();
        httpConnection.setDebugOutput(programOptions.isDebugMode());
        programOptions.setPlaylist_id(configMaps.getPlaylist_id());
        spotifySession.setClient_id(configMaps.getClient_id());
        spotifySession.setClient_secret(configMaps.getClient_secret());
        spotifySession.setRedirect_uri(configMaps.getRedirect_uri());
        spotifySession.setRefresh_token(configMaps.getRefresh_token());
        spotifySession.setUser_id(configMaps.getUser_id());
    }

    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil();
        ConfigMaps configMaps = new ConfigMaps(fileUtil.getConfigMap());
        ProgramOptions programOptions = ProgramOptions.getInstance();
        SpotifySession spotifySession = SpotifySession.getInstance();
        Initialize(fileUtil, configMaps, programOptions, spotifySession);

        if (args.length == 1) {
            switch (args[0]) {
                case "--gui":
                    GUI.launch(GUI.class, args);
                    return;
                case "--req":
                    AutoModeRequirementCheck(programOptions, spotifySession);
                    return;
                case "--auto-mode":
                    if (programOptions.getPlaylist_id() == null || spotifySession.getRefresh_token() == null
                            || spotifySession.getUser_id() == null ||  spotifySession.getClient_id() == null
                            || spotifySession.getClient_secret() == null || spotifySession.getRedirect_uri() == null) {
                        System.out.println("Required parameters not set!");
                        return;
                    }
                    AutoMode autoMode = new AutoMode(fileUtil, configMaps);
                    autoMode.runFunctions();
                    return;
                case "--cli":
                    MainMenu mainMenu = new MainMenu(fileUtil);
                    mainMenu.userInterface();
                    return;
                case "--cli-test":
                    CLI_Interface cli = new CLI_Interface(fileUtil, configMaps);
                    cli.initSession();
                    return;
                case "--help":
                    HelpMenu();
                    return;
                default:
                    System.out.println("Unknown argument: " + args[0]);
                    return;
            }
        }
        if (args.length == 3) {
            if (args[0].equals("--set")) {
                switch (args[1]) {
                    case "--playlist-id":
                        programOptions.setPlaylist_id(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    case "--userid":
                        spotifySession.setUser_id(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    case "--client-id":
                        spotifySession.setClient_id(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    case "--client-secret":
                        spotifySession.setClient_secret(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    case "--redirect-uri":
                        spotifySession.setRedirect_uri(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    case "--refresh-token":
                        spotifySession.setRefresh_token(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                        return;
                    default:
                        HelpMenu();
                }
            }

            if (args[0].equals("--get-refresh-token")) {
                User_Access_Token userAccessToken = new User_Access_Token();
                while (true) {
                    if (spotifySession.getCode() == null || spotifySession.getCode().isEmpty()) {
                        spotifySession.setCode(args[1].trim());
                    }
                    if (spotifySession.getRedirect_uri() == null || spotifySession.getRedirect_uri().isEmpty()) {
                        spotifySession.setRedirect_uri(args[2].trim());
                        SaveConfig(fileUtil, spotifySession, programOptions);
                    } else {
                        break;
                    }
                }
                userAccessToken.get_Refresh_Token();
            } else {
                HelpMenu();
            }
        } else {
            HelpMenu();
        }
    }
}