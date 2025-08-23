package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.JavaFXInterface.GUI;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Run_Modes.AutoMode;
import com.example.SpotifyWebAPI.Run_Modes.CLI_Interface;
import com.example.SpotifyWebAPI.Tools.ConsoleColours;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.ConsoleInterface.*;
import com.example.SpotifyWebAPI.Tools.Logger;

/**
 * Main Class and starting point
 */
public class Main {
    /**
     * Checks if all values for Auto Mode are set
     * @param spotifySession    session object
     */
    private static void AutoModeRequirementCheck(SpotifySession spotifySession) {
        System.out.println("Auto Mode Requirement Check");
        System.out.println((spotifySession.getUser_id() == null ? ConsoleColours.RED + "User ID not set!" + ConsoleColours.RESET
                : "Set User ID: " + spotifySession.getUser_id()));
        System.out.println((spotifySession.getClient_id() == null ? ConsoleColours.RED + "Client ID not set!" + ConsoleColours.RESET
                : "Set Client ID: " + spotifySession.getClient_id()));
        System.out.println((spotifySession.getClient_secret() == null ? ConsoleColours.RED + "Client Secret not set!" + ConsoleColours.RESET
                : "Set Client Secret: " + spotifySession.getClient_secret()));
        System.out.println((spotifySession.getRedirect_uri() == null ? ConsoleColours.RED + "Redirect URI not set!" + ConsoleColours.RESET
                : "Set Redirect URI: " + spotifySession.getRedirect_uri()));
        System.out.println((spotifySession.getRefresh_token() == null ? ConsoleColours.RED + "Refresh Token not set!" + ConsoleColours.RESET
                : "Set Refresh Token: " + spotifySession.getRefresh_token()));
        System.out.println((spotifySession.getPlaylist_id() == null) ? ConsoleColours.RED + "Playlist ID not set!" + ConsoleColours.RESET
                : "Set Playlist ID: " + spotifySession.getPlaylist_id());
    }

    /**
     * Prints Help Menu on console
     */
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

    /**
     * Main Method.
     * Takes commandline arguments.
     * If no arguments are given, reads from configuration to either launch into CLI, Auto Mode or JavaFX GUI.
     * If there is no configuration file or the values not set by default launches into JavaFX GUI.
     * @param args  Commandline arguments, allows maximum of 3 arguments and lowest no arguments.
     */
    public static void main(String[] args) {
        /*
        Set what keys should be in config file
         */
        String[] Credentials = {
                "client_id",
                "client_secret",
                "redirect_uri",
                "refresh_token",
                "playlist_id",
                "output_debug",
                "auto_mode",
                "user_id",
                "launch_gui",
                "verbose_log_file",
                "coloured_output"
        };
        FileUtil.AddToConfigMap(Credentials);
        SpotifySession spotifySession = SpotifySession.getInstance();
        FileUtil.readConfig();

        //Settings
        Logger.DEBUG.Log("launch_gui: " + ProgramOptions.LAUNCH_GUI(), false);
        Logger.DEBUG.Log("auto_mode: " + ProgramOptions.isAutoMode(), false);
        Logger.DEBUG.Log("verbose_log_file: " + Logger.getVerboseLogFile(), false);
        Logger.DEBUG.Log("debug_output: " + Logger.getDebugOutput(), false);
        Logger.DEBUG.Log("coloured_output: " + Logger.getColouredOutput(), false);

        if (args.length == 0) {
            if (ProgramOptions.LAUNCH_GUI()) {
                GUI.launch(GUI.class, args);
                return;
            }
            if (ProgramOptions.isAutoMode()) {
                AutoMode autoMode = new AutoMode();
                autoMode.runFunctions();
            } else {
                MainMenu mainMenu = new MainMenu();
                mainMenu.userInterface();
            }
            return;
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "--gui":
                    GUI.launch(GUI.class, args);
                    return;
                case "--req":
                    AutoModeRequirementCheck(spotifySession);
                    return;
                case "--auto-mode":
                    if (spotifySession.getPlaylist_id() == null || spotifySession.getRefresh_token() == null
                            || spotifySession.getUser_id() == null ||  spotifySession.getClient_id() == null
                            || spotifySession.getClient_secret() == null || spotifySession.getRedirect_uri() == null) {
                        System.out.println("Required parameters not set!");
                        return;
                    }
                    AutoMode autoMode = new AutoMode();
                    autoMode.runFunctions();
                    return;
                case "--cli":
                    MainMenu mainMenu = new MainMenu();
                    mainMenu.userInterface();
                    return;
                case "--cli-test":
                    CLI_Interface cli = new CLI_Interface();
                    cli.initSession();
                    return;
                case "--help":
                    HelpMenu();
                    return;
                case "--migrate":
                    FileUtil.MigrateToYAML();
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
                        spotifySession.setPlaylist_id(args[2].trim());
                    case "--userid":
                        spotifySession.setUser_id(args[2].trim());
                    case "--client-id":
                        spotifySession.setClient_id(args[2].trim());
                    case "--client-secret":
                        spotifySession.setClient_secret(args[2].trim());
                    case "--redirect-uri":
                        spotifySession.setRedirect_uri(args[2].trim());
                    case "--refresh-token":
                        spotifySession.setRefresh_token(args[2].trim());
                    default:
                        HelpMenu();
                }
                FileUtil.WriteConfig();
            }

            if (args[0].equals("--get-refresh-token")) {
                User_Access_Token userAccessToken = new User_Access_Token();
                while (true) {
                    if (spotifySession.getCode() == null || spotifySession.getCode().isEmpty()) {
                        spotifySession.setCode(args[1].trim());
                    }
                    if (spotifySession.getRedirect_uri() == null || spotifySession.getRedirect_uri().isEmpty()) {
                        spotifySession.setRedirect_uri(args[2].trim());
                        FileUtil.WriteConfig();
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