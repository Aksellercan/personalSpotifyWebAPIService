package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Tools.ConsoleColours;
import com.example.SpotifyWebAPI.Tools.Logger;

/**
 * CLI Interface Settings Menu
 */
public class SettingsMenu extends HelperFunctions {

    /**
     * Settings menu
     */
    public void Menu() {
        while (true) {
            clearScreen();
            System.out.println("Settings ");
            System.out.println("1. Set Http Debug Output" +
                    (Logger.getDebugOutput() ? ConsoleColours.GREEN + " - Debug Output Enabled" + ConsoleColours.RESET : ""));
            System.out.println("2. Include Debugs in log file (More Verbose)" +
                    (Logger.getVerboseLogFile() ? ConsoleColours.GREEN + " - Debugs will be included in log file" + ConsoleColours.RESET : ""));
            System.out.println("3. Set Coloured Logger Output" +
                    (Logger.getColouredOutput() ? ConsoleColours.GREEN + " - Logger will output ANSI Coloured lines" + ConsoleColours.RESET: ""));
            System.out.println("4. Set Auto Mode" +
                    (programOptions.isAutoMode() ? ConsoleColours.GREEN + " - Auto Mode Enabled, Program won't launch to CLI on next run" + ConsoleColours.RESET: ""));
            System.out.println("5. Set GUI Mode" +
                    (programOptions.LAUNCH_GUI() ? ConsoleColours.GREEN + " - GUI Enabled" + ConsoleColours.RESET : ""));
            System.out.println("6. Migrate Config file to YAML");
            System.out.println("7. Save Config" +
                    (programOptions.isChangesSaved() ? "" : ConsoleColours.RED + " - Changes not saved" + ConsoleColours.RESET));
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
                        if (Logger.getDebugOutput()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setDebugOutput(false);
                        System.out.println("Http Debug Output set to false");
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
                        if (Logger.getVerboseLogFile()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setVerboseLogFile(false);
                        System.out.println("Debugs won't included in log file");
                    }
                    break;
                case "3":
                    System.out.println((Logger.getColouredOutput() ? "Logger will output ANSI coloured lines" : "Logger will not output ANSI coloured lines") +
                            "\nPress y to enable\nPress n to disable");
                    if (scanner.nextLine().equals("y")) {
                        if (!Logger.getColouredOutput()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setColouredOutput(true);
                        System.out.println("Logger will output ANSI coloured lines");
                    } else {
                        if (Logger.getColouredOutput()) {
                            programOptions.setChangesSaved(false);
                        }
                        Logger.setColouredOutput(false);
                        System.out.println("Logger will not output ANSI coloured lines");
                    }
                    break;
                case "4":
                    System.out.println("Set Auto Mode:\nCurrent State is " + programOptions.isAutoMode() +
                            "\nPress y to enable Auto Mode\nPress n to disable Auto Mode");
                    if (scanner.nextLine().equals("y")) {
                        programOptions.setAutoMode(true);
                        System.out.println("Auto Mode set to true");
                        setupAutoMode();
                    } else {
                        if (programOptions.isAutoMode()) {
                            programOptions.setChangesSaved(false);
                        }
                        programOptions.setAutoMode(false);
                        System.out.println("Auto Mode set to false");
                    }
                    break;
                case "5":
                    System.out.println("Set GUI Mode:\nCurrent State is " + programOptions.LAUNCH_GUI() +
                            "\nPress y to enable GUI\nPress n to disable GUI");
                    if (scanner.nextLine().equals("y")) {
                        if (!programOptions.LAUNCH_GUI()) {
                            programOptions.setChangesSaved(false);
                        }
                        programOptions.setLAUNCH_GUI(true);
                        System.out.println("GUI set to true");
                    } else {
                        if (programOptions.LAUNCH_GUI()) {
                            programOptions.setChangesSaved(false);
                        }
                        programOptions.setLAUNCH_GUI(false);
                        System.out.println("GUI set to false");
                    }
                    break;
                case "6":
                    fileUtil.MigrateToYAML();
                    break;
                case "7":
                    System.out.println(ConsoleColours.YELLOW + "Saving Config..." + ConsoleColours.RESET);
                    getFileUtil().WriteConfig();
                    break;
                case "0":
                    return;
                default:
                    System.out.println(ConsoleColours.RED + "Invalid input" + ConsoleColours.RESET);
                    break;
            }
        }
    }
}
