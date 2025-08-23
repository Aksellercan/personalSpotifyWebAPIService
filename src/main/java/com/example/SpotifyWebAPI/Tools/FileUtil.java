package com.example.SpotifyWebAPI.Tools;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Reads and writes configuration file in "Config/*"
 */
public class FileUtil {
    private final File configPath = new File("Config");
    private final File configFile = new File(configPath + File.separator + "config.yaml");
    private final HashMap<String, String> configMap = new HashMap<>();
    private final ArrayList<String> comments = new ArrayList<>();

    /**
     * Checks if the directory exists if not creates it
     * @throws IOException if the file cannot be created
     */
    private void checkExist(File configPath, File configFile) throws IOException {
        if (!configPath.exists()) {
            boolean createDir = configPath.mkdirs();
            if (!createDir) {
                throw new IOException("Could not create config directory");
            }
        }
        if (!configFile.exists()) {
            boolean createFile = configFile.createNewFile();
            if (!createFile) {
                throw new IOException("Could not create config file");
            }
        }
    }

    /**
     * If config files does not exist, it fills the hashmap with empty values
     * @param keys  Hashmap keys
     */
    public void AddToConfigMap(String... keys) {
        if (!configMap.isEmpty()) {
            return;
        }
        for (String key : keys) {
            configMap.put(key, "");
        }
    }

    /**
     * Returns the mapped HashMap
     * @return Hashmap with read values
     */
    public HashMap<String, String> getConfigMap() {
        return configMap;
    }

    /**
     * Parses booleans from String. If not recognized will return "returnValue"
     * @param value   configMap.get(key)
     * @param returnValue Return value of parse
     * @return  True is value is null or invalid, otherwise sets value according to file
     */
    private boolean BooleanParse(String value, boolean returnValue) {
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        if (!value.isEmpty()) Logger.ERROR.LogSilently("Key value \"" + value +"\" is not valid. Expected \"true\" or \"false\".");
        return returnValue;
    }

    /**
     * Migrates configuration from "config.txt" to "config.yaml"
     */
    public void MigrateToYAML() {
        Logger.INFO.Log("Reading config...", false);
        OldConfigReader();
        ProgramOptions.getInstance().setChangesSaved(false);
        Logger.INFO.Log("Config read. Now writing it as YAML", false);
        WriteConfig();
    }

    /**
     * Overrides in memory HashMap config with new values if "update" is true.
     * If "update" is false then it will map values in configMap with objects.
     * New settings should be added here
     * @param key   HashMap key
     */
    private void UpdateConfig(String key, boolean update) {
        ProgramOptions programOptions = ProgramOptions.getInstance();
        SpotifySession spotifySession = SpotifySession.getInstance();
        switch (key) {
            case "client_id":
                if (update)  {
                    configMap.put(key, spotifySession.getClient_id());
                    break;
                }
                spotifySession.setClient_id(configMap.get(key));
                break;
            case "client_secret":
                if (update) {
                    configMap.put(key, spotifySession.getClient_secret());
                    break;
                }
                spotifySession.setClient_secret(configMap.get(key));
                break;
            case "refresh_token":
                if (update) {
                    configMap.put(key, spotifySession.getRefresh_token());
                    break;
                }
                spotifySession.setRefresh_token(configMap.get(key));
                break;
            case "user_id":
                if (update) {
                    configMap.put(key, spotifySession.getUser_id());
                    break;
                }
                spotifySession.setUser_id(configMap.get(key));
                break;
            case "redirect_uri":
                if (update) {
                    configMap.put(key, spotifySession.getRedirect_uri());
                    break;
                }
                spotifySession.setRedirect_uri(configMap.get(key));
                break;
            case "playlist_id":
                if (update) {
                    configMap.put(key, spotifySession.getPlaylist_id());
                    break;
                }
                spotifySession.setPlaylist_id(configMap.get(key));
                break;
            case "output_debug":
                if (update) {
                    configMap.put(key, String.valueOf(Logger.getDebugOutput()));
                    break;
                }
                Logger.setDebugOutput(BooleanParse(configMap.get(key), false));
                break;
            case "verbose_log_file":
                if (update) {
                    configMap.put(key, String.valueOf(Logger.getVerboseLogFile()));
                    break;
                }
                Logger.setVerboseLogFile(BooleanParse(configMap.get(key), false));
                break;
            case "launch_gui":
                if (update) {
                    configMap.put(key, String.valueOf(programOptions.LAUNCH_GUI()));
                    break;
                }
                programOptions.setLAUNCH_GUI(BooleanParse(configMap.get(key), true));
                break;
            case "auto_mode":
                if (update) {
                    configMap.put(key, String.valueOf(programOptions.isAutoMode()));
                    break;
                }
                programOptions.setAutoMode(BooleanParse(configMap.get(key), false));
                break;
            case "coloured_output":
                if (update) {
                    configMap.put(key, String.valueOf(Logger.getColouredOutput()));
                    break;
                }
                Logger.setColouredOutput(BooleanParse(configMap.get(key), false));
                break;
        }
    }

    /**
     * Reads the old config
     */
    private void OldConfigReader() {
        File deprecatedConfigFile = new File(configPath + File.separator + "config.txt");
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(deprecatedConfigFile))) {
            checkExist(configPath, deprecatedConfigFile);
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    if (comments.isEmpty()) {
                        comments.add(line);
                    }
                    if (!comments.contains(line)) {
                        comments.add(line);
                    }
                    lineNumber++;
                    continue;
                }
                lineNumber++;
                splitLine = line.split("=", 2);
                if (splitLine.length == 2) {
                    FormatLine(lineNumber, line, splitLine);
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", expected format: \"key=value\". Continue reading the file.");
                }
            }
            for (String key : configMap.keySet()) {
                UpdateConfig(key, false);
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "OldConfigReader()");
        }
    }

    /**
     * Reads the config file. Uses Hashmap to map values String with String and also uses Arraylist to preserve comments in config file with any line starting with '#' or "//"
     */
    public void readConfig() {
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(configFile))) {
            checkExist(configPath, configFile);
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    comments.add(line);
                    lineNumber++;
                    continue;
                }
                lineNumber++;
                splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    FormatLine(lineNumber, line, splitLine);
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", expected format: \"key: value\". Continue reading the file.");
                }
            }
            for (String key : configMap.keySet()) {
                UpdateConfig(key, false);
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "readConfig()");
        }
    }

    private void FormatLine(int lineNumber, String line, String[] splitLine) {
        String key = splitLine[0].trim();
        if (!configMap.containsKey(key)) {
            Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", Key \"" + key + "\" not found. Continue reading the file.");
            return;
        }
        String value = splitLine[1].trim();
        if (value.isEmpty()) Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ":" + " Value of key \"" + key +"\" cannot be empty.");
        configMap.put(key, value);
    }

    /**
     * @deprecated
     * Writes the config file with values provided and comments. Uses config.txt as config file
     * Using newer WriteConfig() instead
     *
     * @param option String array in pairs
     */
    public void writeConfig(String... option) {
        if (option.length % 2 != 0) {
            Logger.ERROR.Log("[ DEPRECATED ] Invalid number of arguments for writeConfig(String... option)", false);
        }
        File deprecatedConfigFile = new File(configPath + File.separator + "config.txt");
        try {
            checkExist(configPath, deprecatedConfigFile);
            try (FileWriter writer = new FileWriter(deprecatedConfigFile, false)) {
                if (!comments.isEmpty()) {
                    for (String comment : comments) {
                        writer.write(comment + "\n");
                    }
                }
                for (int i = 0; i < option.length; i += 2) {
                    if (option[i + 1] == null || option[i + 1].isEmpty()) {
                        Logger.ERROR.LogSilently("[ DEPRECATED ] Invalid value for key: " + option[i]);
                    } else {
                        String key = option[i].trim();
                        String value = option[i + 1].trim();
                        writer.write(key + "=" + value + "\n");
                    }
                }
            }
        } catch (IOException e) {
            Logger.ERROR.LogException(e, "[ DEPRECATED ] writeConfig(String line)", false);
        }
    }

    /**
     * Simpler Config writer. Overrides current config file when the changes are not saved. Uses YAML format as config file
     */
    public void WriteConfig() {
        try {
            checkExist(configPath, configFile);
            if (ProgramOptions.getInstance().isChangesSaved()) {
                Logger.INFO.Log("Nothing to save", false);
                return;
            }
            try (FileWriter fileWriter = new FileWriter(configFile, false)) {
                if (!comments.isEmpty()) {
                    for (String comment : comments) {
                        fileWriter.write(comment + "\n");
                    }
                }
                for (String key : configMap.keySet()) {
                    UpdateConfig(key, true);
                    if(configMap.get(key) == null || configMap.get(key).isEmpty()) continue;
                    Logger.DEBUG.Log("Key \"" + key + "\", Value \"" + configMap.get(key) + "\"");
                    fileWriter.write(key + ": " + configMap.get(key) + "\n");
                }
                ProgramOptions.getInstance().setChangesSaved(true);
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "writeConfig()");
        }
    }
}