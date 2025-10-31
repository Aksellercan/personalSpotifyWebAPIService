package com.example.SpotifyWebAPI.Tools.Files.Parsers;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Files.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Files.Configuration;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

import java.io.*;

/**
 * YAMLParser inherits Configuration abstract class
 */
public class YAMLParser extends Configuration implements Parsers {
    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            YAMLParser yamlParser = new YAMLParser();
            Logger.INFO.Log("Using YAML Reader, with no token type checker");
            Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Reading config on thread: " + Thread.currentThread().getName());
            tokenConfig = yamlParser.LoadKeys();
            yamlParser.ReadConfig();
            yamlParser.MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            YAMLParser yamlParser = new YAMLParser();
            yamlParser.MapKeys(true);
            yamlParser.WriteConfig();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Writes settings to configuration file. Skips if changes are already saved.
     */
    public void WriteConfig() {
        if (!ProgramOptions.isChangesSaved()) {
            Logger.WARN.Log("Changes are not saved yet.");
            String lastCategory = "";
            int count = 0;
            try (FileWriter fw = new FileWriter(MkDirs("config.yaml"), false)) {
                for (Token current : tokenConfig) {
                    if (!current.getCategoryType().isEmpty()) {
                        if (!lastCategory.equals(current.getCategoryType())) {
                            lastCategory = current.getCategoryType();
                            fw.write((count > 0 ? "\n" : "") + "# " + current.getCategoryType() + "\n");
                            count++;
                        }
                    }
                    Logger.DEBUG.Log("Writing key: value " + current.getKey() + ": " + (current.isSensitiveInfo() ? "<SensitiveInfo>" : current.getValue()));
                    fw.write(current.getKey() + ": " + current.getValue() + "\n");
                }
                ProgramOptions.setChangesSaved(true);
                Logger.INFO.Log("Saved changes.");
            } catch (Exception e) {
                Logger.CRITICAL.LogException(e, "Failed to write configuration");
            }
        } else {
            Logger.INFO.Log("Changes are already saved.");
        }
    }

    /**
     * Reads configuration file and saves it to tokenConfig array
     */
    //TODO remove quotes at start and beginning of a string
    //TODO read arrays name:[]
    /*
    instead of line by line formatting just like JSON reader put in a string but have 2 strings one for normal key:value other for arrays
    so return String[] then format as follows name_entry: ["red", "blue", "green"]...
     */
    public void ReadConfig() {
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs("config.yaml")))) {
            String line;
            String[] splitLine;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    FindAndSetToken(splitLine[0].trim(), splitLine[1].trim());
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineCount + ": \"" + line + "\", expected format: \"key: value\". Continue reading the file.");
                }
            }
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to read configuration");
        }
    }
}
