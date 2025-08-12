package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import java.util.Scanner;

/**
 * CLI Interface Main Menu
 */
public class MainMenu {
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final FileUtil fileUtil;

    public MainMenu (FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    /**
     * Main menu
     */
    public void userInterface() {
        Scanner scanner = new Scanner(System.in);
        HelperFunctions helperFunctions = new HelperFunctions(scanner);
        helperFunctions.checkClientCredentials();
        helperFunctions.setFileUtil(fileUtil);
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("Spotify Web API CLI Interface");
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Settings");
            System.out.println("4. Save Config");
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
                    SettingsMenu settingsMenu = new SettingsMenu(scanner, helperFunctions);
                    settingsMenu.Menu();
                    break;
                case "4":
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
