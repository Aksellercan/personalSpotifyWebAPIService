package com.example.SpotifyWebAPI.Run_Modes.ConsoleInterface;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Logger.ConsoleColours;
import com.example.SpotifyWebAPI.Tools.Files.JSONParser;

/**
 * CLI Interface Main Menu
 */
public class MainMenu extends HelperFunctions {
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
            System.out.println("0. Exit the program" + (ProgramOptions.isChangesSaved() ? "" : ConsoleColours.RED + " - Changes not saved" + ConsoleColours.RESET));
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
                    System.out.println(ConsoleColours.YELLOW + "Saving Config..." + ConsoleColours.RESET);
                    JSONParser.MapAndWriteConfig();
                    break;
                case "0":
                    System.out.println(ConsoleColours.GREEN + "Exiting the program..." + ConsoleColours.RESET);
                    scanner.close();
                    return;
                default:
                    System.out.println(ConsoleColours.RED + "Invalid input" + ConsoleColours.RESET);
                    break;
            }
        }
    }
}
