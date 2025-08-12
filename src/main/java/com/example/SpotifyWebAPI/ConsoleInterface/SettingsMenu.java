package com.example.SpotifyWebAPI.ConsoleInterface;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Logger;

import java.util.Scanner;
/**
 * CLI Interface Settings Menu
 */
public class SettingsMenu {
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final Scanner scanner;
    private final HelperFunctions helperFunctions;

    public SettingsMenu(Scanner scanner, HelperFunctions helperFunctions) {
        this.scanner = scanner;
        this.helperFunctions = helperFunctions;
    }

    /**
     * Settings menu
     */
    public void Menu() {
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("Settings ");
            System.out.println("1. Set Http Debug Output" + (Logger.getDebugOutput() ? " - Debug Output Enabled" : ""));
            System.out.println("2. Include Debugs in log file (More Verbose)" + (Logger.getVerboseLogFile() ? " - Debugs will be included in log file" : ""));
            System.out.println("3. Set Auto Mode" + (programOptions.isAutoMode() ? " - Auto Mode Enabled, Program won't launch to CLI on next run" : ""));
            System.out.println("4. Set GUI Mode" + (programOptions.LAUNCH_GUI() ? " - GUI Enabled" : ""));
            System.out.println("5. Save Config" + (programOptions.isChangesSaved() ? "" : " - Changes not saved"));
            System.out.println("0. Go Back");
            switch (scanner.nextLine()) {
                case "1":
                    System.out.println("Set Http Debug Output:\nCurrent State is " + Logger.getDebugOutput() +
                            "\nPress y to enable debug output\nPress n to disable debug output");
                    if (scanner.nextLine().equals("y")) {
                        if (!Logger.getDebugOutput()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setDebugOutput(true);
                        System.out.println("Http Debug Output set to true");
                    } else {
                        Logger.setDebugOutput(false);
                        System.out.println("Http Debug Output set to false");
                        programOptions.setChangesSaved(false);
                    }
                    break;
                case "2":
                    System.out.println((Logger.getVerboseLogFile() ? "Debugs will be included in log file" : "Debugs won't be included in log file") +
                            "\nPress y to include them\nPress n to not include them");
                    if (scanner.nextLine().equals("y")) {
                        if (!Logger.getVerboseLogFile()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setVerboseLogFile(true);
                        System.out.println("Debugs will be included in log file");
                    } else {
                        Logger.setDebugOutput(false);
                        System.out.println("Debugs won't included in log file");
                        programOptions.setChangesSaved(false);
                    }
                    break;
                case "3":
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
                case "4":
                    System.out.println("Set GUI Mode:\nCurrent State is " + programOptions.LAUNCH_GUI() +
                            "\nPress y to enable GUI\nPress n to disable GUI");
                    if (scanner.nextLine().equals("y")) {
                        if (!programOptions.LAUNCH_GUI()) {
                            programOptions.setChangesSaved(false);
                        }
                        programOptions.setLAUNCH_GUI(true);
                        Logger.INFO.Log("GUI set to true");
                    } else {
                        programOptions.setChangesSaved(false);
                        programOptions.setLAUNCH_GUI(false);
                        System.out.println("GUI set to false");
                    }
                    break;
                case "5":
                    System.out.println("Saving Config...");
                    helperFunctions.saveConfig();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }
}
