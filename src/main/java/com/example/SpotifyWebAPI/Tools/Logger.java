package com.example.SpotifyWebAPI.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    ;
    private final String severity;

    Logger(String severity) {
        this.severity = severity;
    }

    public void Log(String message) {
        Log(message, true);
    }

    public void Log(String message, boolean writetoFile) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fullMessage = now.format(formatter) + severity + message;
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    private void writeLog(String fullMessage) {
        try {
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fileName = "log_" + today.format(formatter);
            File logPath = new File("Logs");
            if (!logPath.exists()) {
                boolean createDir = logPath.mkdirs();
                if (!createDir) {
                    throw new IOException("Could not create log directory");
                }
            }
            File logFile = new File(logPath + File.separator + fileName + ".log");
            if (!logFile.exists()) {
                boolean createFile = logFile.createNewFile();
                if (!createFile) {
                    throw new IOException("Could not create log file");
                }
            }
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(fullMessage + "\n");
            }
        } catch (IOException e) {
            System.out.println("writeLog(String fullMessage) Error! Error: " + e.getMessage());
        }
    }
}