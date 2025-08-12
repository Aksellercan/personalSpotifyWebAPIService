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
     *
     * @throws IOException if the file cannot be created
     */
    private void checkExist() throws IOException {
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
     * Returns the mapped HashMap
     *
     * @return Hashmap with read values
     */
    public HashMap<String, String> getConfigMap() {
        return configMap;
    }

    /**
     * Override read HashMap config with new values
     * @param key   HashMap key
     */
    private void UpdateConfig(String key) {
        ProgramOptions programOptions = ProgramOptions.getInstance();
        SpotifySession spotifySession = SpotifySession.getInstance();
        switch (key) {
            case "client_id":
                configMap.put(key, spotifySession.getClient_id());
                break;
            case "client_secret":
                configMap.put(key, spotifySession.getClient_secret());
                break;
            case "refresh_token":
                configMap.put(key, spotifySession.getRefresh_token());
                break;
            case "user_id":
                configMap.put(key, spotifySession.getUser_id());
                break;
            case "redirect_uri":
                configMap.put(key, spotifySession.getRedirect_uri());
                break;
            case "playlist_id":
                configMap.put(key, spotifySession.getPlaylist_id());
                break;
            case "output_debug":
                configMap.put(key, String.valueOf(Logger.getDebugOutput()));
                break;
            case "verbose_log_file":
                configMap.put(key, String.valueOf(Logger.getVerboseLogFile()));
                break;
            case "launch_gui":
                configMap.put(key, String.valueOf(programOptions.LAUNCH_GUI()));
                break;
            case "auto_mode":
                configMap.put(key, String.valueOf(programOptions.isAutoMode()));
                break;
        }
    }

    /**
     * Reads the config file. Uses Hashmap to map values String with String and also uses Arraylist to preserve comments in config file with any line starting with '#' or "//"
     */
    public void readConfig() {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(configFile))) {
            checkExist();
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    comments.add(line);
                    continue;
                }
                splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    String key = splitLine[0].trim();
                    String value = splitLine[1].trim();
                    configMap.put(key, value);
                } else {
                    Logger.ERROR.LogSilently("Invalid config line: " + line + ", expected format: key=value. Continue reading the file.");
                }
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "readConfig()");
        }
    }

    /**
     * Writes the config file with values provided and comments
     *
     * @param option String array in pairs
     */
    public void writeConfig(String... option) {
        if (option.length % 2 != 0) {
            Logger.ERROR.Log("Invalid number of arguments for writeConfig(String... option)", false);
        }
        try {
            checkExist();
            try (FileWriter writer = new FileWriter(configFile, false)) {
                if (!comments.isEmpty()) {
                    for (String comment : comments) {
                        writer.write(comment + "\n");
                    }
                }
                for (int i = 0; i < option.length; i += 2) {
                    if (option[i + 1] == null || option[i + 1].isEmpty()) {
                        Logger.ERROR.LogSilently("Invalid value for key: " + option[i]);
                    } else {
                        String key = option[i].trim();
                        String value = option[i + 1].trim();
                        writer.write(key + ": " + value + "\n");
                    }
                }
            }
        } catch (IOException e) {
            Logger.ERROR.LogException(e, "writeConfig(String line)", false);
        }
    }

    /**
     * Simpler Config writer. Overrides current config file when the changes are not saved
     */
    public void WriteConfig() {
        try {
            checkExist();
            try (FileWriter fileWriter = new FileWriter(configFile, false)) {
                if (!comments.isEmpty()) {
                    for (String comment : comments) {
                        fileWriter.write(comment + "\n");
                    }
                }
                for (String key : configMap.keySet()) {
                    if (ProgramOptions.getInstance().isChangesSaved()) {
                        Logger.INFO.Log("Nothing to commit", false);
                        return;
                    }
                    UpdateConfig(key);
                    fileWriter.write(key + ": " + configMap.get(key) + "\n");
                }
                ProgramOptions.getInstance().setChangesSaved(true);
            }
        } catch (IOException e) {
            Logger.ERROR.LogExceptionSilently(e, "writeConfig()");
        }
    }
}