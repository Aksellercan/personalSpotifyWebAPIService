package com.example.SpotifyWebAPI.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public void writeConfig(String message) {
        try {
            File configPath = new File("Config");
            if (!configPath.exists()) {
                boolean createDir = configPath.mkdirs();
                if (!createDir) {
                    throw new IOException("Could not create log directory");
                }
            }
            File configFile = new File(configPath + File.separator + "config.txt");
            if (!configFile.exists()) {
                boolean createFile = configFile.createNewFile();
                if (!createFile) {
                    throw new IOException("Could not create log file");
                }
            }
            try (FileWriter writer = new FileWriter(configFile, true)) {
                writer.write(message + "\n");
            }
        } catch (IOException e) {
            System.out.println("writeLog(String message) Error! Error: " + e.getMessage());
        }
    }
}
