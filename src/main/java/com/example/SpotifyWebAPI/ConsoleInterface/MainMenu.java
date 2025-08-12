package com.example.SpotifyWebAPI.ConsoleInterface;
import com.example.SpotifyWebAPI.Main;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import java.util.Scanner;

/**
 * CLI Interface Main Menu
 */
public class MainMenu extends HelperFunctions {
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final FileUtil fileUtil = Main.fileUtil;

    /**
     * Main menu
     */
    public void userInterface() {
        checkClientCredentials();
        while (true) {
            clearScreen();
            System.out.println("Spotify Web API CLI Interface");
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Settings");
            System.out.println("4. Save Config");
            System.out.println("0. Exit the program" + (programOptions.isChangesSaved() ? "" : " - Changes not saved"));
            switch (scanner.nextLine()) {
                case "1":
                    BasicAuthMenu basicAuthMenu = new BasicAuthMenu();
                    basicAuthMenu.Basic_auth_Functions();
                    break;
                case "2":
                    UserRequestsMenu userRequestsMenu = new UserRequestsMenu();
                    userRequestsMenu.Oauth2_Functions();
                    break;
                case "3":
                    SettingsMenu settingsMenu = new SettingsMenu();
                    settingsMenu.Menu();
                    break;
                case "4":
                    System.out.println("Saving Config...");
                    fileUtil.WriteConfig();
                    break;
                case "0":
                    System.out.println("Exiting the program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }
}
