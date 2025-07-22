package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import java.util.Scanner;

public class MainMenu {
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private HelperFunctions helperFunctions;
    private final FileUtil fileUtil;

    public MainMenu (FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void userInterface() {
        Scanner scanner = new Scanner(System.in);
        helperFunctions = new HelperFunctions(scanner);
        helperFunctions.checkClientCredentials();
        helperFunctions.setFileUtil(fileUtil);
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("Spotify Web API CLI Interface");
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Set Http Debug Output" + (programOptions.isDebugMode() ? " - Debug Output Enabled" : ""));
            System.out.println("4. Set Auto Mode" + (programOptions.isAutoMode() ? " - Auto Mode Enabled, Program won't launch to CLI on next run" : ""));
            System.out.println("5. Save Config");
            System.out.println("0. Exit the program" + (programOptions.isChangesSaved() ? "" : " - Changes not saved"));
            switch (scanner.nextLine()) {
                case "1":
                    BasicAuthMenu basicAuthMenu = new BasicAuthMenu(scanner);
                    basicAuthMenu.Basic_auth_Functions();
                    break;
                case "2":
                    UserRequestsMenu userRequestsMenu = new UserRequestsMenu(scanner, fileUtil);
                    userRequestsMenu.Oauth2_Functions();
                    break;
                case "3":
                    HTTPConnection  httpConnection = HTTPConnection.getInstance();
                    System.out.println("Set Http Debug Output:\nCurrent State is " + programOptions.isDebugMode() +
                            "\nPress y to enable debug output\nPress n to disable debug output");
                    if (scanner.nextLine().equals("y")) {
                        if (!programOptions.isDebugMode()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setDebugOutput(true);
                        programOptions.setDebugMode(true);
                        System.out.println("Http Debug Output set to true");
                    } else {
                        Logger.setDebugOutput(false);
                        programOptions.setDebugMode(false);
                        System.out.println("Http Debug Output set to false");
                        programOptions.setChangesSaved(false);
                    }
                    break;
                case "4":
                    System.out.println("Set Auto Mode:\nCurrent State is " + programOptions.isAutoMode() +
                            "\nPress y to enable Auto Mode\nPress n to disable Auto Mode");
                    if (scanner.nextLine().equals("y")) {
                        programOptions.setAutoMode(true);
                        Logger.INFO.Log("Auto Mode set to true");
                        helperFunctions.setupAutoMode();
                    } else {
                        programOptions.setAutoMode(false);
                        System.out.println("Auto Mode set to false");
                    }
                    break;
                case "5":
                    System.out.println("Saving Config...");
                    helperFunctions.saveConfig();
                    break;
                case "0":
                    System.out.println("Exiting the program...");
                    return;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }
}
