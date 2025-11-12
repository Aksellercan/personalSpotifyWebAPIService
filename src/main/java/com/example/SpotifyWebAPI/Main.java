package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.System.TimerWorker;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Views.GUI;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Run_Modes.AutoMode;
import com.example.SpotifyWebAPI.Tools.Logger.ConsoleColours;
import com.example.SpotifyWebAPI.Run_Modes.ConsoleInterface.*;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.YAMLParser;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.Tools.Logger.LoggerBackend;
import com.example.SpotifyWebAPI.Tools.Logger.LoggerSettings;

import java.util.Random;

/**
 * Main Class and starting point
 */
public class Main {
    /**
     * Checks if all values for Auto Mode are set
     *
     * @param spotifySession session object
     */
    private static void AutoModeRequirementCheck(SpotifySession spotifySession) {
        System.out.println("Auto Mode Requirement Check");
        System.out.println((spotifySession.getUser_id().isEmpty() ? ConsoleColours.RED + "User ID not set!" + ConsoleColours.RESET
                : "Set User ID: " + spotifySession.getUser_id()));
        System.out.println((spotifySession.getClient_id().isEmpty() ? ConsoleColours.RED + "Client ID not set!" + ConsoleColours.RESET
                : "Set Client ID: " + spotifySession.getClient_id()));
        System.out.println((spotifySession.getClient_secret().isEmpty() ? ConsoleColours.RED + "Client Secret not set!" + ConsoleColours.RESET
                : "Set Client Secret: " + spotifySession.getClient_secret()));
        System.out.println((spotifySession.getRedirect_uri().isEmpty() ? ConsoleColours.RED + "Redirect URI not set!" + ConsoleColours.RESET
                : "Set Redirect URI: " + spotifySession.getRedirect_uri()));
        System.out.println((spotifySession.getRefresh_token().isEmpty() ? ConsoleColours.RED + "Refresh Token not set!" + ConsoleColours.RESET
                : "Set Refresh Token: " + spotifySession.getRefresh_token()));
        System.out.println((spotifySession.getPlaylist_id().isEmpty()) ? ConsoleColours.RED + "Playlist ID not set!" + ConsoleColours.RESET
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
        System.out.println("  --migrate                                     Migrates configuration from \"config.txt\" to \"config.yaml\"");
        System.out.println("\nUsage: program set [CONFIGURATION] <value> [CONFIGURATION] <value>...");
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

    private static SpotifySession Launch(boolean concurrent) {
        if (concurrent) {
            Random rand = new Random();
            long id = rand.nextLong();
            Thread configReaderThread = new Thread(YAMLParser::ReadConfigAndMap);
//            Thread configReaderThread = new Thread(JSONParser::ReadConfigAndMap);
            configReaderThread.setName(configReaderThread.getName() + "_config-reader_" + (id > 0 ? id : -1*id));
            configReaderThread.start();
        } else {
            YAMLParser.ReadConfigAndMap();
        }
        return SpotifySession.getInstance();
    }

    /**
     * Main Method.
     * Takes commandline arguments.
     * If no arguments are given, reads from configuration to either launch into CLI, Auto Mode or JavaFX GUI.
     * If there is no configuration file or the values not set by default launches into JavaFX GUI.
     *
     * @param args Commandline arguments, allows maximum of 14 with "set" argument and lowest no arguments.
     */
    public static void main(String[] args) {
        Thread loggerThread = new Thread(new LoggerBackend());
        loggerThread.setDaemon(true);
        loggerThread.start();
        Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Main thread: " + Thread.currentThread().getName());
        SpotifySession spotifySession = null;
        if (args.length == 1) {
            if (args[0].equals("--help")) {
                HelpMenu();
                return;
            }
            if (args[0].equals("--settings")) {
                LoggerSettings.setQuiet(true);
            }
        } else {
            spotifySession = Launch(true);
        }
        if (args.length == 0) {
            if (ProgramOptions.LAUNCH_GUI()) {
                GUI.launch(GUI.class, args);
                return;
            }
            if (ProgramOptions.isAutoMode()) {
                spotifySession = Launch(false);
                AutoMode autoMode = new AutoMode();
                autoMode.runFunctions();
            } else {
                MainMenu mainMenu = new MainMenu();
                mainMenu.userInterface();
            }
            return;
        }
        switch (args[0]) {
            //TODO use sequential config reader to fix race condition
            case "--settings":
                //Settings
                System.out.println("launch_gui: " + ProgramOptions.LAUNCH_GUI());
                System.out.println("auto_mode: " + ProgramOptions.isAutoMode());
                System.out.println("verbose_log_file: " + LoggerSettings.getVerboseLogFile());
                System.out.println("debug_output: " + LoggerSettings.getDebugOutput());
                System.out.println("coloured_output: " + LoggerSettings.getColouredOutput());
                System.out.println("enable_stack_traces: " + LoggerSettings.getEnableStackTraces());
                System.out.println("playlist_limit: " + ProgramOptions.getPlaylist_limit());
                return;
            case "--gui":
                try {
                    spotifySession = Launch(true);
                    Thread timer = new Thread(new TimerWorker());
                    timer.start();
                    GUI.launch(GUI.class, args);
                    timer.join();
                } catch (InterruptedException e) {
                    Logger.THREAD_CRITICAL.LogThreadException(Thread.currentThread(), e, "Timer thread couldn't be started");
                }
                return;
            case "--req":
                AutoModeRequirementCheck(spotifySession);
                return;
            case "--auto-mode":
                spotifySession = Launch(false);
                if (spotifySession.getPlaylist_id() == null || spotifySession.getRefresh_token() == null
                        || spotifySession.getUser_id() == null || spotifySession.getClient_id() == null
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
            case "--migrate":
                YAMLParser.MigrateToYAML();
                return;
            case "set":
                if (args.length < 14) {
                    for (String str : args) {
                        if (str == null || str.isEmpty()) {
                            System.out.println("Empty value");
                            return;
                        }
                    }
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].equals("--playlist-id")) {
                            spotifySession.setPlaylist_id(args[i + 1].trim());
                        }
                        if (args[i].equals("--user-id")) {
                            spotifySession.setUser_id(args[i + 1].trim());
                        }
                        if (args[i].equals("--client-id")) {
                            spotifySession.setClient_id(args[i + 1].trim());
                        }
                        if (args[i].equals("--client-secret")) {
                            spotifySession.setClient_secret(args[i + 1].trim());
                        }
                        if (args[i].equals("--redirect-uri")) {
                            spotifySession.setRedirect_uri(args[i + 1].trim());
                        }
                        if (args[i].equals("--refresh-token")) {
                            spotifySession.setRefresh_token(args[i + 1].trim());
                        }
                    }
                    ProgramOptions.setChangesSaved(false);
                    YAMLParser.MapAndWriteConfig();
                } else {
                    HelpMenu();
                }
                break;
            case "--get-refresh-token":
                if (args.length == 3) {
                    if (!args[1].trim().isEmpty() || !args[2].trim().isEmpty()) {
                        User_Access_Token userAccessToken = new User_Access_Token();
                        spotifySession.setCode(args[1].trim());
                        spotifySession.setRedirect_uri(args[2].trim());
                        if (userAccessToken.get_Refresh_Token()) {
                            ProgramOptions.setChangesSaved(false);
                            YAMLParser.MapAndWriteConfig();
                        } else {
                            System.out.println("Invalid credentials");
                        }
                    }
                } else {
                    System.out.println("Invalid Usage\n\t--get-refresh-token <code> <redirect uri>     get refresh token");
                }
                break;
            default:
                System.out.println(ConsoleColours.RED + "Unknown argument: " + args[0] + ConsoleColours.RESET);
                HelpMenu();
        }
    }
}