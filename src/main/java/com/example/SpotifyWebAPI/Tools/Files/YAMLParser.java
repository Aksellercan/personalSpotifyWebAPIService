package com.example.SpotifyWebAPI.Tools.Files;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.*;

/**
 * YAMLParser inherits Configuration abstract class
 */
public final class YAMLParser extends Configuration {

    /**
     * Private Constructor
     */
    private YAMLParser() {}

    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            tokenConfig = LoadKeys();
            ReadConfig();
            MapKeys(tokenConfig.length == 0);
            Logger.DEBUG.Log("Using YAML Reader, with no token type checker");
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            MapKeys(true);
            WriteConfig();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Migrates configuration from "config.txt" to "config.yaml"
     */
    public static void MigrateToYAML() {
        Logger.INFO.Log("Setting token array size...", false);
        tokenConfig = LoadKeys();
        Logger.INFO.Log("Reading config...", false);
        OldConfigReader();
        ProgramOptions.setChangesSaved(false);
        Logger.INFO.Log("Config read. Now writing it as YAML", false);
        WriteConfig();
        Logger.INFO.Log("Wrote config as YAML", false);
    }

    /**
     * Reads the configuration file and counts lines. This is used to dynamically scale tokenConfig array
     * @param filename  Name of file to get length
     * @return  Line count, if file is empty or not present then returns 0
     */
    private static int getFileLength(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs(filename)))) {
            int fileLength = 0;
            while (br.readLine() != null) {
                fileLength++;
            }
            br.close();
            return fileLength;
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to estimate file length");
            return 0;
        }
    }

    /**
     * Writes settings to configuration file. Skips if changes are already saved.
     */
    private static void WriteConfig() {
        Logger.INFO.Log("Changes are " + (ProgramOptions.isChangesSaved() ? "already saved." : "not saved yet."));
        if (!ProgramOptions.isChangesSaved()) {
            try (FileWriter fw = new FileWriter(MkDirs("config.yaml"), false)) {
                for (Token current : tokenConfig) {
                    Logger.DEBUG.Log("Writing key: value " + current.getKey() + ": " + current.getValue());
                    fw.write(current.getKey() + ": " + current.getValue() + "\n");
                }
                ProgramOptions.setChangesSaved(true);
                Logger.INFO.Log("Saved changes.");
            } catch (Exception e) {
                Logger.CRITICAL.LogException(e, "Failed to write configuration");
            }
        }
    }

    /**
     * Reads configuration file and saves it to tokenConfig array
     */
    private static void ReadConfig() {
        int lineCount = 0;
        int tokenCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs("config.yaml")))) {
            String line;
            String[] splitLine;
            while ((line = br.readLine()) != null) {
                lineCount++;
                splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    tokenConfig[tokenCount] = new Token(splitLine[0].trim(), splitLine[1].trim());
                    Logger.DEBUG.Log(tokenConfig[tokenCount].toString());
                    tokenCount++;
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineCount + ": \"" + line + "\", expected format: \"key: value\". Continue reading the file.");
                }
            }
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to read configuration");
        }
    }

    /**
     * Reads the old config
     */
    private static void OldConfigReader() {
        int lineNumber = 0;
        int tokenNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(MkDirs("config.txt")))) {
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                splitLine = line.split("=", 2);
                if (splitLine.length == 2) {
                    tokenConfig[tokenNumber] = new Token(splitLine[0].trim(), splitLine[1].trim());
                    tokenNumber++;
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", expected format: \"key=value\". Continue reading the file.");
                }
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "OldConfigReader()");
        }
    }
}
