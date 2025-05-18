package com.example.SpotifyWebAPI.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FileUtil {
    private final File configPath = new File("Config");
    private final File configFile = new File(configPath + File.separator + "config.txt");;
    private final HashMap<String, String> configMap = new HashMap<>();

    private void checkExist() {
        try {
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
        } catch (IOException e) {
            Logger.ERROR.Log(e.getMessage(), false);
        }
    }

    public HashMap<String, String> getConfigMap() {
        return configMap;
    }

    public void readConfig() {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(configFile))) {
            checkExist();
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.startsWith("//") || line.isEmpty()) {
                    continue;
                }
                splitLine = line.split("=");
                if (splitLine.length == 2) {
                    String key = splitLine[0].trim();
                    String value = splitLine[1].trim();
                    configMap.put(key, value);
                } else {
                    throw new IOException("Invalid config line: " + line);
                }
            }
        } catch (IOException e) {
            Logger.ERROR.Log("readConfig() Error: " + e.getMessage(), true);
        }
    }

    public void writeConfig(String... option) {
        if (option.length % 2 != 0) {
            Logger.ERROR.Log("Invalid number of arguments for writeConfig(String... option)", false);
        }
        try {
            checkExist();
            try (FileWriter writer = new FileWriter(configFile, false)) {
                for (int i = 0; i < option.length; i += 2) {
                    if (option[i + 1] == null || option[i + 1].isEmpty()) {
                        Logger.ERROR.LogSilently("Invalid value for key: " + option[i]);
                    } else {
                        String key = option[i].trim();
                        String value = option[i + 1].trim();
                        writer.write(key + "=" + value + "\n");
                    }
                }
            }
        } catch (IOException e) {
            Logger.ERROR.Log("writeConfig(String line) Error: " + e.getMessage(), false);
        }
    }
}