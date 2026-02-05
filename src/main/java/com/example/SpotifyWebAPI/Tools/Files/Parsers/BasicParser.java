package com.example.SpotifyWebAPI.Tools.Files.Parsers;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Files.Configuration;
import com.example.SpotifyWebAPI.Tools.Files.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BasicParser extends Configuration implements Parsers {
    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            BasicParser basicParser = new BasicParser();
            Logger.INFO.Log("Using Basic Reader, with no token type checker");
            tokenConfig = basicParser.LoadKeys();
            basicParser.ReadConfig();
            basicParser.MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            BasicParser basicParser = new BasicParser();
            basicParser.MapKeys(true);
            basicParser.WriteConfig();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Reads config file
     */
    public void ReadConfig() {
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
                    Logger.WARN.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", expected format: \"key=value\". Continue reading the file.");
                }
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "Basic Parser");
        }
    }

    /**
     * Writes settings to configuration file. Skips if changes are already saved.
     */
    public void WriteConfig() {
        try (FileWriter fw = new FileWriter(MkDirs("config.txt"), false)) {
            for (Token current : tokenConfig) {
                Logger.DEBUG.Log("Writing key: value " + current.getKey() + "=" + (current.isSensitiveInfo() ? "<SensitiveInfo>" : current.getValue()));
                if (current.getKey().equals("logger_check_every")) {
                    fw.write(current.getKey() + "=" + current.getValue() + "ms\n");
                    continue;
                }
                fw.write(current.getKey() + "=" + current.getValue() + "\n");
            }
            ProgramOptions.setChangesSaved(true);
            Logger.INFO.Log("Saved changes.");
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to write configuration");
        }
    }
}
